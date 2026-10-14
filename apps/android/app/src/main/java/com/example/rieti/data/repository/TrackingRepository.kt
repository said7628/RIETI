package com.example.rieti.data.repository

import android.content.Context
import com.example.rieti.data.session.SessionManager
import com.example.rieti.data.session.AuthenticatedRequests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Representa los datos de seguimiento de un reporte ciudadano
 * registrados en el sistema RIETI.
 *
 * Almacena la información general del reporte, su estado actual,
 * las fechas relevantes y el historial de cambios de estado.
 *
 * @property folio Identificador del reporte ciudadano.
 * @property statusCode Código del estado actual del reporte.
 * @property registeredAt Fecha de registro del reporte.
 * @property updatedAt Fecha de la última actualización.
 * @property municipality Municipio asociado al reporte.
 * @property closedAt Fecha de cierre, si está disponible.
 * @property resolution Información de resolución, si existe.
 * @property timeline Relación de estados del reporte con sus
 * respectivas fechas de actualización.
 *
 * @author Equipo 3:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
data class CitizenTracking(
    val folio: String, val statusCode: String, val registeredAt: String,
    val updatedAt: String, val municipality: String, val closedAt: String?,
    val resolution: String?, val timeline: Map<String,String>
)


/**
 * Repositorio encargado de consultar el seguimiento
 * de los reportes ciudadanos mediante su folio.
 *
 * Utiliza OkHttp para realizar solicitudes HTTP al backend
 * de RIETI y SessionManager para obtener el token
 * de autenticación del usuario.
 *
 * Procesa la respuesta JSON del servidor y convierte
 * la información recibida en un objeto CitizenTracking.
 *
 * @property context Contexto de Android utilizado
 * para consultar la sesión del usuario.
 */

data class CitizenReportSummary(
    val folio: String,
    val statusCode: String,
    val municipality: String,
    val registeredAt: String,
    val updatedAt: String
)

class TrackingRepository(private val context: Context) {
    /**
     * Cliente HTTP utilizado para consultar el seguimiento
     * de los reportes mediante el backend de RIETI.
     *
     * Establece un tiempo máximo de conexión de 15 segundos
     * y un tiempo máximo de lectura de 25 segundos.
     */
    private val http = OkHttpClient.Builder().connectTimeout(15,TimeUnit.SECONDS)
        .readTimeout(25,TimeUnit.SECONDS).build()

    /**
     * Consulta la información de seguimiento de un reporte
     * ciudadano a partir de su folio.
     *
     * La operación se ejecuta mediante Dispatchers.IO para
     * realizar la comunicación de red fuera del hilo principal.
     *
     * Primero obtiene el token de acceso del usuario,
     * comprueba el formato del folio y realiza una solicitud
     * GET autenticada al endpoint de seguimiento.
     *
     * Posteriormente, interpreta la respuesta JSON y obtiene
     * los datos generales del reporte y su historial de estados.
     *
     * @param folio Identificador del reporte que se desea consultar.
     * @return Objeto CitizenTracking con la información de
     * seguimiento proporcionada por el backend.
     *
     * @throws IllegalStateException Si no existe una sesión
     * autenticada o si el servidor devuelve un error.
     * @throws IllegalArgumentException Si el folio no cumple
     * con el formato establecido.
     * @throws Exception Si ocurre un error durante la solicitud
     * HTTP o el procesamiento de la respuesta JSON.
     */
    /** Reportes de seguimiento registrados por la cuenta actual; no devuelve anonimos. */
    suspend fun listMine(): List<CitizenReportSummary> = withContext(Dispatchers.IO) {
        val token = SessionManager(context).accessToken()?.takeIf { it.isNotBlank() }
            ?: error("Inicia sesión para consultar tus reportes")
        val url = "https://tdjmvpcukz452yjfd4pkjuae7i0yajrv.lambda-url.us-east-1.on.aws/mobile/mis-reportes"
        val request = Request.Builder().url(url).header("Authorization", "Bearer $token").get().build()
        AuthenticatedRequests(context).execute(http, request).use { response ->
            val raw = response.body?.string().orEmpty()
            val data = runCatching { JSONObject(raw) }.getOrElse { JSONObject() }
            if (!response.isSuccessful || !data.optBoolean("ok")) {
                error(data.optJSONObject("error")?.optString("message")?.takeIf { it.isNotBlank() }
                    ?: "No se pudo cargar tu lista de reportes (HTTP ${response.code})")
            }
            val array = data.optJSONArray("reports") ?: return@withContext emptyList()
            (0 until array.length()).map { index ->
                val item = array.getJSONObject(index)
                CitizenReportSummary(
                    folio = item.getString("folio"),
                    statusCode = item.getString("statusCode"),
                    municipality = item.optString("municipality"),
                    registeredAt = item.optString("registeredAt"),
                    updatedAt = item.optString("updatedAt")
                )
            }
        }
    }

    suspend fun lookup(folio: String): CitizenTracking = withContext(Dispatchers.IO) {
        val token = SessionManager(context).accessToken()?.takeIf { it.isNotBlank() }
            ?: error("Inicia sesion para consultar tus reportes")
        require(Regex("[A-Za-z0-9]{2,20}-[0-9]{4}-[0-9]{6}").matches(folio)) { "Folio invalido" }
        val url = "https://tdjmvpcukz452yjfd4pkjuae7i0yajrv.lambda-url.us-east-1.on.aws/mobile/seguimiento/$folio"
        val request = Request.Builder().url(url).header("Authorization","Bearer $token").get().build()
        AuthenticatedRequests(context).execute(http, request).use { response ->
            val body = JSONObject(response.body?.string().orEmpty())
            if (!response.isSuccessful || !body.optBoolean("ok")) {
                error(body.optJSONObject("error")?.optString("message") ?: "No se pudo consultar el reporte")
            }
            val r = body.getJSONObject("report")
            val arr = r.optJSONArray("timeline")
            val events = mutableMapOf<String,String>()
            if (arr != null) for (i in 0 until arr.length()) {
                val e = arr.getJSONObject(i)
                events[e.optString("status")] = e.optString("at")
            }
            CitizenTracking(r.getString("folio"),r.getString("statusCode"),
                r.optString("registeredAt"),r.optString("updatedAt"),r.optString("municipality"),
                r.optString("closedAt").takeIf { it.isNotBlank() && it != "null" },
                r.optString("resolution").takeIf { it.isNotBlank() && it != "null" },events)
        }
    }
}
