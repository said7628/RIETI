
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
 * Administra las solicitudes HTTP autenticadas de RIETI.
 *
 * Permite ejecutar peticiones utilizando un token de acceso
 * y renovar las credenciales cuando el token está próximo
 * a vencer o cuando el servidor responde con HTTP 401.
 *
 * Utiliza SessionManager para consultar y actualizar
 * la información de la sesión, así como AuthRepository
 * para solicitar la renovación del token.
 *
 * Implementa un Mutex compartido para evitar que
 * varias solicitudes intenten renovar las credenciales
 * simultáneamente.
 *
 * También verifica el límite de siete días de inactividad
 * establecido para las sesiones y permite intentar
 * su renovación cuando se recupera la conectividad.
 *
 * Autor original: Said Valseca Figueroa.
 *
 * @param context Contexto de Android utilizado
 * para acceder al administrador de sesiones.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
class AuthenticatedRequests(context: Context) {

    /**
     * Administrador encargado de consultar,
     * almacenar y eliminar los datos de sesión.
     *
     * Utiliza el contexto de aplicación para
     * evitar depender del ciclo de vida de una Activity.
     */
    private val sessions = SessionManager(context.applicationContext)

    /**
     * Contiene los recursos compartidos entre
     * las instancias de AuthenticatedRequests.
     */
    companion object {

        /**
         * Mutex compartido que sincroniza las operaciones
         * de renovación de tokens.
         *
         * Evita que varias solicitudes concurrentes
         * ejecuten el proceso de renovación al mismo tiempo.
         */
        private val refreshMutex = Mutex()
    }

    /**
     * Comprueba si un token está próximo a vencer.
     *
     * Intenta interpretar el token como un JWT
     * y recuperar el campo "exp" de su contenido.
     *
     * Decodifica la segunda sección del token
     * utilizando Base64 URL_SAFE y obtiene
     * la fecha de expiración expresada en segundos.
     *
     * Considera que el token está próximo a vencer
     * cuando su expiración ocurre dentro de los
     * siguientes 90 segundos.
     *
     * Si el token no puede interpretarse, devuelve
     * false para permitir que el servidor determine
     * su validez.
     *
     * @param token Token de acceso cuya fecha
     * de expiración se desea comprobar.
     *
     * @return true si el token tiene una fecha
     * de expiración válida y está próximo a vencer;
     * false en caso contrario.
     */
    private fun expiresSoon(token: String): Boolean {
        return try {
            val piece = token.split(".")[1]

            val decoded = String(
                Base64.decode(
                    piece,
                    Base64.URL_SAFE or Base64.NO_WRAP
                )
            )

            val expiry = JSONObject(decoded).optLong("exp")

            expiry > 0 &&
                    expiry * 1000L <
                    System.currentTimeMillis() + 90_000L

        } catch (_: Exception) {
            false // Un token opaco lo validara el servidor.
        }
    }

    /**
     * Renueva el token de acceso de la sesión.
     *
     * Utiliza refreshMutex.withLock para sincronizar
     * las solicitudes concurrentes de renovación.
     *
     * Antes de renovar, verifica que la sesión
     * continúe dentro del límite de siete días
     * de inactividad.
     *
     * Si otra solicitud ya renovó el token,
     * reutiliza el nuevo valor almacenado
     * en SessionManager.
     *
     * En caso contrario, recupera el refresh token
     * y solicita nuevas credenciales mediante
     * AuthRepository.refresh().
     *
     * Si la renovación es exitosa, actualiza
     * la información de sesión y devuelve
     * el nuevo token de acceso.
     *
     * Si el refresh token es inválido o el servidor
     * devuelve un error de autorización, elimina
     * los datos de sesión almacenados.
     *
     * @param previousAccess Token de acceso utilizado
     * antes de intentar la renovación.
     *
     * @return Token de acceso actualizado,
     * recuperado o generado mediante la renovación.
     *
     * @throws IllegalStateException Si la sesión
     * ha expirado, no existen credenciales válidas
     * o no se puede completar la renovación.
     */
    private suspend fun renew(
        previousAccess: String
    ): String = refreshMutex.withLock {

        if (!sessions.isWithinSevenDays()) {
            sessions.clear()
            error(
                "Tu sesion cumplio siete dias sin actividad. Inicia sesion nuevamente."
            )
        }

        val current = sessions.accessToken().orEmpty()

        if (current.isNotBlank() && current != previousAccess) {
            return@withLock current
        }

        val refresh = sessions.refreshToken()
            ?.takeIf { it.isNotBlank() }
            ?: error("Inicia sesion para continuar")

        when (val result = AuthRepository().refresh(refresh)) {

            is AuthResult.Success -> {
                sessions.saveRefresh(result.data)

                sessions.accessToken()
                    ?: error("No se recibio un token nuevo")
            }

            is AuthResult.Error -> {

                if (result.code in setOf(
                        "INVALID_REFRESH_TOKEN",
                        "UNAUTHORIZED"
                    )
                ) {
                    sessions.clear()

                    error(
                        "Tu sesion termino. Inicia sesion nuevamente."
                    )
                }

                error(
                    "No se pudo renovar la sesion. Revisa tu conexion e intenta de nuevo."
                )
            }
        }
    }

    /**
     * Intenta renovar el token cuando se recupera
     * la conexión a Internet.
     *
     * Primero verifica que exista una sesión activa.
     *
     * Después consulta la fecha de la última
     * autenticación exitosa y evita solicitar
     * una renovación si han transcurrido
     * menos de cinco minutos.
     *
     * Si existe un token de acceso y se cumple
     * el intervalo establecido, llama a renew()
     * para intentar actualizar las credenciales.
     *
     * @throws IllegalStateException Si la sesión
     * ya no es válida o falla la renovación.
     */
    suspend fun onConnectionRestored() {

        if (!sessions.hasSession()) return

        val last = sessions.lastSuccessfulAuthAt()

        if (
            last > 0L &&
            System.currentTimeMillis() - last <
            5L * 60L * 1000L
        ) return

        val current = sessions.accessToken() ?: return

        renew(current)
    }

    /**
     * Ejecuta una solicitud HTTP autenticada.
     *
     * Comprueba que la sesión se encuentre dentro
     * del límite de siete días de inactividad
     * y recupera el token de acceso almacenado.
     *
     * Si el token está próximo a vencer,
     * solicita su renovación antes de ejecutar
     * la petición.
     *
     * Agrega el encabezado Authorization con
     * el esquema Bearer y ejecuta la solicitud
     * mediante OkHttp en Dispatchers.IO.
     *
     * Si el servidor responde con HTTP 401,
     * cierra la respuesta original, intenta
     * renovar el token y repite la petición
     * una sola vez con las nuevas credenciales.
     *
     * Si la respuesta inicial no es HTTP 401,
     * la devuelve sin realizar otro intento.
     *
     * @param client Cliente OkHttp utilizado
     * para ejecutar la solicitud de red.
     * @param request Solicitud HTTP original
     * a la que se agregará el token de acceso.
     *
     * @return Respuesta HTTP de la solicitud
     * original o del único reintento realizado.
     *
     * @throws IllegalStateException Si la sesión
     * no es válida o no puede renovarse.
     * @throws java.io.IOException Si ocurre
     * un error de comunicación durante la petición.
     */
    suspend fun execute(
        client: OkHttpClient,
        request: Request
    ): Response {

        if (!sessions.isWithinSevenDays()) {
            sessions.clear()

            error(
                "Tu sesion cumplio siete dias sin actividad. Inicia sesion nuevamente."
            )
        }

        var token = sessions.accessToken()
            ?.takeIf { it.isNotBlank() }
            ?: error("Inicia sesion para continuar")

        if (expiresSoon(token)) {
            token = renew(token)
        }

        fun withToken(access: String): Request =
            request.newBuilder()
                .header("Authorization", "Bearer $access")
                .build()

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
