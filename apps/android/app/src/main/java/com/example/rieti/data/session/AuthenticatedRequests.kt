package com.example.rieti.data.session

import android.content.Context
import android.util.Base64
import com.example.rieti.data.repository.AuthRepository
import com.example.rieti.data.repository.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

/**
 * Renueva el token al vencer y vuelve a intentar una peticion solo si
 * el servidor responde 401. Evita varias renovaciones simultaneas.
 * Autor: Said Valseca Figueroa.
 */
class AuthenticatedRequests(context: Context) {
    private val sessions = SessionManager(context.applicationContext)

    companion object {
        private val refreshMutex = Mutex()
    }

    private fun expiresSoon(token: String): Boolean {
        return try {
            val piece = token.split(".")[1]
            val decoded = String(Base64.decode(piece, Base64.URL_SAFE or Base64.NO_WRAP))
            val expiry = JSONObject(decoded).optLong("exp")
            expiry > 0 && expiry * 1000L < System.currentTimeMillis() + 90_000L
        } catch (_: Exception) {
            false // Un token opaco lo validara el servidor.
        }
    }

    private suspend fun renew(previousAccess: String): String = refreshMutex.withLock {
        if (!sessions.isWithinSevenDays()) {
            sessions.clear()
            error("Tu sesion cumplio siete dias sin actividad. Inicia sesion nuevamente.")
        }
        val current = sessions.accessToken().orEmpty()
        if (current.isNotBlank() && current != previousAccess) return@withLock current
        val refresh = sessions.refreshToken()?.takeIf { it.isNotBlank() }
            ?: error("Inicia sesion para continuar")
        when (val result = AuthRepository().refresh(refresh)) {
            is AuthResult.Success -> {
                sessions.saveRefresh(result.data)
                sessions.accessToken() ?: error("No se recibio un token nuevo")
            }
            is AuthResult.Error -> {
                if (result.code in setOf("INVALID_REFRESH_TOKEN", "UNAUTHORIZED")) {
                    sessions.clear()
                    error("Tu sesion termino. Inicia sesion nuevamente.")
                }
                error("No se pudo renovar la sesion. Revisa tu conexion e intenta de nuevo.")
            }
        }
    }

    /** Renueva al recuperar la conexion, sin repetir solicitudes muy seguidas. */
    suspend fun onConnectionRestored() {
        if (!sessions.hasSession()) return
        val last = sessions.lastSuccessfulAuthAt()
        if (last > 0L && System.currentTimeMillis() - last < 5L * 60L * 1000L) return
        val current = sessions.accessToken() ?: return
        renew(current)
    }

    suspend fun execute(client: OkHttpClient, request: Request): Response {
        if (!sessions.isWithinSevenDays()) {
            sessions.clear()
            error("Tu sesion cumplio siete dias sin actividad. Inicia sesion nuevamente.")
        }
        var token = sessions.accessToken()?.takeIf { it.isNotBlank() }
            ?: error("Inicia sesion para continuar")
        if (expiresSoon(token)) token = renew(token)

        fun withToken(access: String): Request = request.newBuilder()
            .header("Authorization", "Bearer $access").build()

        var response = withContext(Dispatchers.IO) {
            client.newCall(withToken(token)).execute()
        }
        if (response.code != 401) return response

        response.close()
        token = renew(token)
        response = withContext(Dispatchers.IO) {
            client.newCall(withToken(token)).execute()
        }
        return response
    }
}
