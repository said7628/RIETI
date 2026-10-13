package com.example.rieti.data.repository

import android.content.Context
import android.net.Uri
import com.example.rieti.data.session.SessionManager
import com.example.rieti.model.ReportUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.Normalizer
import java.util.Locale
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Gestiona el envío de reportes ciudadanos y sus evidencias
 * fotográficas desde la aplicación Android hacia RIETI.
 *
 * Se comunica con el backend mediante solicitudes HTTP
 * autenticadas para registrar reportes, solicitar URL
 * prefirmadas, confirmar evidencias y finalizar el envío.
 *
 * Las imágenes se transfieren directamente a Amazon S3
 * utilizando las URL prefirmadas proporcionadas por el backend.
 *
 * @param context Contexto de Android utilizado para
 * acceder a la sesión y a los archivos seleccionados.
 *
 * @author Equipo 3:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
class ReportSubmissionService(private val context: Context) {

    /**
     * Dirección base del backend de RIETI,
     * desplegado mediante AWS Lambda Function URL.
     */
    private val base = "https://tdjmvpcukz452yjfd4pkjuae7i0yajrv.lambda-url.us-east-1.on.aws/"

    /**
     * Cliente HTTP utilizado para registrar reportes,
     * solicitar URL prefirmadas y transferir evidencias.
     *
     * Configura tiempos máximos de conexión de 15 segundos
     * y de lectura y escritura de 25 segundos.
     */
    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    /**
     * Ejecuta una solicitud HTTP POST con contenido JSON
     * hacia un endpoint del backend de RIETI.
     *
     * Incorpora el token de autenticación mediante
     * el encabezado Authorization con esquema Bearer.
     *
     * Interpreta la respuesta JSON y comprueba tanto
     * el estado HTTP como el indicador "ok".
     *
     * @param path Ruta relativa del endpoint solicitado.
     * @param token Token de acceso del usuario autenticado.
     * @param body Objeto JSON enviado en la solicitud.
     * @return Respuesta JSON devuelta por el backend.
     *
     * @throws IllegalStateException Si la respuesta HTTP
     * no es exitosa o el backend devuelve "ok" falso.
     */
    private fun jsonRequest(path: String, token: String, body: JSONObject): JSONObject {
        val req = Request.Builder().url(base + path)
            .header("Authorization", "Bearer $token")
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()
        http.newCall(req).execute().use { response ->
            val raw = response.body?.string().orEmpty()
            val data = runCatching { JSONObject(raw) }.getOrElse { JSONObject() }
            if (!response.isSuccessful || !data.optBoolean("ok")) {
                val error = data.optJSONObject("error")
                throw IllegalStateException(error?.optString("message")?.takeIf { it.isNotBlank() }
                    ?: "Error HTTP ${response.code} al comunicarse con RIETI")
            }
            return data
        }
    }

    /**
     * Envía un reporte ciudadano al backend de RIETI
     * junto con las evidencias fotográficas seleccionadas.
     *
     * La operación se ejecuta en Dispatchers.IO para
     * realizar las solicitudes de red fuera del hilo principal.
     *
     * El procedimiento comprende las siguientes etapas:
     *
     * 1. Obtiene el token de autenticación de la sesión.
     * 2. Valida los campos obligatorios del reporte.
     * 3. Convierte los valores seleccionados a identificadores
     *    reconocidos por el backend.
     * 4. Normaliza el municipio y determina su identificador.
     * 5. Registra el reporte mediante POST /mobile/reportes.
     * 6. Valida las imágenes y calcula su hash SHA-256.
     * 7. Solicita URL prefirmadas para cargar las evidencias.
     * 8. Transfiere las imágenes directamente a Amazon S3.
     * 9. Confirma las evidencias cargadas ante el backend.
     * 10. Solicita la finalización del reporte cuando
     *     se dispone de un token de carga.
     *
     * @param state Estado del formulario con los datos
     * del reporte y las referencias a las evidencias.
     * @param submissionId Identificador del envío utilizado
     * como clientReportUuid en la solicitud al backend.
     * @return Folio asignado al reporte registrado.
     *
     * @throws IllegalArgumentException Si algún dato
     * obligatorio o alguna evidencia incumple las validaciones.
     * @throws IllegalStateException Si no existe un token
     * de acceso o falla una operación con el backend o S3.
     * @throws Exception Si ocurre un error de red, lectura
     * de archivos o procesamiento de la respuesta.
     */
    suspend fun submit(state: ReportUiState, submissionId: String): String = withContext(Dispatchers.IO) {
        val token = SessionManager(context).accessToken()
            ?.takeIf { it.isNotBlank() } ?: error("Debes iniciar sesion nuevamente")
        require(state.municipality.isNotBlank()) { "Selecciona el municipio" }
        require(state.workType.isNotBlank()) { "Selecciona el tipo de trabajo" }
        require(state.childrenAges.isNotBlank()) { "Selecciona la edad aproximada" }

        val ageIds = mapOf("0-5" to 1, "6-11" to 2, "12-14" to 3, "15-17" to 4)
        val workIds = mapOf(
            "Venta en semáforos" to 1, "Repartiendo volantes" to 2,
            "Venta en mercado" to 3, "Despachador en tortillería" to 4,
            "Trabajo en construcción" to 5, "Lavado de autos" to 6,
            "Despachador de gasolina" to 7, "Cerillo en supermercado" to 8,
            "Limpiaparabrisas" to 9, "Mendicidad forzada o inducida" to 10,
            "Explotación sexual comercial de NNA" to 11
        )
        val ageId = ageIds[state.childrenAges] ?: error("Selecciona un rango de edad válido")
        val workId = workIds[state.workType] ?: error("Selecciona una actividad válida")

        // El geocodificador puede devolver nombres abreviados.
        // La API recibe el ID oficial del municipio adherido.
        val municipioNormalizado = Normalizer.normalize(
            state.municipality.trim().lowercase(Locale.ROOT), Normalizer.Form.NFD
        ).replace(Regex("\\p{Mn}+"), "")
        val municipioId = when {
            "cuautitlan izcalli" in municipioNormalizado -> 3
            "atizapan de zaragoza" in municipioNormalizado ||
                municipioNormalizado == "atizapan" -> 1
            "tlalnepantla" in municipioNormalizado -> 4
            else -> error("RIETI no recibe reportes en este municipio")
        }

        val request = JSONObject()
            .put("mode", state.mode)
            .put("clientReportUuid", submissionId)
            .put("municipalityId", municipioId)
            .put("neighborhood", state.neighborhood)
            .put("ageRangeId", ageId)
            .put("children", state.numberOfChildren)
            .put("workTypeId", workId)
            .put("details", state.details)
            .put("latitude", state.selectedLocation.latitude)
            .put("longitude", state.selectedLocation.longitude)
            .put("address", state.fullAddress)
            .put("postalCode", state.postalCode)
            .put("schedule", state.workSchedule)

        val created = jsonRequest("mobile/reportes", token, request)
        val uploadToken = created.optString("uploadToken").takeIf { it.isNotBlank() }
        val id = created.getLong("id")
        val folio = created.getString("folio")

        state.evidenceReferences.distinct().forEach { uriString ->
            val uri = Uri.parse(uriString)
            val mime = context.contentResolver.getType(uri)?.lowercase()
                ?: error("No se pudo identificar el tipo de imagen")
            require(mime in setOf("image/jpeg", "image/png", "image/webp")) {
                "Solo se aceptan imagenes JPG, PNG o WEBP"
            }
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: error("No se pudo abrir una fotografia")
            require(bytes.isNotEmpty() && bytes.size <= 10 * 1024 * 1024) {
                "Cada imagen debe medir entre 1 byte y 10 MB"
            }
            val sha = MessageDigest.getInstance("SHA-256").digest(bytes)
                .joinToString("") { "%02x".format(it) }
            val metadata = JSONObject().put("reportId", id).put("uploadToken", uploadToken).put("mimeType", mime)
                .put("sizeBytes", bytes.size).put("sha256Hex", sha)
            val signed = jsonRequest("mobile/evidencias/presign", token, metadata)
            // En un PUT prefirmado, los headers deben coincidir con los firmados por Lambda.
            // No añadir el Bearer token de RIETI a esta solicitud de S3.
            // El presigner de AWS puede colocar la metadata en la query firmada.
            // En ese caso no enviamos otra copia como header HTTP.
            val signedUrl = signed.getString("uploadUrl")
            val parsedUrl = signedUrl.toHttpUrl()
            val metadataInUrl = parsedUrl.queryParameterNames.any {
                it.equals("x-amz-meta-sha256", ignoreCase = true)
            }
            val putBuilder = Request.Builder().url(signedUrl)
                .put(bytes.toRequestBody(mime.toMediaType()))
                .header("Content-Type", mime)
            if (!metadataInUrl) {
                putBuilder.header("x-amz-meta-sha256", sha)
            }
            val put = putBuilder.build()
            http.newCall(put).execute().use { response ->
                if (!response.isSuccessful) {
                    // S3 devuelve XML con Code y Message; nunca mostramos la URL
                    // prefirmada, firmas, tokens ni detalles internos de la petición.
                    val xml = response.body?.string().orEmpty()
                    val code = Regex("<Code>\\s*([^<]+)\\s*</Code>")
                        .find(xml)?.groupValues?.getOrNull(1)?.trim()
                        ?.takeIf { it.matches(Regex("[A-Za-z0-9]{1,80}")) }
                    val label = when (code) {
                        "SignatureDoesNotMatch" -> "Firma S3 incorrecta"
                        "AccessDenied" -> "S3 denegó el acceso"
                        "RequestTimeTooSkewed" -> "Reloj del dispositivo desfasado"
                        "ExpiredToken" -> "Credenciales AWS vencidas"
                        "AuthorizationQueryParametersError" -> "Parámetros de firma S3 inválidos"
                        else -> "S3 rechazó la imagen"
                    }
                    error("$label (HTTP ${response.code}; código S3: ${code ?: "no disponible"}). " +
                        "El reporte puede haberse creado; no comiences uno nuevo todavía.")
                }
            }
            jsonRequest("mobile/evidencias/confirmar", token, JSONObject(metadata.toString())
                .put("evidenceUuid", signed.getString("evidenceUuid"))
                .put("objectKey", signed.getString("objectKey")))
        }
        if (uploadToken != null) {
            jsonRequest("mobile/reportes/finalizar", token, JSONObject().put("reportId", id).put("uploadToken", uploadToken))
        }
        folio
    }
}
