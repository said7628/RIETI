import fs from "node:fs";
import crypto from "node:crypto";
import mysql from "mysql2/promise";
import bcrypt from "bcryptjs";
import { S3Client, PutObjectCommand, GetObjectCommand, HeadObjectCommand } from "@aws-sdk/client-s3";
import { getSignedUrl } from "@aws-sdk/s3-request-presigner";

const ca = fs.readFileSync("./global-bundle.pem", "utf8");
const pool = mysql.createPool({
  host: process.env.DB_HOST,
  port: Number(process.env.DB_PORT || 3306),
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME || "rieti",
  ssl: { ca, rejectUnauthorized: true },
  waitForConnections: true,
  connectionLimit: 4,
  maxIdle: 4,
  idleTimeout: 60000,
  enableKeepAlive: true
});
const s3 = new S3Client({ region: process.env.AWS_REGION || "us-east-1" });

const STATUS_NAME_TO_CODE = new Map([
  ["Registrado","REGISTRADO"],["En revisiÃ³n","EN_REVISION"],["En seguimiento","EN_SEGUIMIENTO"],
  ["Canalizado","CANALIZADO"],["Concluido","CONCLUIDO"],["Archivado","ARCHIVADO"],
  ["Cancelado","CANCELADO"],["Reincidente","REINCIDENTE"]
])

const ALLOWED_STATUS_TRANSITIONS = new Map([
  ["REGISTRADO", new Set([
    "EN_REVISION",
    "REINCIDENTE",
    "CANCELADO",
  ])],

  ["REINCIDENTE", new Set([
    "EN_REVISION",
    "CANCELADO",
  ])],

  ["EN_REVISION", new Set([
    "EN_SEGUIMIENTO",
    "CANALIZADO",
    "CANCELADO",
  ])],

  ["EN_SEGUIMIENTO", new Set([
    "CANALIZADO",
    "CONCLUIDO",
    "CANCELADO",
  ])],

  ["CANALIZADO", new Set([
    "EN_SEGUIMIENTO",
    "CONCLUIDO",
    "CANCELADO",
  ])],

  ["CONCLUIDO", new Set([
    "ARCHIVADO",
  ])],

  ["ARCHIVADO", new Set([])],
  ["CANCELADO", new Set([])],
]);

function httpError(status, code, message, details) {
  const e = new Error(message); e.status=status; e.code=code; e.details=details; return e;
}
function response(statusCode, body) {
  return { statusCode, headers: {
    "content-type":"application/json; charset=utf-8",
    "cache-control":"no-store"
  }, body: JSON.stringify(body) };
}
function parseBody(event) {
  if (!event.body) return {};
  try {
    const raw = event.isBase64Encoded ? Buffer.from(event.body,"base64").toString("utf8") : event.body;
    return JSON.parse(raw);
  } catch { throw httpError(400,"INVALID_JSON","El cuerpo JSON no es vÃ¡lido"); }
}
function getRequest(event) {
  const method=(event?.requestContext?.http?.method || event.httpMethod || "GET").toUpperCase();
  const path=(event.rawPath || event.path || "/").replace(/\/+$/,"") || "/";
  const headers=Object.fromEntries(Object.entries(event.headers||{}).map(([k,v])=>[k.toLowerCase(),v]));
  return {method,path,headers,query:event.queryStringParameters||{},body:parseBody(event)};
}
function sha256(v){ return crypto.createHash("sha256").update(v).digest("hex"); }
function randomToken(n=32){ return crypto.randomBytes(n).toString("base64url"); }
function b64url(s){ return Buffer.from(s).toString("base64url"); }

function signAccessToken(user) {
  const secret=process.env.JWT_SECRET;
  if(!secret) throw new Error("JWT_SECRET is not configured");
  const now=Math.floor(Date.now()/1000), ttl=Number(process.env.JWT_TTL_SECONDS||900);
  const h=b64url(JSON.stringify({alg:"HS256",typ:"JWT"}));
  const p=b64url(JSON.stringify({sub:String(user.id),role:user.rol_codigo,admin:Boolean(user.es_administrador),municipioId:user.municipio_id??null,iat:now,exp:now+ttl}));
  const sig=crypto.createHmac("sha256",secret).update(`${h}.${p}`).digest("base64url");
  return `${h}.${p}.${sig}`;
}
function verifyAccessToken(token) {
  const secret=process.env.JWT_SECRET;
  if(!secret || !token) throw httpError(401,"UNAUTHORIZED","SesiÃ³n invÃ¡lida");
  const parts=token.split(".");
  if(parts.length!==3) throw httpError(401,"UNAUTHORIZED","SesiÃ³n invÃ¡lida");
  const [h,p,s]=parts, expected=crypto.createHmac("sha256",secret).update(`${h}.${p}`).digest("base64url");
  const a=Buffer.from(s), b=Buffer.from(expected);
  if(a.length!==b.length || !crypto.timingSafeEqual(a,b)) throw httpError(401,"UNAUTHORIZED","SesiÃ³n invÃ¡lida");
  const payload=JSON.parse(Buffer.from(p,"base64url").toString("utf8"));
  if(!payload.exp || payload.exp < Math.floor(Date.now()/1000)) throw httpError(401,"TOKEN_EXPIRED","La sesiÃ³n expirÃ³");
  return payload;
}
function bearer(headers){ const m=/^Bearer\s+(.+)$/i.exec(headers.authorization||""); return m?.[1]||null; }

async function loadUserById(id){
  const [rows]=await pool.execute(`SELECT u.id,u.uuid,u.nombre,u.correo,u.telefono,u.activo,u.municipio_id,u.dependencia_id,
    r.id rol_id,r.codigo rol_codigo,r.nombre rol_nombre,r.acceso_web,r.acceso_android,r.es_administrador
    FROM usuarios u JOIN roles r ON r.id=u.rol_id WHERE u.id=? LIMIT 1`,[id]);
  return rows[0]||null;
}
async function requireUser(req,{web=false,admin=false}={}){
  const payload=verifyAccessToken(bearer(req.headers));
  const user=await loadUserById(payload.sub);
  if(!user || !user.activo) throw httpError(401,"UNAUTHORIZED","Cuenta inactiva o inexistente");
  if(web && !user.acceso_web) throw httpError(403,"WEB_ACCESS_DENIED","Este perfil no tiene acceso web");
  if(admin && !user.es_administrador) throw httpError(403,"FORBIDDEN","Se requieren privilegios de administrador");
  return user;
}
function publicUser(u){ return {
  id:Number(u.id), uuid:u.uuid, name:u.nombre, email:u.correo, phone:u.telefono,
  role:u.rol_codigo,
  roleCode:u.rol_codigo,
  roleName:u.rol_nombre,
  municipalityId:u.municipio_id==null?null:Number(u.municipio_id),
  dependencyId:u.dependencia_id==null?null:Number(u.dependencia_id),
  active:Boolean(u.activo)
};}
async function createSession(user,req){
  const refresh=randomToken(48), hash=sha256(refresh), days=Number(process.env.REFRESH_TTL_DAYS||30);
  const device=String(req.headers["user-agent"]||"").slice(0,200)||null;
  const ip=req.headers["x-forwarded-for"]?.split(",")[0]?.trim()||null;
  await pool.execute(`INSERT INTO sesiones(usuario_id,refresh_token_hash,dispositivo,ip_hash,expira_at,created_at)
    VALUES(?,?,?,?,DATE_ADD(UTC_TIMESTAMP(6),INTERVAL ? DAY),UTC_TIMESTAMP(6))`,
    [user.id,hash,device,ip?sha256(ip):null,days]);
  return {accessToken:signAccessToken(user),refreshToken:refresh};
}
async function health(){
  const [rows]=await pool.query(`SELECT (SELECT COUNT(*) FROM roles) roles,
    (SELECT COUNT(*) FROM estatus_operacionales) estatus,
    (SELECT COUNT(*) FROM usuarios) usuarios,
    (SELECT COUNT(*) FROM reportes) reportes`);
  return response(200,{ok:true,service:"rieti-api",database:process.env.DB_NAME||"rieti",
    result:Object.fromEntries(Object.entries(rows[0]).map(([k,v])=>[k,Number(v)]))});
}
async function login(req){
  const email=String(req.body.email||"").trim().toLowerCase(), password=String(req.body.password||"");
  if(!email||!password) throw httpError(400,"MISSING_CREDENTIALS","Correo y contraseÃ±a son obligatorios");
  const [rows]=await pool.execute(`SELECT u.*,r.codigo rol_codigo,r.nombre rol_nombre,r.acceso_web,r.acceso_android,r.es_administrador
    FROM usuarios u JOIN roles r ON r.id=u.rol_id WHERE LOWER(u.correo)=? LIMIT 1`,[email]);
  const user=rows[0];
  if(!user||!user.activo||!user.acceso_web) throw httpError(401,"INVALID_CREDENTIALS","Correo o contraseÃ±a incorrectos");
  if(!(await bcrypt.compare(password,user.password_hash))) throw httpError(401,"INVALID_CREDENTIALS","Correo o contraseÃ±a incorrectos");
  await pool.execute(`UPDATE usuarios SET ultimo_acceso_at=UTC_TIMESTAMP(6),updated_at=UTC_TIMESTAMP(6) WHERE id=?`,[user.id]);
  return response(200,{ok:true,user:publicUser(user),...(await createSession(user,req))});
}
async function refreshSession(req){
  const token=String(req.body.refreshToken||""); if(!token) throw httpError(400,"MISSING_REFRESH_TOKEN","Falta refreshToken");
  const conn=await pool.getConnection();
  try{
    await conn.beginTransaction();
    const [ss]=await conn.execute(`SELECT * FROM sesiones WHERE refresh_token_hash=? AND revocada_at IS NULL AND expira_at>UTC_TIMESTAMP(6) LIMIT 1 FOR UPDATE`,[sha256(token)]);
    const s=ss[0]; if(!s) throw httpError(401,"INVALID_REFRESH_TOKEN","SesiÃ³n invÃ¡lida o vencida");
    const [us]=await conn.execute(`SELECT u.*,r.codigo rol_codigo,r.nombre rol_nombre,r.acceso_web,r.acceso_android,r.es_administrador
      FROM usuarios u JOIN roles r ON r.id=u.rol_id WHERE u.id=? LIMIT 1`,[s.usuario_id]);
    const user=us[0]; if(!user||!user.activo) throw httpError(401,"UNAUTHORIZED","Cuenta inactiva");
    await conn.execute(`UPDATE sesiones SET revocada_at=UTC_TIMESTAMP(6) WHERE id=?`,[s.id]);
    const next=randomToken(48), nextHash=sha256(next), days=Number(process.env.REFRESH_TTL_DAYS||30);
    await conn.execute(`INSERT INTO sesiones(usuario_id,refresh_token_hash,dispositivo,ip_hash,expira_at,created_at)
      VALUES(?,?,?,?,DATE_ADD(UTC_TIMESTAMP(6),INTERVAL ? DAY),UTC_TIMESTAMP(6))`,
      [user.id,nextHash,s.dispositivo,s.ip_hash,days]);
    await conn.commit();
    return response(200,{ok:true,accessToken:signAccessToken(user),refreshToken:next,user:publicUser(user)});
  }catch(e){await conn.rollback();throw e;}finally{conn.release();}
}
async function logout(req){
  const token=String(req.body.refreshToken||"");
  if(token) await pool.execute(`UPDATE sesiones SET revocada_at=UTC_TIMESTAMP(6) WHERE refresh_token_hash=? AND revocada_at IS NULL`,[sha256(token)]);
  return response(200,{ok:true});
}
async function me(req){ const u=await requireUser(req,{web:true}); return response(200,{ok:true,user:publicUser(u)}); }

async function bootstrapAdmin(req){
  if(!process.env.BOOTSTRAP_KEY || req.headers["x-bootstrap-key"]!==process.env.BOOTSTRAP_KEY) throw httpError(404,"NOT_FOUND","Ruta no encontrada");
  const [cnt]=await pool.query(`SELECT COUNT(*) n FROM usuarios u JOIN roles r ON r.id=u.rol_id WHERE r.es_administrador=1 AND r.acceso_web=1 AND u.activo=1`);
  if(Number(cnt[0].n)>0) throw httpError(409,"ADMIN_EXISTS","Ya existe un administrador web activo");
  const name=String(req.body.name||"").trim(), email=String(req.body.email||"").trim().toLowerCase(), password=String(req.body.password||"");
  if(!name||!email||password.length<10) throw httpError(400,"INVALID_INPUT","Nombre, correo y contraseÃ±a de al menos 10 caracteres son obligatorios");
  const roleCode=String(req.body.roleCode||"ADMINISTRADOR_FUNDACION").trim().toUpperCase();
  const [roles]=await pool.execute(`SELECT id FROM roles WHERE codigo=? AND es_administrador=1 AND acceso_web=1 AND activo=1 LIMIT 1`,[roleCode]);
  if(!roles[0]) throw httpError(500,"ADMIN_ROLE_MISSING","No existe el rol administrador web solicitado");
  const hash=await bcrypt.hash(password,12);
  await pool.execute(`INSERT INTO usuarios(uuid,rol_id,municipio_id,dependencia_id,nombre,correo,telefono,password_hash,activo,created_at,updated_at)
    VALUES(?,?,NULL,NULL,?,?,NULL,?,1,UTC_TIMESTAMP(6),UTC_TIMESTAMP(6))`,
    [crypto.randomUUID(),roles[0].id,name,email,hash]);
  return response(201,{ok:true,message:"Administrador creado"});
}
async function listCatalogs(req){
  await requireUser(req,{web:true});
  const names=["roles","municipios","colonias","semaforos","estatus_operacionales","prioridades","rangos_edad","tipos_trabajo_infantil","niveles_riesgo","sexos","dependencias"];
  const out={};
  for(const n of names){
    const sql=n==="roles"
      ? `SELECT * FROM roles WHERE activo=1 ORDER BY id`
      : `SELECT * FROM ${n}`
    const [rows]=await pool.query(sql)
    out[n]=rows
  }
  return response(200,{ok:true,catalogs:out});
}
async function listUsers(req){
  await requireUser(req,{web:true,admin:true});
  const [rows]=await pool.query(`SELECT u.id,u.uuid,u.nombre,u.correo,u.telefono,u.activo,u.municipio_id,u.dependencia_id,
    r.codigo rol_codigo,r.nombre rol_nombre,r.es_administrador FROM usuarios u JOIN roles r ON r.id=u.rol_id ORDER BY u.nombre`);
  return response(200,{ok:true,users:rows.map(publicUser)});
}
async function createUser(req){
  await requireUser(req,{web:true,admin:true});
  const {name,email,password,roleId,municipalityId=null,dependencyId=null,phone=null}=req.body;
  if(!name||!email||!password||!roleId) throw httpError(400,"INVALID_INPUT","Faltan datos obligatorios");
  if(String(password).length<10) throw httpError(400,"WEAK_PASSWORD","La contraseÃ±a debe tener al menos 10 caracteres");
  try{
    await pool.execute(`INSERT INTO usuarios(uuid,rol_id,municipio_id,dependencia_id,nombre,correo,telefono,password_hash,activo,created_at,updated_at)
      VALUES(?,?,?,?,?,?,?,?,1,UTC_TIMESTAMP(6),UTC_TIMESTAMP(6))`,
      [crypto.randomUUID(),roleId,municipalityId,dependencyId,String(name).trim(),String(email).trim().toLowerCase(),phone,await bcrypt.hash(String(password),12)]);
  }catch(e){ if(e.code==="ER_DUP_ENTRY") throw httpError(409,"EMAIL_EXISTS","Ese correo ya estÃ¡ registrado"); throw e; }
  return response(201,{ok:true});
}
async function updateUser(req,id){
  await requireUser(req,{web:true,admin:true});
  const allowed={name:"nombre",roleId:"rol_id",municipalityId:"municipio_id",dependencyId:"dependencia_id",phone:"telefono",active:"activo"};
  const sets=[],params=[];
  for(const [k,c] of Object.entries(allowed)) if(Object.hasOwn(req.body,k)){sets.push(`${c}=?`);params.push(req.body[k]);}
  if(req.body.password){if(String(req.body.password).length<10)throw httpError(400,"WEAK_PASSWORD","La contraseÃ±a debe tener al menos 10 caracteres");sets.push("password_hash=?");params.push(await bcrypt.hash(String(req.body.password),12));}
  if(!sets.length) throw httpError(400,"NO_CHANGES","No hay cambios");
  sets.push("updated_at=UTC_TIMESTAMP(6)");params.push(id);
  await pool.execute(`UPDATE usuarios SET ${sets.join(",")} WHERE id=?`,params);
  return response(200,{ok:true});
}
async function deactivateUser(req,id){
  const actor=await requireUser(req,{web:true,admin:true});
  if(Number(actor.id)===Number(id)) throw httpError(400,"SELF_DEACTIVATE","No puedes desactivar tu propia cuenta");
  await pool.execute(`UPDATE usuarios SET activo=0,updated_at=UTC_TIMESTAMP(6) WHERE id=?`,[id]);
  await pool.execute(`UPDATE sesiones SET revocada_at=UTC_TIMESTAMP(6) WHERE usuario_id=? AND revocada_at IS NULL`,[id]);
  return response(200,{ok:true});
}

const REPORT_SELECT=`SELECT r.id,r.uuid,r.folio,r.municipio_id,r.colonia_id,r.modalidad,r.origen,r.estimacion_nna,
 r.descripcion,r.latitud,r.longitud,r.ubicacion_origen,r.fecha_hora_observacion_utc,r.estatus_id,r.prioridad_id,
 r.asignado_usuario_id,r.asignado_dependencia_id,r.descripcion_solucion,r.cerrado_at,r.created_at,r.updated_at,
 r.direccion_referencia,r.codigo_postal,r.horario_observado,m.nombre municipio,c.nombre colonia,t.nombre work_type,
 nr.nombre nivel_riesgo,re.nombre age_range,eo.nombre status,eo.codigo status_code,p.nombre priority,p.codigo priority_code,
 au.nombre case_holder FROM reportes r JOIN municipios m ON m.id=r.municipio_id LEFT JOIN colonias c ON c.id=r.colonia_id
 JOIN tipos_trabajo_infantil t ON t.id=r.tipo_trabajo_id JOIN niveles_riesgo nr ON nr.id=r.nivel_riesgo_id
 JOIN rangos_edad re ON re.id=r.rango_edad_id JOIN estatus_operacionales eo ON eo.id=r.estatus_id
 JOIN prioridades p ON p.id=r.prioridad_id LEFT JOIN usuarios au ON au.id=r.asignado_usuario_id`;
function mapReport(r){return{
  id:Number(r.id),uuid:r.uuid,folio:r.folio,createdAt:r.created_at,updatedAt:r.updated_at,workType:r.work_type,
  colonia:r.colonia||r.municipio,municipality:r.municipio,address:[r.direccion_referencia,r.colonia,r.municipio,r.codigo_postal].filter(Boolean).join(", "),
  postalCode:r.codigo_postal,status:r.status,statusCode:r.status_code,priority:r.priority_code||r.priority,caseHolder:r.case_holder||null,
  nnaEstimate:r.estimacion_nna==="5_MAS"?"5+":r.estimacion_nna,ageRange:r.age_range,schedule:r.horario_observado||"â€”",description:r.descripcion,solution:r.descripcion_solucion||null,
  latitude:Number(r.latitud),longitude:Number(r.longitud),risk:r.nivel_riesgo,modality:r.modalidad,origin:r.origen
};}
async function writeAudit(conn,{
  userId=null,
  municipalityId=null,
  action,
  entityType,
  entityId=null,
  metadata=null
}){
  await conn.execute(
    `INSERT INTO auditoria(
      usuario_id,
      municipio_id,
      accion,
      entidad_tipo,
      entidad_id,
      request_id,
      ip_hash,
      metadata_segura,
      created_at
    )
    VALUES(?,?,?,?,?,NULL,NULL,?,UTC_TIMESTAMP(6))`,
    [
      userId,
      municipalityId,
      action,
      entityType,
      entityId==null ? null : String(entityId),
      metadata==null ? null : JSON.stringify(metadata)
    ]
  )
}

const ROLE_PERMISSIONS = {
  ADMINISTRADOR_FUNDACION: new Set([
    "REPORT_VIEW",
    "REPORT_CREATE",
    "REPORT_UPDATE_STATUS",
    "REPORT_UPDATE_PRIORITY",
    "REPORT_ASSIGN",
    "REPORT_NOTE",
    "EVIDENCE_VIEW",
    "EVIDENCE_UPLOAD",
    "USER_ADMIN",
    "MAP_VIEW"
  ]),

  GESTOR_MUNICIPAL: new Set([
    "REPORT_VIEW",
    "REPORT_CREATE",
    "REPORT_UPDATE_STATUS",
    "REPORT_UPDATE_PRIORITY",
    "REPORT_ASSIGN",
    "REPORT_NOTE",
    "EVIDENCE_VIEW",
    "EVIDENCE_UPLOAD",
    "MAP_VIEW"
  ]),

  PROCURADOR: new Set([
    "REPORT_VIEW",
    "REPORT_UPDATE_STATUS",
    "REPORT_UPDATE_PRIORITY",
    "REPORT_NOTE",
    "EVIDENCE_VIEW",
    "EVIDENCE_UPLOAD",
    "MAP_VIEW"
  ])
}

function requirePermission(user,permission){
  const role=user.rol_codigo

  if(!role){
    throw httpError(
      403,
      "ROLE_REQUIRED",
      "El usuario no tiene rol vÃ¡lido"
    )
  }

  const permissions=ROLE_PERMISSIONS[role]

  if(!permissions || !permissions.has(permission)){
    throw httpError(
      403,
      "FORBIDDEN",
      "No tienes permiso para realizar esta acciÃ³n"
    )
  }
}

function reportAccess(user,alias="r"){if(user.es_administrador)return{sql:"",params:[]};if(!user.municipio_id)throw httpError(403,"MUNICIPALITY_REQUIRED","El usuario no tiene municipio asignado");return{sql:` AND ${alias}.municipio_id=?`,params:[user.municipio_id]};}
async function listReports(req){const u=await requireUser(req,{web:true});requirePermission(u,"REPORT_VIEW");const a=reportAccess(u);const[rows]=await pool.execute(`${REPORT_SELECT} WHERE 1=1 ${a.sql} ORDER BY r.created_at DESC LIMIT 1000`,a.params);return response(200,{ok:true,reports:rows.map(mapReport)});}
async function getReport(req,key){const u=await requireUser(req,{web:true});requirePermission(u,"REPORT_VIEW");const a=reportAccess(u);const[rows]=await pool.execute(`${REPORT_SELECT} WHERE (r.id=? OR r.folio=?) ${a.sql} LIMIT 1`,[key,key,...a.params]);if(!rows[0])throw httpError(404,"REPORT_NOT_FOUND","Reporte no encontrado");const[notes]=await pool.execute(`SELECT n.id,n.nota,n.created_at,u.nombre author FROM reporte_notas n JOIN usuarios u ON u.id=n.autor_usuario_id WHERE n.reporte_id=? ORDER BY n.created_at`,[rows[0].id]);return response(200,{ok:true,report:mapReport(rows[0]),notes});}
async function lookupId(table,value,field="nombre"){if(value==null||value==="")return null;if(/^\d+$/.test(String(value)))return Number(value);const[rows]=await pool.execute(`SELECT id FROM ${table} WHERE LOWER(${field})=LOWER(?) LIMIT 1`,[String(value).trim()]);return rows[0]?.id??null;}

async function createReport(req){
  const u=await requireUser(req,{web:true}),b=req.body;
  requirePermission(u,"REPORT_CREATE");
  const municipalityId=u.es_administrador?await lookupId("municipios",b.municipalityId??b.municipality):u.municipio_id;
  const colonyId=await lookupId("colonias",b.colonyId??b.neighborhood);
  let ageValue=b.ageRangeId??b.ageRange;
  if(!ageValue && b.ages!==undefined){
    const age=Number.parseInt(String(b.ages),10);
    if(Number.isFinite(age)) ageValue=age<=5?"0-5":age<=11?"6-11":age<=14?"12-14":"15-17";
  }
  const ageId=await lookupId("rangos_edad",ageValue);
  const workId=await lookupId("tipos_trabajo_infantil",b.workTypeId??b.workType);
  let riskId=await lookupId("niveles_riesgo",b.riskId??b.risk);
  if(!riskId) riskId=await lookupId("niveles_riesgo","MEDIO","codigo") || await lookupId("niveles_riesgo","Medio");
  const[st]=await pool.query(`SELECT id FROM estatus_operacionales WHERE codigo='REGISTRADO' OR LOWER(nombre)='registrado' ORDER BY id LIMIT 1`);
  const[pr]=await pool.query(`SELECT id FROM prioridades WHERE codigo='MEDIA' OR LOWER(nombre)='media' ORDER BY id LIMIT 1`);
  if(!municipalityId||!ageId||!workId||!riskId||!st[0]||!pr[0]) throw httpError(400,"CATALOG_VALUE_MISSING","No se pudieron resolver catÃ¡logos requeridos");
  const lat=Number(b.latitude),lon=Number(b.longitude); if(!Number.isFinite(lat)||!Number.isFinite(lon))throw httpError(400,"LOCATION_REQUIRED","Latitud y longitud son obligatorias");
  const raw=String(b.children||b.nnaEstimate||"1"), nna=Number(raw)>=5?"5_MAS":(["1","2","3","4"].includes(raw)?raw:"1");
  const conn=await pool.getConnection();
  try{
    await conn.beginTransaction();
    const year=new Date().getUTCFullYear();
    const [[seq0]]=await conn.query(`SELECT ultimo_consecutivo FROM folio_secuencias WHERE municipio_id=? AND anio=? FOR UPDATE`,[municipalityId,year]);
    let seq;
    if(!seq0){seq=1;await conn.execute(`INSERT INTO folio_secuencias(municipio_id,anio,ultimo_consecutivo,updated_at) VALUES(?,?,1,UTC_TIMESTAMP(6))`,[municipalityId,year]);}
    else{seq=Number(seq0.ultimo_consecutivo)+1;await conn.execute(`UPDATE folio_secuencias SET ultimo_consecutivo=?,updated_at=UTC_TIMESTAMP(6) WHERE municipio_id=? AND anio=?`,[seq,municipalityId,year]);}
    const[[mun]]=await conn.query(`SELECT clave FROM municipios WHERE id=?`,[municipalityId]); const folio=`${mun.clave}-${year}-${String(seq).padStart(6,"0")}`;
    const[result]=await conn.execute(`INSERT INTO reportes(uuid,folio,municipio_id,colonia_id,modalidad,creador_usuario_id,origen,client_report_uuid,
      estimacion_nna,rango_edad_id,sexo_id,tipo_trabajo_id,nivel_riesgo_id,descripcion,latitud,longitud,ubicacion_origen,consentimiento_ubicacion_at,
      fecha_hora_observacion_utc,estatus_id,prioridad_id,asignado_usuario_id,asignado_dependencia_id,descripcion_solucion,cerrado_at,creado_por_usuario_id,
      actualizado_por_usuario_id,created_at,updated_at,direccion_referencia,codigo_postal,horario_observado)
      VALUES(?,?,?,?,'SEGUIMIENTO',?,'WEB',?,?,?,?,?,?,?,?,?,'MAPA',UTC_TIMESTAMP(6),?,?,?,NULL,NULL,NULL,NULL,?,?,UTC_TIMESTAMP(6),UTC_TIMESTAMP(6),?,?,?)`,
      [crypto.randomUUID(),folio,municipalityId,colonyId,u.id,b.clientReportUuid||null,nna,ageId,b.sexId||null,workId,riskId,String(b.details||b.description||"").trim(),
       lat,lon,b.observedAt||null,st[0].id,pr[0].id,u.id,u.id,String(b.address||"").trim()||null,String(b.postalCode||"").trim()||null,String(b.schedule||"").trim()||null]);
    await conn.commit(); return response(201,{ok:true,id:Number(result.insertId),folio});
  }catch(e){await conn.rollback();throw e;}finally{conn.release();}
}
async function updateReport(req,key){
  const u=await requireUser(req,{web:true})

  if(Object.hasOwn(req.body,"priority")){
    requirePermission(u,"REPORT_UPDATE_PRIORITY")
  }

  if(
    Object.hasOwn(req.body,"status") ||
    Object.hasOwn(req.body,"solution")
  ){
    requirePermission(u,"REPORT_UPDATE_STATUS")
  }

  const a=reportAccess(u)
  const c=await pool.getConnection()

  try{
    await c.beginTransaction()

    const[rows]=await c.execute(
      `SELECT
         r.*,
         eo.codigo AS estatus_codigo,
         p.codigo AS prioridad_codigo
       FROM reportes r
       JOIN estatus_operacionales eo
         ON eo.id=r.estatus_id
       JOIN prioridades p
         ON p.id=r.prioridad_id
       WHERE (r.id=? OR r.folio=?)
         ${a.sql}
       LIMIT 1
       FOR UPDATE`,
      [key,key,...a.params]
    )

    const r=rows[0]

    if(!r){
      throw httpError(
        404,
        "REPORT_NOT_FOUND",
        "Reporte no encontrado"
      )
    }

    const sets=[]
    const params=[]

    let newStatus=null
    let newPriorityId=null
    let newPriorityCode=null

    if(req.body.priority){
      const pid=
        await lookupId(
          "prioridades",
          req.body.priority,
          "codigo"
        ) ||
        await lookupId(
          "prioridades",
          req.body.priority
        )

      if(!pid){
        throw httpError(
          400,
          "INVALID_PRIORITY",
          "Prioridad invÃ¡lida"
        )
      }

      const[priorityRows]=await c.execute(
        `SELECT id,codigo
         FROM prioridades
         WHERE id=?
         LIMIT 1`,
        [pid]
      )

      newPriorityId=priorityRows[0].id
      newPriorityCode=priorityRows[0].codigo

      sets.push("prioridad_id=?")
      params.push(newPriorityId)
    }

    if(req.body.status){
      const code=
        STATUS_NAME_TO_CODE.get(req.body.status) ||
        String(req.body.status).toUpperCase()

      const[st]=await c.execute(
        `SELECT id,codigo,nombre
         FROM estatus_operacionales
         WHERE codigo=?
            OR LOWER(nombre)=LOWER(?)
         LIMIT 1`,
        [code,req.body.status]
      )

      if(!st[0]){
        throw httpError(
          400,
          "INVALID_STATUS",
          "Estatus invÃ¡lido"
        )
      }

      newStatus=st[0]

      const currentStatus=r.estatus_codigo
      const nextStatus=newStatus.codigo

      if(nextStatus!==currentStatus){
        const allowed=
          ALLOWED_STATUS_TRANSITIONS.get(currentStatus)

        if(!allowed || !allowed.has(nextStatus)){
          throw httpError(
            409,
            "INVALID_STATUS_TRANSITION",
            `No se permite cambiar de ${currentStatus} a ${nextStatus}`
          )
        }
      }

      const solution=String(
        req.body.solution ||
        r.descripcion_solucion ||
        ""
      ).trim()

      if(
        ["CONCLUIDO","ARCHIVADO"].includes(nextStatus) &&
        !solution
      ){
        throw httpError(
          400,
          "SOLUTION_REQUIRED",
          "Debes registrar la soluciÃ³n"
        )
      }

      sets.push("estatus_id=?")
      params.push(newStatus.id)

      if(Object.hasOwn(req.body,"solution")){
        sets.push("descripcion_solucion=?")
        params.push(req.body.solution||null)
      }

      if(nextStatus==="CONCLUIDO"){
        sets.push("cerrado_at=UTC_TIMESTAMP(6)")
      }
    }else if(Object.hasOwn(req.body,"solution")){
      sets.push("descripcion_solucion=?")
      params.push(req.body.solution||null)
    }

    if(!sets.length){
      throw httpError(
        400,
        "NO_CHANGES",
        "No hay cambios"
      )
    }

    sets.push(
      "actualizado_por_usuario_id=?",
      "updated_at=UTC_TIMESTAMP(6)"
    )

    params.push(
      u.id,
      r.id
    )

    await c.execute(
      `UPDATE reportes
       SET ${sets.join(",")}
       WHERE id=?`,
      params
    )

    if(
      newStatus &&
      Number(newStatus.id)!==Number(r.estatus_id)
    ){
      const solutionSnapshot=
        Object.hasOwn(req.body,"solution")
          ? (req.body.solution||null)
          : (r.descripcion_solucion||null)

      await c.execute(
        `INSERT INTO reporte_estatus_historial(
          reporte_id,
          estatus_anterior_id,
          estatus_nuevo_id,
          cambiado_por_id,
          solucion_snapshot,
          created_at
        )
        VALUES(?,?,?,?,?,UTC_TIMESTAMP(6))`,
        [
          r.id,
          r.estatus_id,
          newStatus.id,
          u.id,
          solutionSnapshot
        ]
      )

      await writeAudit(c,{
        userId:u.id,
        municipalityId:r.municipio_id,
        action:"REPORT_STATUS_CHANGED",
        entityType:"REPORTE",
        entityId:r.id,
        metadata:{
          folio:r.folio,
          from:r.estatus_codigo,
          to:newStatus.codigo
        }
      })
    }

    if(
      newPriorityId &&
      Number(newPriorityId)!==Number(r.prioridad_id)
    ){
      await writeAudit(c,{
        userId:u.id,
        municipalityId:r.municipio_id,
        action:"REPORT_PRIORITY_CHANGED",
        entityType:"REPORTE",
        entityId:r.id,
        metadata:{
          folio:r.folio,
          from:r.prioridad_codigo,
          to:newPriorityCode
        }
      })
    }

    await c.commit()

    return response(200,{ok:true})
  }catch(e){
    await c.rollback()
    throw e
  }finally{
    c.release()
  }
}
async function addNote(req,key){const u=await requireUser(req,{web:true});requirePermission(u,"REPORT_NOTE");const a=reportAccess(u);const[rows]=await pool.execute(`SELECT r.id FROM reportes r WHERE (r.id=? OR r.folio=?) ${a.sql} LIMIT 1`,[key,key,...a.params]);if(!rows[0])throw httpError(404,"REPORT_NOT_FOUND","Reporte no encontrado");const note=String(req.body.note||"").trim();if(!note)throw httpError(400,"NOTE_REQUIRED","La nota es obligatoria");await pool.execute(`INSERT INTO reporte_notas(reporte_id,autor_usuario_id,nota,created_at) VALUES(?,?,?,UTC_TIMESTAMP(6))`,[rows[0].id,u.id,note]);return response(201,{ok:true});}
async function assignReport(req,key){
  const u=await requireUser(req,{web:true})
  requirePermission(u,"REPORT_ASSIGN")
  const a=reportAccess(u)

  const[rows]=await pool.execute(
    `SELECT
       r.id,
       r.folio,
       r.municipio_id,
       r.asignado_usuario_id,
       r.asignado_dependencia_id,
       eo.codigo AS estatus_codigo
     FROM reportes r
     JOIN estatus_operacionales eo
       ON eo.id=r.estatus_id
     WHERE (r.id=? OR r.folio=?)
       ${a.sql}
     LIMIT 1`,
    [key,key,...a.params]
  )

  if(!rows[0]){
    throw httpError(
      404,
      "REPORT_NOT_FOUND",
      "Reporte no encontrado"
    )
  }

  const report=rows[0]
  const assignee=req.body.userId||null
  const dep=req.body.dependencyId||null

  if(!assignee&&!dep){
    throw httpError(
      400,
      "ASSIGNEE_REQUIRED",
      "Selecciona usuario o dependencia"
    )
  }

  const c=await pool.getConnection()

  try{
    await c.beginTransaction()

    if(assignee){
      const[users]=await c.execute(
        `SELECT
           u.id,
           u.activo,
           u.municipio_id,
           r.codigo AS rol_codigo,
           r.acceso_web
         FROM usuarios u
         JOIN roles r ON r.id=u.rol_id
         WHERE u.id=?
         LIMIT 1`,
        [assignee]
      )

      const assignedUser=users[0]

      if(!assignedUser){
        throw httpError(
          400,
          "INVALID_ASSIGNEE",
          "El usuario seleccionado no existe"
        )
      }

      if(!assignedUser.activo){
        throw httpError(
          400,
          "ASSIGNEE_INACTIVE",
          "No puedes asignar un reporte a una cuenta inactiva"
        )
      }

      if(!assignedUser.acceso_web){
        throw httpError(
          400,
          "ASSIGNEE_ROLE_NOT_ALLOWED",
          "Ese tipo de usuario no puede recibir asignaciones administrativas"
        )
      }

      if(
        assignedUser.municipio_id!==null &&
        Number(assignedUser.municipio_id)!==Number(report.municipio_id)
      ){
        throw httpError(
          403,
          "ASSIGNEE_MUNICIPALITY_MISMATCH",
          "No puedes asignar este reporte a un usuario de otro municipio"
        )
      }
    }

    if(dep){
      const[dependencies]=await c.execute(
        `SELECT id,municipio_id,activo
         FROM dependencias
         WHERE id=?
         LIMIT 1`,
        [dep]
      )

      const dependency=dependencies[0]

      if(!dependency){
        throw httpError(
          400,
          "INVALID_DEPENDENCY",
          "La dependencia seleccionada no existe"
        )
      }

      if(!dependency.activo){
        throw httpError(
          400,
          "DEPENDENCY_INACTIVE",
          "La dependencia seleccionada estÃ¡ inactiva"
        )
      }

      if(
        dependency.municipio_id!==null &&
        Number(dependency.municipio_id)!==Number(report.municipio_id)
      ){
        throw httpError(
          403,
          "DEPENDENCY_MUNICIPALITY_MISMATCH",
          "La dependencia no pertenece al municipio del reporte"
        )
      }
    }

    await c.execute(
      `UPDATE reporte_asignaciones
       SET finalizado_at=UTC_TIMESTAMP(6)
       WHERE reporte_id=?
         AND finalizado_at IS NULL`,
      [report.id]
    )

    await c.execute(
      `INSERT INTO reporte_asignaciones(
         reporte_id,
         usuario_asignado_id,
         dependencia_asignada_id,
         asignado_por_id,
         motivo,
         asignado_at
       )
       VALUES(?,?,?,?,?,UTC_TIMESTAMP(6))`,
      [
        report.id,
        assignee,
        dep,
        u.id,
        req.body.reason||null
      ]
    )

    let nextStatusId=null

    if(report.estatus_codigo==="REGISTRADO"){
      const[statuses]=await c.execute(
        `SELECT id
         FROM estatus_operacionales
         WHERE codigo='EN_REVISION'
         LIMIT 1`
      )

      if(!statuses[0]){
        throw new Error(
          "No existe el estatus EN_REVISION"
        )
      }

      nextStatusId=statuses[0].id
    }

    await c.execute(
      `UPDATE reportes
       SET asignado_usuario_id=?,
           asignado_dependencia_id=?,
           estatus_id=COALESCE(?,estatus_id),
           actualizado_por_usuario_id=?,
           updated_at=UTC_TIMESTAMP(6)
       WHERE id=?`,
      [
        assignee,
        dep,
        nextStatusId,
        u.id,
        report.id
      ]
    )

    if(nextStatusId){
      await c.execute(
        `INSERT INTO reporte_estatus_historial(
          reporte_id,
          estatus_anterior_id,
          estatus_nuevo_id,
          cambiado_por_id,
          solucion_snapshot,
          created_at
        )
        SELECT
          ?,
          eo_old.id,
          ?,
          ?,
          NULL,
          UTC_TIMESTAMP(6)
        FROM estatus_operacionales eo_old
        WHERE eo_old.codigo=?`,
        [
          report.id,
          nextStatusId,
          u.id,
          report.estatus_codigo
        ]
      )

      await writeAudit(c,{
        userId:u.id,
        municipalityId:report.municipio_id,
        action:"REPORT_STATUS_CHANGED",
        entityType:"REPORTE",
        entityId:report.id,
        metadata:{
          folio:report.folio,
          from:report.estatus_codigo,
          to:"EN_REVISION",
          reason:"ASSIGNMENT"
        }
      })
    }

    await writeAudit(c,{
      userId:u.id,
      municipalityId:report.municipio_id,
      action:"REPORT_ASSIGNED",
      entityType:"REPORTE",
      entityId:report.id,
      metadata:{
        folio:report.folio,
        previousUserId:report.asignado_usuario_id,
        newUserId:assignee,
        previousDependencyId:report.asignado_dependencia_id,
        newDependencyId:dep
      }
    })

    await c.commit()
  }catch(e){
    await c.rollback()
    throw e
  }finally{
    c.release()
  }

  return response(201,{ok:true})
}
async function recoveryRequest(req){
  const email=String(req.body.email||"")
    .trim()
    .toLowerCase()

  if(!email){
    throw httpError(
      400,
      "EMAIL_REQUIRED",
      "El correo es obligatorio"
    )
  }

  const[rows]=await pool.execute(
    `SELECT id,nombre,correo
     FROM usuarios
     WHERE LOWER(correo)=?
       AND activo=1
     LIMIT 1`,
    [email]
  )

  // Siempre respondemos lo mismo para no revelar
  // si una cuenta existe o no.
  if(!rows[0]){
    return response(200,{
      ok:true,
      message:"Si existe una cuenta activa con ese correo, recibirÃ¡s un cÃ³digo de recuperaciÃ³n."
    })
  }

  const user=rows[0]

  const code=String(
    crypto.randomInt(100000,1000000)
  )

  await pool.execute(
    `UPDATE recuperaciones_password
     SET usado_at=UTC_TIMESTAMP(6)
     WHERE usuario_id=?
       AND usado_at IS NULL`,
    [user.id]
  )

  await pool.execute(
    `INSERT INTO recuperaciones_password(
       usuario_id,
       token_hash,
       expira_at,
       created_at
     )
     VALUES(
       ?,
       ?,
       DATE_ADD(UTC_TIMESTAMP(6),INTERVAL 15 MINUTE),
       UTC_TIMESTAMP(6)
     )`,
    [
      user.id,
      sha256(code)
    ]
  )

  const resendKey=process.env.RESEND_API_KEY
  const emailFrom=
    process.env.EMAIL_FROM ||
    "RIETI <no-reply@saidvalseca.com>"

  const replyTo=
    process.env.EMAIL_REPLY_TO ||
    "contact@saidvalseca.com"

  if(!resendKey){
    throw new Error(
      "RESEND_API_KEY is not configured"
    )
  }

  const resendResponse=await fetch(
    "https://api.resend.com/emails",
    {
      method:"POST",
      headers:{
        "Authorization":`Bearer ${resendKey}`,
        "Content-Type":"application/json"
      },
      body:JSON.stringify({
        from:emailFrom,
        to:[user.correo],
        reply_to:replyTo,
        subject:"CÃ³digo para recuperar tu contraseÃ±a de RIETI",
        html:`
          <div style="
            font-family:Arial,Helvetica,sans-serif;
            max-width:600px;
            margin:0 auto;
            padding:32px;
            color:#111;
          ">
            <div style="
              background:#000;
              color:#fff;
              border-radius:16px 16px 0 0;
              padding:24px 28px;
            ">
              <h1 style="
                margin:0;
                font-family:Georgia,serif;
                font-weight:400;
              ">
                RIETI
              </h1>
            </div>

            <div style="
              border:1px solid #eee;
              border-top:none;
              border-radius:0 0 16px 16px;
              padding:30px 28px;
            ">
              <h2 style="margin-top:0">
                RecuperaciÃ³n de contraseÃ±a
              </h2>

              <p>
                Hola ${String(user.nombre||"").replace(/[<>&"]/g,"")},
              </p>

              <p>
                Recibimos una solicitud para cambiar
                la contraseÃ±a de tu cuenta en RIETI.
              </p>

              <p>
                Usa el siguiente cÃ³digo:
              </p>

              <div style="
                margin:26px 0;
                padding:18px;
                border-radius:12px;
                background:#f5f5f5;
                text-align:center;
                font-size:34px;
                font-weight:700;
                letter-spacing:8px;
              ">
                ${code}
              </div>

              <p>
                El cÃ³digo vence en <strong>15 minutos</strong>.
              </p>

              <p style="
                color:#666;
                font-size:13px;
              ">
                Si tÃº no solicitaste este cambio,
                puedes ignorar este mensaje.
              </p>
            </div>
          </div>
        `
      })
    }
  )

  const resendBody=
    await resendResponse.text()

  if(!resendResponse.ok){
    console.error(
      "RESEND_RECOVERY_ERROR",
      {
        status:resendResponse.status,
        body:resendBody
      }
    )

    throw httpError(
      502,
      "EMAIL_SEND_FAILED",
      "No fue posible enviar el correo de recuperaciÃ³n"
    )
  }

  return response(200,{
    ok:true,
    message:"Si existe una cuenta activa con ese correo, recibirÃ¡s un cÃ³digo de recuperaciÃ³n."
  })
}

async function recoveryReset(req){const email=String(req.body.email||"").trim().toLowerCase(),code=String(req.body.code||"").trim(),password=String(req.body.password||"");if(password.length<10)throw httpError(400,"WEAK_PASSWORD","La contraseÃ±a debe tener al menos 10 caracteres");const[rows]=await pool.execute(`SELECT rp.id,rp.usuario_id FROM recuperaciones_password rp JOIN usuarios u ON u.id=rp.usuario_id WHERE LOWER(u.correo)=? AND rp.token_hash=? AND rp.usado_at IS NULL AND rp.expira_at>UTC_TIMESTAMP(6) ORDER BY rp.id DESC LIMIT 1`,[email,sha256(code)]);if(!rows[0])throw httpError(400,"INVALID_RECOVERY_CODE","CÃ³digo invÃ¡lido o vencido");const c=await pool.getConnection();try{await c.beginTransaction();await c.execute(`UPDATE usuarios SET password_hash=?,updated_at=UTC_TIMESTAMP(6) WHERE id=?`,[await bcrypt.hash(password,12),rows[0].usuario_id]);await c.execute(`UPDATE recuperaciones_password SET usado_at=UTC_TIMESTAMP(6) WHERE id=?`,[rows[0].id]);await c.execute(`UPDATE sesiones SET revocada_at=UTC_TIMESTAMP(6) WHERE usuario_id=? AND revocada_at IS NULL`,[rows[0].usuario_id]);await c.commit();}catch(e){await c.rollback();throw e;}finally{c.release();}return response(200,{ok:true});}


async function presignEvidence(req){
  const u=await requireUser(req,{web:true})
  requirePermission(u,"EVIDENCE_UPLOAD")

  const {
    reportId,
    mimeType,
    sizeBytes,
    sha256Hex
  }=req.body

  if(!reportId || !mimeType || !sizeBytes || !sha256Hex){
    throw httpError(
      400,
      "INVALID_INPUT",
      "Faltan datos de evidencia"
    )
  }

  const normalizedMime=String(mimeType).trim().toLowerCase()
  const normalizedHash=String(sha256Hex).trim().toLowerCase()
  const normalizedSize=Number(sizeBytes)

  if(
    !Number.isSafeInteger(normalizedSize) ||
    normalizedSize<=0
  ){
    throw httpError(
      400,
      "INVALID_FILE_SIZE",
      "El tamaÃ±o del archivo es invÃ¡lido"
    )
  }

  if(normalizedSize>10*1024*1024){
    throw httpError(
      413,
      "FILE_TOO_LARGE",
      "MÃ¡ximo 10 MB"
    )
  }

  if(!["image/jpeg","image/png","image/webp"].includes(normalizedMime)){
    throw httpError(
      400,
      "INVALID_MIME",
      "Solo JPEG, PNG o WEBP"
    )
  }

  if(!/^[a-f0-9]{64}$/.test(normalizedHash)){
    throw httpError(
      400,
      "INVALID_SHA256",
      "SHA-256 invÃ¡lido"
    )
  }

  const a=reportAccess(u)

  const[rows]=await pool.execute(
    `SELECT
       r.id,
       r.uuid
     FROM reportes r
     WHERE (r.id=? OR r.folio=?)
       ${a.sql}
     LIMIT 1`,
    [reportId,reportId,...a.params]
  )

  if(!rows[0]){
    throw httpError(
      404,
      "REPORT_NOT_FOUND",
      "Reporte no encontrado"
    )
  }

  const evidenceUuid=crypto.randomUUID()

  const ext={
    "image/jpeg":"jpg",
    "image/png":"png",
    "image/webp":"webp"
  }[normalizedMime]

  const objectKey=
    `reportes/${rows[0].uuid}/evidencias/${evidenceUuid}.${ext}`

  const bucket=process.env.S3_BUCKET

  if(!bucket){
    throw new Error("S3_BUCKET is not configured")
  }

  const uploadUrl=await getSignedUrl(
    s3,
    new PutObjectCommand({
      Bucket:bucket,
      Key:objectKey,
      ContentType:normalizedMime,
      Metadata:{
        sha256:normalizedHash
      }
    }),
    {expiresIn:300}
  )

  return response(200,{
    ok:true,
    evidenceUuid,
    objectKey,
    uploadUrl,
    expiresIn:300
  })
}

async function confirmEvidence(req){
  const u=await requireUser(req,{web:true})
  requirePermission(u,"EVIDENCE_UPLOAD")

  const b=req.body

  if(
    !b.reportId ||
    !b.evidenceUuid ||
    !b.objectKey ||
    !b.mimeType ||
    !b.sizeBytes ||
    !b.sha256Hex
  ){
    throw httpError(
      400,
      "INVALID_INPUT",
      "Faltan datos para confirmar la evidencia"
    )
  }

  const evidenceUuid=String(b.evidenceUuid).trim()
  const normalizedMime=String(b.mimeType).trim().toLowerCase()
  const normalizedHash=String(b.sha256Hex).trim().toLowerCase()
  const normalizedSize=Number(b.sizeBytes)

  if(
    !/^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(
      evidenceUuid
    )
  ){
    throw httpError(
      400,
      "INVALID_EVIDENCE_UUID",
      "UUID de evidencia invÃ¡lido"
    )
  }

  if(!["image/jpeg","image/png","image/webp"].includes(normalizedMime)){
    throw httpError(
      400,
      "INVALID_MIME",
      "Tipo de archivo invÃ¡lido"
    )
  }

  if(
    !Number.isSafeInteger(normalizedSize) ||
    normalizedSize<=0 ||
    normalizedSize>10*1024*1024
  ){
    throw httpError(
      400,
      "INVALID_FILE_SIZE",
      "TamaÃ±o de archivo invÃ¡lido"
    )
  }

  if(!/^[a-f0-9]{64}$/.test(normalizedHash)){
    throw httpError(
      400,
      "INVALID_SHA256",
      "SHA-256 invÃ¡lido"
    )
  }

  const a=reportAccess(u)

  const[rows]=await pool.execute(
    `SELECT
       r.id,
       r.uuid,
       r.municipio_id
     FROM reportes r
     WHERE (r.id=? OR r.folio=?)
       ${a.sql}
     LIMIT 1`,
    [b.reportId,b.reportId,...a.params]
  )

  const report=rows[0]

  if(!report){
    throw httpError(
      404,
      "REPORT_NOT_FOUND",
      "Reporte no encontrado"
    )
  }

  const ext={
    "image/jpeg":"jpg",
    "image/png":"png",
    "image/webp":"webp"
  }[normalizedMime]

  const expectedKey=
    `reportes/${report.uuid}/evidencias/${evidenceUuid}.${ext}`

  if(String(b.objectKey)!==expectedKey){
    throw httpError(
      400,
      "INVALID_OBJECT_KEY",
      "La ruta de la evidencia no corresponde al reporte"
    )
  }

  const bucket=process.env.S3_BUCKET

  if(!bucket){
    throw new Error("S3_BUCKET is not configured")
  }

  let head

  try{
    head=await s3.send(
      new HeadObjectCommand({
        Bucket:bucket,
        Key:expectedKey
      })
    )
  }catch(e){
    if(
      e?.name==="NotFound" ||
      e?.$metadata?.httpStatusCode===404
    ){
      throw httpError(
        400,
        "EVIDENCE_OBJECT_NOT_FOUND",
        "El archivo todavÃ­a no existe en almacenamiento"
      )
    }

    throw e
  }

  if(Number(head.ContentLength)!==normalizedSize){
    throw httpError(
      400,
      "EVIDENCE_SIZE_MISMATCH",
      "El tamaÃ±o del archivo no coincide"
    )
  }

  if(
    String(head.ContentType||"").toLowerCase()!==normalizedMime
  ){
    throw httpError(
      400,
      "EVIDENCE_MIME_MISMATCH",
      "El tipo del archivo no coincide"
    )
  }

  if(
    String(head.Metadata?.sha256||"").toLowerCase()
      !==normalizedHash
  ){
    throw httpError(
      400,
      "EVIDENCE_HASH_MISMATCH",
      "El hash de la evidencia no coincide"
    )
  }

  const c=await pool.getConnection()

  try{
    await c.beginTransaction()

    const[result]=await c.execute(
      `INSERT INTO evidencias(
        reporte_id,
        uuid,
        storage_provider,
        object_key,
        mime_type,
        tamano_bytes,
        sha256_hex,
        cifrada,
        kms_key_ref,
        subida_por_id,
        created_at
      )
      VALUES(
        ?,?,
        'S3',
        ?,?,?,?,
        1,
        NULL,
        ?,
        UTC_TIMESTAMP(6)
      )`,
      [
        report.id,
        evidenceUuid,
        expectedKey,
        normalizedMime,
        normalizedSize,
        normalizedHash,
        u.id
      ]
    )

    await writeAudit(c,{
      userId:u.id,
      municipalityId:report.municipio_id,
      action:"EVIDENCE_CONFIRMED",
      entityType:"EVIDENCE",
      entityId:result.insertId,
      metadata:{
        reportId:Number(report.id),
        evidenceUuid,
        mimeType:normalizedMime,
        sizeBytes:normalizedSize
      }
    })

    await c.commit()

    return response(201,{
      ok:true,
      id:Number(result.insertId),
      evidenceUuid
    })

  }catch(e){
    await c.rollback()

    if(e.code==="ER_DUP_ENTRY"){
      throw httpError(
        409,
        "EVIDENCE_ALREADY_CONFIRMED",
        "La evidencia ya fue confirmada"
      )
    }

    throw e

  }finally{
    c.release()
  }
}


async function listReportEvidences(req,key){
  const u=await requireUser(req,{web:true})
  requirePermission(u,"EVIDENCE_VIEW")

  const access=reportAccess(u,"r")

  // Primero comprobamos que el reporte exista y que el usuario
  // realmente tenga acceso a su municipio.
  const[reportRows]=await pool.execute(
    `
    SELECT
      r.id
    FROM reportes r
    WHERE (r.id=? OR r.folio=?)
      ${access.sql}
    LIMIT 1
    `,
    [key,key,...access.params]
  )

  const report=reportRows[0]

  if(!report){
    throw httpError(
      404,
      "REPORT_NOT_FOUND",
      "Reporte no encontrado"
    )
  }

  // No se devuelve object_key ni informaciÃ³n interna de S3.
  const[rows]=await pool.execute(
    `
    SELECT
      e.id,
      e.uuid,
      e.mime_type,
      e.tamano_bytes,
      e.created_at
    FROM evidencias e
    WHERE e.reporte_id=?
    ORDER BY e.created_at ASC
    `,
    [report.id]
  )

  return response(200,{
    ok:true,
    evidences:rows.map(e=>({
      id:Number(e.id),
      uuid:e.uuid,
      mimeType:e.mime_type,
      sizeBytes:Number(e.tamano_bytes),
      createdAt:e.created_at
    }))
  })
}

async function evidenceUrl(req,id){
  const u=await requireUser(req,{web:true})
  requirePermission(u,"EVIDENCE_VIEW")

  const a=reportAccess(u)

  const[rows]=await pool.execute(
    `SELECT
       e.id,
       e.object_key
     FROM evidencias e
     JOIN reportes r
       ON r.id=e.reporte_id
     WHERE e.id=?
       ${a.sql}
     LIMIT 1`,
    [id,...a.params]
  )

  if(!rows[0]){
    throw httpError(
      404,
      "EVIDENCE_NOT_FOUND",
      "Evidencia no encontrada"
    )
  }

  const url=await getSignedUrl(
    s3,
    new GetObjectCommand({
      Bucket:process.env.S3_BUCKET,
      Key:rows[0].object_key
    }),
    {expiresIn:300}
  )

  return response(200,{
    ok:true,
    url,
    expiresIn:300
  })
}



async function dashboardSummary(req){
  const u=await requireUser(req,{web:true})
  requirePermission(u,"REPORT_VIEW")

  const q=req.query||{}
  const where=["1=1"]
  const params=[]

  // ----------------------------------------------------------
  // AISLAMIENTO MUNICIPAL
  // ----------------------------------------------------------

  const access=reportAccess(u,"r")

  if(access.sql){
    where.push(
      access.sql.replace(/^\s*AND\s+/i,"")
    )
    params.push(...access.params)
  }

  if(
    u.es_administrador &&
    q.municipalityId!==undefined
  ){
    const municipalityId=
      Number.parseInt(
        String(q.municipalityId),
        10
      )

    if(
      !Number.isInteger(municipalityId) ||
      municipalityId<=0
    ){
      throw httpError(
        400,
        "INVALID_MUNICIPALITY",
        "municipalityId debe ser un entero positivo"
      )
    }

    where.push("r.municipio_id=?")
    params.push(municipalityId)
  }

  // ----------------------------------------------------------
  // FECHAS
  // ----------------------------------------------------------

  const datePattern=/^\d{4}-\d{2}-\d{2}$/

  if(q.from!==undefined){
    const from=String(q.from)

    if(!datePattern.test(from)){
      throw httpError(
        400,
        "INVALID_DATE_FROM",
        "from debe usar formato YYYY-MM-DD"
      )
    }

    where.push("r.created_at>=?")
    params.push(`${from} 00:00:00`)
  }

  if(q.to!==undefined){
    const to=String(q.to)

    if(!datePattern.test(to)){
      throw httpError(
        400,
        "INVALID_DATE_TO",
        "to debe usar formato YYYY-MM-DD"
      )
    }

    where.push(
      "r.created_at<DATE_ADD(?,INTERVAL 1 DAY)"
    )

    params.push(`${to} 00:00:00`)
  }

  if(
    q.from!==undefined &&
    q.to!==undefined &&
    String(q.from)>String(q.to)
  ){
    throw httpError(
      400,
      "INVALID_DATE_RANGE",
      "from no puede ser posterior a to"
    )
  }

  const condition=where.join(" AND ")

  // ----------------------------------------------------------
  // RESUMEN GENERAL
  // ----------------------------------------------------------

  const[[totals]]=await pool.execute(
    `
    SELECT
      COUNT(*) AS total_reports,

      SUM(
        CASE WHEN s.codigo='ATENDIDO'
        THEN 1 ELSE 0 END
      ) AS attended,

      SUM(
        CASE WHEN s.codigo='ATENDIENDO'
        THEN 1 ELSE 0 END
      ) AS attending,

      SUM(
        CASE WHEN s.codigo='SIN_ATENDER'
        THEN 1 ELSE 0 END
      ) AS unattended,

      SUM(
        CASE WHEN s.codigo='INACTIVO'
        THEN 1 ELSE 0 END
      ) AS inactive,

      SUM(
        CASE WHEN p.codigo='URGENTE'
        THEN 1 ELSE 0 END
      ) AS urgent,

      SUM(
        CASE
          WHEN p.codigo='URGENTE'
           AND s.codigo='ATENDIDO'
          THEN 1
          ELSE 0
        END
      ) AS urgent_attended,

      SUM(
        CASE
          WHEN EXISTS(
            SELECT 1
            FROM reporte_duplicidades d
            WHERE
              (
                d.reporte_id=r.id
                OR
                d.posible_duplicado_de_id=r.id
              )
              AND d.estado='CONFIRMADO'
          )
          THEN 1
          ELSE 0
        END
      ) AS confirmed_duplicates,

      SUM(
        CASE r.estimacion_nna
          WHEN '1' THEN 1
          WHEN '2' THEN 2
          WHEN '3' THEN 3
          WHEN '4' THEN 4
          WHEN '5_MAS' THEN 5
          ELSE 0
        END
      ) AS minimum_estimated_children,

      SUM(
        CASE
          WHEN s.codigo='ATENDIDO'
          THEN
            CASE r.estimacion_nna
              WHEN '1' THEN 1
              WHEN '2' THEN 2
              WHEN '3' THEN 3
              WHEN '4' THEN 4
              WHEN '5_MAS' THEN 5
              ELSE 0
            END
          ELSE 0
        END
      ) AS minimum_children_attended,

      SUM(
        CASE
          WHEN EXISTS(
            SELECT 1
            FROM evidencias ev
            WHERE ev.reporte_id=r.id
          )
          THEN 1
          ELSE 0
        END
      ) AS reports_with_evidence,

      AVG(
        CASE
          WHEN r.cerrado_at IS NOT NULL
          THEN TIMESTAMPDIFF(
            HOUR,
            r.created_at,
            r.cerrado_at
          )
          ELSE NULL
        END
      ) AS average_resolution_hours,

      SUM(
        CASE
          WHEN r.cerrado_at IS NOT NULL
          THEN 1
          ELSE 0
        END
      ) AS closed_reports

    FROM reportes r

    JOIN estatus_operacionales eo
      ON eo.id=r.estatus_id

    JOIN semaforos s
      ON s.id=eo.semaforo_id

    JOIN prioridades p
      ON p.id=r.prioridad_id

    WHERE ${condition}
    `,
    params
  )

  const total=Number(totals.total_reports||0)
  const attended=Number(totals.attended||0)
  const urgent=Number(totals.urgent||0)
  const urgentAttended=
    Number(totals.urgent_attended||0)

  const minimumChildren=
    Number(
      totals.minimum_estimated_children||0
    )

  const minimumChildrenAttended=
    Number(
      totals.minimum_children_attended||0
    )

  const reportsWithEvidence=
    Number(totals.reports_with_evidence||0)

  const percentage=(value,base)=>
    base>0
      ? Number(((value/base)*100).toFixed(2))
      : 0

  // ----------------------------------------------------------
  // AGRUPACIONES
  // ----------------------------------------------------------

  async function grouped(
    column,
    joins=""
  ){
    const[rows]=await pool.execute(
      `
      SELECT
        ${column} AS code,
        COUNT(*) AS total

      FROM reportes r

      ${joins}

      WHERE ${condition}

      GROUP BY ${column}

      ORDER BY total DESC, code ASC
      `,
      params
    )

    return Object.fromEntries(
      rows.map(row=>[
        row.code===null
          ? "NO_ESPECIFICADO"
          : String(row.code),
        Number(row.total)
      ])
    )
  }

  const[
    bySex,
    byAgeRange,
    byRisk,
    byPriority,
    byStatus,
    byTrafficLight,
    byWorkType,
    byOrigin,
    byLocationSource,
    estimatedChildrenBuckets
  ]=await Promise.all([

    grouped(
      "sx.codigo",
      "LEFT JOIN sexos sx ON sx.id=r.sexo_id"
    ),

    grouped(
      "re.codigo",
      "JOIN rangos_edad re ON re.id=r.rango_edad_id"
    ),

    grouped(
      "nr.codigo",
      "JOIN niveles_riesgo nr ON nr.id=r.nivel_riesgo_id"
    ),

    grouped(
      "p.codigo",
      "JOIN prioridades p ON p.id=r.prioridad_id"
    ),

    grouped(
      "eo.codigo",
      "JOIN estatus_operacionales eo ON eo.id=r.estatus_id"
    ),

    grouped(
      "s.codigo",
      `
      JOIN estatus_operacionales eo
        ON eo.id=r.estatus_id
      JOIN semaforos s
        ON s.id=eo.semaforo_id
      `
    ),

    grouped(
      "tt.codigo",
      `
      JOIN tipos_trabajo_infantil tt
        ON tt.id=r.tipo_trabajo_id
      `
    ),

    grouped(
      "r.origen"
    ),

    grouped(
      "r.ubicacion_origen"
    ),

    grouped(
      "r.estimacion_nna"
    )
  ])

  // ----------------------------------------------------------
  // EVOLUCION MENSUAL
  // ----------------------------------------------------------

  const[monthlyRows]=await pool.execute(
    `
    SELECT
      DATE_FORMAT(
        r.created_at,
        '%Y-%m'
      ) AS month,

      COUNT(*) AS reports,

      SUM(
        CASE
          WHEN s.codigo='ATENDIDO'
          THEN 1
          ELSE 0
        END
      ) AS attended,

      SUM(
        CASE
          WHEN s.codigo='ATENDIENDO'
          THEN 1
          ELSE 0
        END
      ) AS attending,

      SUM(
        CASE
          WHEN p.codigo='URGENTE'
          THEN 1
          ELSE 0
        END
      ) AS urgent,

      SUM(
        CASE r.estimacion_nna
          WHEN '1' THEN 1
          WHEN '2' THEN 2
          WHEN '3' THEN 3
          WHEN '4' THEN 4
          WHEN '5_MAS' THEN 5
          ELSE 0
        END
      ) AS minimum_children

    FROM reportes r

    JOIN estatus_operacionales eo
      ON eo.id=r.estatus_id

    JOIN semaforos s
      ON s.id=eo.semaforo_id

    JOIN prioridades p
      ON p.id=r.prioridad_id

    WHERE ${condition}

    GROUP BY
      DATE_FORMAT(
        r.created_at,
        '%Y-%m'
      )

    ORDER BY month ASC
    `,
    params
  )

  const monthly=monthlyRows.map(row=>({
    month:row.month,
    reports:Number(row.reports),
    attended:Number(row.attended||0),
    attending:Number(row.attending||0),
    urgent:Number(row.urgent||0),

    minimumEstimatedChildren:
      Number(row.minimum_children||0)
  }))

  // ----------------------------------------------------------
  // COLONIAS CON MAS REPORTES
  // ----------------------------------------------------------

  const[colonyRows]=await pool.execute(
    `
    SELECT
      COALESCE(
        c.nombre,
        'Sin colonia especificada'
      ) AS name,

      COUNT(*) AS total

    FROM reportes r

    LEFT JOIN colonias c
      ON c.id=r.colonia_id

    WHERE ${condition}

    GROUP BY
      r.colonia_id,
      c.nombre

    ORDER BY total DESC

    LIMIT 10
    `,
    params
  )

  const topColonies=colonyRows.map(row=>({
    name:row.name,
    total:Number(row.total)
  }))

  // ----------------------------------------------------------
  // MUNICIPIOS
  // ----------------------------------------------------------

  const[municipalityRows]=await pool.execute(
    `
    SELECT
      m.id,
      m.nombre AS name,
      COUNT(*) AS total

    FROM reportes r

    JOIN municipios m
      ON m.id=r.municipio_id

    WHERE ${condition}

    GROUP BY
      m.id,
      m.nombre

    ORDER BY total DESC

    LIMIT 20
    `,
    params
  )

  const byMunicipality=
    municipalityRows.map(row=>({
      id:Number(row.id),
      name:row.name,
      total:Number(row.total)
    }))

  // ----------------------------------------------------------
  // RESPUESTA
  // ----------------------------------------------------------

  const averageResolutionHours=
    totals.average_resolution_hours==null
      ? null
      : Number(
          Number(
            totals.average_resolution_hours
          ).toFixed(2)
        )

  const summary={
    totalReports:total,

    attended,
    attending:
      Number(totals.attending||0),

    unattended:
      Number(totals.unattended||0),

    inactive:
      Number(totals.inactive||0),

    attentionRate:
      percentage(attended,total),

    urgent,

    urgentAttended,

    urgentAttentionRate:
      percentage(
        urgentAttended,
        urgent
      ),

    confirmedDuplicates:
      Number(
        totals.confirmed_duplicates||0
      ),

    minimumEstimatedChildren:
      minimumChildren,

    minimumChildrenAttended:
      minimumChildrenAttended,

    minimumChildrenAttentionRate:
      percentage(
        minimumChildrenAttended,
        minimumChildren
      ),

    reportsWithEvidence,

    reportsWithoutEvidence:
      Math.max(
        0,
        total-reportsWithEvidence
      ),

    evidenceCoverageRate:
      percentage(
        reportsWithEvidence,
        total
      ),

    closedReports:
      Number(
        totals.closed_reports||0
      ),

    averageResolutionHours
  }

  return response(200,{
    ok:true,

    summary,

    monthly,

    bySex,
    byAgeRange,
    byRisk,
    byPriority,
    byStatus,
    byTrafficLight,
    byWorkType,
    byOrigin,
    byLocationSource,

    estimatedChildrenBuckets,

    topColonies,
    byMunicipality,

    // Compatibilidad con la versiÃ³n anterior del endpoint.
    totalReports:summary.totalReports,
    confirmedDuplicates:
      summary.confirmedDuplicates
  })
}

async function reportMap(req){
  const u=await requireUser(req,{web:true})
  requirePermission(u,"MAP_VIEW")

  const q=req.query||{}
  const where=["1=1"]
  const params=[]

  // ----------------------------------------------------------
  // AISLAMIENTO MUNICIPAL
  // ----------------------------------------------------------
  const access=reportAccess(u,"v")

  if(access.sql){
    where.push(access.sql.replace(/^\s*AND\s+/i,""))
    params.push(...access.params)
  }

  // El administrador puede filtrar por municipio.
  // Los roles municipales nunca pueden cambiar su alcance.
  if(u.es_administrador && q.municipalityId!==undefined){
    const municipalityId=Number.parseInt(
      String(q.municipalityId),
      10
    )

    if(
      !Number.isInteger(municipalityId) ||
      municipalityId<=0
    ){
      throw httpError(
        400,
        "INVALID_MUNICIPALITY",
        "municipalityId debe ser un entero positivo"
      )
    }

    where.push("v.municipio_id=?")
    params.push(municipalityId)
  }

  // ----------------------------------------------------------
  // FILTROS DE CATALOGO
  // ----------------------------------------------------------
  const filterMap=[
    ["risk","v.nivel_riesgo_codigo"],
    ["priority","v.prioridad_codigo"],
    ["status","v.estatus_codigo"],
    ["trafficLight","v.semaforo_codigo"],
    ["workType","v.tipo_trabajo_codigo"],
    ["ageRange","v.rango_edad_codigo"]
  ]

  for(const [queryKey,column] of filterMap){
    if(q[queryKey]!==undefined && String(q[queryKey]).trim()!==""){
      where.push(`${column}=?`)
      params.push(String(q[queryKey]).trim().toUpperCase())
    }
  }

  // ----------------------------------------------------------
  // FILTRO POR COLONIA
  // ----------------------------------------------------------
  if(q.colonyId!==undefined){
    const colonyId=Number.parseInt(String(q.colonyId),10)

    if(!Number.isInteger(colonyId)||colonyId<=0){
      throw httpError(
        400,
        "INVALID_COLONY",
        "colonyId debe ser un entero positivo"
      )
    }

    where.push("v.colonia_id=?")
    params.push(colonyId)
  }

  // ----------------------------------------------------------
  // FECHAS
  // YYYY-MM-DD
  // ----------------------------------------------------------
  const datePattern=/^\d{4}-\d{2}-\d{2}$/

  if(q.from!==undefined){
    const from=String(q.from)

    if(!datePattern.test(from)){
      throw httpError(
        400,
        "INVALID_DATE_FROM",
        "from debe usar formato YYYY-MM-DD"
      )
    }

    where.push("v.created_at>=?")
    params.push(`${from} 00:00:00`)
  }

  if(q.to!==undefined){
    const to=String(q.to)

    if(!datePattern.test(to)){
      throw httpError(
        400,
        "INVALID_DATE_TO",
        "to debe usar formato YYYY-MM-DD"
      )
    }

    where.push("v.created_at<DATE_ADD(?,INTERVAL 1 DAY)")
    params.push(`${to} 00:00:00`)
  }

  if(
    q.from!==undefined &&
    q.to!==undefined &&
    String(q.from)>String(q.to)
  ){
    throw httpError(
      400,
      "INVALID_DATE_RANGE",
      "from no puede ser posterior a to"
    )
  }

  // ----------------------------------------------------------
  // QUERY ANALITICA
  // No devuelve descripciÃ³n, contacto, direcciÃ³n, notas
  // ni evidencia.
  // ----------------------------------------------------------
  const sql=`
    SELECT
      v.reporte_id,
      v.municipio_id,
      v.colonia_id,
      v.latitud,
      v.longitud,
      v.fecha_hora_observacion_utc,
      v.created_at,
      v.estimacion_nna,
      v.rango_edad_codigo,
      v.sexo_codigo,
      v.tipo_trabajo_codigo,
      v.nivel_riesgo_codigo,
      v.prioridad_codigo,
      v.estatus_codigo,
      v.semaforo_codigo,
      v.tiene_duplicidad_confirmada
    FROM v_inteligencia_territorial v
    WHERE ${where.join(" AND ")}
    ORDER BY v.created_at DESC
    LIMIT 5000
  `

  const[rows]=await pool.execute(sql,params)

  const points=rows.map(r=>({
    reportId:Number(r.reporte_id),
    municipalityId:Number(r.municipio_id),
    colonyId:
      r.colonia_id==null
        ? null
        : Number(r.colonia_id),

    lat:Number(r.latitud),
    lng:Number(r.longitud),

    estimatedChildren:r.estimacion_nna,
    ageRange:r.rango_edad_codigo,
    sex:r.sexo_codigo,
    workType:r.tipo_trabajo_codigo,

    risk:r.nivel_riesgo_codigo,
    priority:r.prioridad_codigo,
    status:r.estatus_codigo,
    trafficLight:r.semaforo_codigo,

    confirmedDuplicate:
      Boolean(Number(r.tiene_duplicidad_confirmada)),

    observedAt:r.fecha_hora_observacion_utc,
    createdAt:r.created_at
  }))

  return response(200,{
    ok:true,
    count:points.length,
    points
  })
}

function match(path,re){const m=re.exec(path);return m?m.slice(1).map(decodeURIComponent):null;}

async function router(req){
  if(req.method==="GET"&&(req.path==="/"||req.path==="/health"))return health();
  if(req.method==="POST"&&req.path==="/setup/bootstrap-admin"){
    if(process.env.BOOTSTRAP_ENABLED!=="true"){
      throw httpError(
        404,
        "NOT_FOUND",
        "Ruta no encontrada"
      )
    }

    return bootstrapAdmin(req)
  }
  if(req.method==="POST"&&req.path==="/auth/login")return login(req);
  if(req.method==="POST"&&req.path==="/auth/refresh")return refreshSession(req);
  if(req.method==="POST"&&req.path==="/auth/logout")return logout(req);
  if(req.method==="GET"&&req.path==="/auth/me")return me(req);
  if(req.method==="POST"&&req.path==="/auth/recovery/request")return recoveryRequest(req);
  if(req.method==="POST"&&req.path==="/auth/recovery/reset")return recoveryReset(req);
  if(req.method==="GET"&&req.path==="/catalogos")return listCatalogs(req);
  if(req.method==="GET"&&req.path==="/usuarios")return listUsers(req);
  if(req.method==="POST"&&req.path==="/usuarios")return createUser(req);
  let m;
  if((m=match(req.path,/^\/usuarios\/([^/]+)$/))&&req.method==="PATCH")return updateUser(req,m[0]);
  if((m=match(req.path,/^\/usuarios\/([^/]+)$/))&&req.method==="DELETE")return deactivateUser(req,m[0]);
  if(req.method==="GET"&&req.path==="/dashboard/resumen")return dashboardSummary(req);
  if(req.method==="GET"&&req.path==="/reportes/mapa")return reportMap(req);
  if(req.method==="GET"&&req.path==="/reportes")return listReports(req);
  if(req.method==="POST"&&req.path==="/reportes")return createReport(req);
  if((m=match(req.path,/^\/reportes\/([^/]+)$/))&&req.method==="GET")return getReport(req,m[0]);
  if((m=match(req.path,/^\/reportes\/([^/]+)$/))&&req.method==="PATCH")return updateReport(req,m[0]);
  if((m=match(req.path,/^\/reportes\/([^/]+)\/notas$/))&&req.method==="POST")return addNote(req,m[0]);
  if((m=match(req.path,/^\/reportes\/([^/]+)\/asignaciones$/))&&req.method==="POST")return assignReport(req,m[0]);
  if((m=match(req.path,/^\/reportes\/([^/]+)\/evidencias$/))&&req.method==="GET")return listReportEvidences(req,m[0]);
  if(req.method==="POST"&&req.path==="/evidencias/presign")return presignEvidence(req);
  if(req.method==="POST"&&req.path==="/evidencias/confirmar")return confirmEvidence(req);
  if((m=match(req.path,/^\/evidencias\/([^/]+)\/url$/))&&req.method==="GET")return evidenceUrl(req,m[0]);
  throw httpError(404,"NOT_FOUND","Ruta no encontrada");
}
export const handler=async(event)=>{
  try{
    const req=getRequest(event);
    req.query=event?.queryStringParameters||req.query||{};
    return await router(req);
  }
  catch(e){
    console.error("RIETI API error",{name:e.name,code:e.code,message:e.message,status:e.status});
    if(e.code&&String(e.code).startsWith("ER_"))return response(500,{ok:false,error:{code:"DATABASE_ERROR",message:"Error de base de datos"}});
    return response(e.status||500,{ok:false,error:{code:e.code||"INTERNAL_ERROR",message:e.status?e.message:"Error interno del servidor",details:e.details}});
  }
};


