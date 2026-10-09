package com.example.rieti.data.repository

import android.content.Context
import com.example.rieti.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class CitizenTracking(
    val folio: String, val statusCode: String, val registeredAt: String,
    val updatedAt: String, val municipality: String, val closedAt: String?,
    val resolution: String?, val timeline: Map<String,String>
)

/** Autor: Said Valseca Figueroa. Consulta el seguimiento real del reporte. */
class TrackingRepository(private val context: Context) {
    private val http = OkHttpClient.Builder().connectTimeout(15,TimeUnit.SECONDS)
        .readTimeout(25,TimeUnit.SECONDS).build()
    suspend fun lookup(folio: String): CitizenTracking = withContext(Dispatchers.IO) {
        val token = SessionManager(context).accessToken()?.takeIf { it.isNotBlank() }
            ?: error("Inicia sesion para consultar tus reportes")
        require(Regex("[A-Za-z0-9]{2,20}-[0-9]{4}-[0-9]{6}").matches(folio)) { "Folio invalido" }
        val url = "https://tdjmvpcukz452yjfd4pkjuae7i0yajrv.lambda-url.us-east-1.on.aws/mobile/seguimiento/$folio"
        val request = Request.Builder().url(url).header("Authorization","Bearer $token").get().build()
        http.newCall(request).execute().use { response ->
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
