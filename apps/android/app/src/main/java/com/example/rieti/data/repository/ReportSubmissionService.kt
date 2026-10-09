package com.example.rieti.data.repository

import android.content.Context
import android.net.Uri
import com.example.rieti.data.session.SessionManager
import com.example.rieti.model.ReportUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Autor: Said Valseca Figueroa
 * Envia reportes a RIETI y las imagenes directamente a S3.
 */
class ReportSubmissionService(private val context: Context) {
    private val base = "https://tdjmvpcukz452yjfd4pkjuae7i0yajrv.lambda-url.us-east-1.on.aws/"
    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

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

    suspend fun submit(state: ReportUiState, submissionId: String): String = withContext(Dispatchers.IO) {
        val token = SessionManager(context).accessToken()
            ?.takeIf { it.isNotBlank() } ?: error("Debes iniciar sesion nuevamente")
        require(state.details.isNotBlank()) { "Describe la situacion antes de enviar" }
        require(state.municipality.isNotBlank()) { "Selecciona el municipio" }
        require(state.workType.isNotBlank()) { "Selecciona el tipo de trabajo" }
        require(state.childrenAges.isNotBlank()) { "Selecciona la edad aproximada" }

        val request = JSONObject()
            .put("mode", state.mode)
            .put("clientReportUuid", submissionId)
            .put("municipality", state.municipality)
            .put("neighborhood", state.neighborhood)
            .put("ages", state.childrenAges)
            .put("children", state.numberOfChildren)
            .put("workType", state.workType)
            .put("details", state.details)
            .put("latitude", state.selectedLocation.latitude)
            .put("longitude", state.selectedLocation.longitude)
            .put("address", state.fullAddress)
            .put("postalCode", state.postalCode)
            .put("schedule", state.workSchedule)

        val created = jsonRequest("mobile/reportes", token, request)
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
            val metadata = JSONObject().put("reportId", id).put("mimeType", mime)
                .put("sizeBytes", bytes.size).put("sha256Hex", sha)
            val signed = jsonRequest("mobile/evidencias/presign", token, metadata)
            val put = Request.Builder().url(signed.getString("uploadUrl"))
                .put(bytes.toRequestBody(mime.toMediaType()))
                .header("x-amz-meta-sha256", sha)
                .build()
            http.newCall(put).execute().use { response ->
                if (!response.isSuccessful) error("No se pudo subir una imagen a S3 (HTTP ${response.code})")
            }
            jsonRequest("mobile/evidencias/confirmar", token, JSONObject(metadata.toString())
                .put("evidenceUuid", signed.getString("evidenceUuid"))
                .put("objectKey", signed.getString("objectKey")))
        }
        folio
    }
}
