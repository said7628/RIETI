package com.example.rieti.data.repository

import android.content.Context
import com.example.rieti.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/** Autor: Said Valseca Figueroa. Operaciones reales de la cuenta. */
class AccountActionsRepository(context: Context) {
    private val sessions = SessionManager(context)
    private val client = OkHttpClient()
    private val base = "https://tdjmvpcukz452yjfd4pkjuae7i0yajrv.lambda-url.us-east-1.on.aws/"

    private suspend fun post(path: String, data: JSONObject) = withContext(Dispatchers.IO) {
        val token = sessions.accessToken()?.takeIf { it.isNotBlank() }
            ?: error("Inicia sesion nuevamente")
        val request = Request.Builder().url(base + path)
            .header("Authorization", "Bearer $token")
            .post(data.toString().toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            val dataResponse = runCatching { JSONObject(response.body?.string().orEmpty()) }.getOrNull()
            if (!response.isSuccessful || dataResponse?.optBoolean("ok") != true) {
                error(dataResponse?.optJSONObject("error")?.optString("message")
                    ?.takeIf { it.isNotBlank() } ?: "Error HTTP ${response.code}")
            }
        }
    }

    suspend fun changePassword(current: String, next: String) {
        require(next.length in 10..128) { "La nueva contrasena debe tener entre 10 y 128 caracteres" }
        post("mobile/cuenta/cambiar-password", JSONObject()
            .put("currentPassword", current).put("newPassword", next))
    }

    suspend fun deactivate(password: String) {
        require(password.isNotBlank()) { "Confirma tu contrasena" }
        post("mobile/cuenta/desactivar", JSONObject().put("password", password))
    }
}
