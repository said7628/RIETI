package com.example.rieti.data.repository

import android.content.Context
import com.example.rieti.data.session.SessionManager
import com.example.rieti.data.session.AuthenticatedRequests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Repositorio encargado de gestionar operaciones
 * relacionadas con las cuentas ciudadanas de RIETI.
 *
 * Permite solicitar el cambio de contraseña y la
 * desactivación de una cuenta mediante los servicios
 * del backend.
 *
 * Utiliza SessionManager para recuperar el token
 * de acceso y OkHttp para realizar solicitudes
 * HTTP autenticadas.
 *
 * @param context Contexto de Android utilizado para
 * acceder a la información de sesión del usuario.
 *
 * @author Equipo 3:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
class AccountActionsRepository(private val context: Context) {

    /**
     * Administrador de sesiones utilizado para recuperar
     * el token de acceso del usuario autenticado.
     */
    private val sessions = SessionManager(context)

    /**
     * Cliente HTTP utilizado para enviar las solicitudes
     * relacionadas con la administración de cuentas.
     */
    private val client = OkHttpClient()

    /**
     * Dirección base del backend de RIETI desplegado
     * mediante AWS Lambda Function URL.
     */
    private val base = "https://tdjmvpcukz452yjfd4pkjuae7i0yajrv.lambda-url.us-east-1.on.aws/"

    /**
     * Realiza una solicitud HTTP POST autenticada
     * hacia un endpoint del backend de RIETI.
     *
     * La operación se ejecuta en Dispatchers.IO para
     * realizar la comunicación de red fuera del hilo principal.
     *
     * Recupera el token de acceso mediante SessionManager
     * y lo incorpora en el encabezado Authorization.
     *
     * Envía los datos en formato JSON y verifica
     * que la respuesta HTTP sea exitosa y que el
     * servidor devuelva el indicador "ok" con valor true.
     *
     * @param path Ruta relativa del endpoint solicitado.
     * @param data Objeto JSON con los datos de la solicitud.
     *
     * @throws IllegalStateException Si no existe un token
     * de acceso o si el backend devuelve un error.
     * @throws Exception Si ocurre un problema de comunicación
     * o procesamiento de la respuesta.
     */
    private suspend fun post(path: String, data: JSONObject) = withContext(Dispatchers.IO) {
        val token = sessions.accessToken()?.takeIf { it.isNotBlank() }
            ?: error("Inicia sesion nuevamente")
        val request = Request.Builder().url(base + path)
            .header("Authorization", "Bearer $token")
            .post(data.toString().toRequestBody("application/json".toMediaType()))
            .build()
        AuthenticatedRequests(context).execute(client, request).use { response ->
            val dataResponse = runCatching { JSONObject(response.body?.string().orEmpty()) }.getOrNull()
            if (!response.isSuccessful || dataResponse?.optBoolean("ok") != true) {
                error(dataResponse?.optJSONObject("error")?.optString("message")
                    ?.takeIf { it.isNotBlank() } ?: "Error HTTP ${response.code}")
            }
        }
    }

    /**
     * Solicita el cambio de contraseña de la cuenta
     * del usuario autenticado.
     *
     * Comprueba que la nueva contraseña tenga entre
     * 10 y 128 caracteres antes de enviar la solicitud.
     *
     * Envía la contraseña actual y la nueva contraseña
     * al endpoint POST /mobile/cuenta/cambiar-password.
     *
     * @param current Contraseña actual del usuario.
     * @param next Nueva contraseña que se desea establecer.
     *
     * @throws IllegalArgumentException Si la nueva contraseña
     * no cumple con la longitud permitida.
     * @throws IllegalStateException Si el usuario no tiene
     * una sesión disponible o el backend rechaza la solicitud.
     */
    suspend fun changePassword(current: String, next: String) {
        require(next.length in 10..128) { "La nueva contrasena debe tener entre 10 y 128 caracteres" }
        post("mobile/cuenta/cambiar-password", JSONObject()
            .put("currentPassword", current).put("newPassword", next))
    }

    /**
     * Solicita la desactivación de la cuenta
     * del usuario autenticado.
     *
     * Verifica que se haya proporcionado una contraseña
     * y envía la solicitud al endpoint
     * POST /mobile/cuenta/desactivar.
     *
     * @param password Contraseña proporcionada
     * para confirmar la solicitud de desactivación.
     *
     * @throws IllegalArgumentException Si la contraseña
     * está vacía o contiene únicamente espacios.
     * @throws IllegalStateException Si no existe una sesión
     * disponible o el backend rechaza la operación.
     */
    suspend fun deactivate(password: String) {
        require(password.isNotBlank()) { "Confirma tu contrasena" }
        post("mobile/cuenta/desactivar", JSONObject().put("password", password))
    }
}
