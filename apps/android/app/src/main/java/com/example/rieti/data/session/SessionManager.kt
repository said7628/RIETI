package com.example.rieti.data.session

import android.content.Context
import com.example.rieti.data.network.LoginResponse
import com.example.rieti.data.network.RefreshResponse
import com.example.rieti.data.network.UserDto

/**
 * Administra la información de sesión de los usuarios
 * de la aplicación Android de RIETI.
 *
 * Utiliza SharedPreferences para almacenar y recuperar
 * los tokens de autenticación, los datos básicos del usuario
 * y el correo electrónico utilizado durante la recuperación
 * de contraseña.
 *
 * También permite comprobar la existencia de un token
 * de renovación y eliminar los datos de sesión almacenados.
 *
 * @param context Contexto de Android utilizado para acceder
 * al almacenamiento de preferencias de la aplicación.
 *
 * @author Equipo 3:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
class SessionManager(
    context: Context
) {
    /**
     * Preferencias privadas de la aplicación utilizadas
     * para conservar la información de sesión.
     *
     * Los datos se almacenan bajo el nombre "rieti_session".
     */
    private val prefs =
        context.getSharedPreferences(
            "rieti_session",
            Context.MODE_PRIVATE
        )

    /**
     * Guarda los datos de sesión recibidos después
     * de una solicitud de inicio de sesión.
     *
     * Delega el almacenamiento de los tokens y la
     * información del usuario en [saveSession].
     *
     * @param response Respuesta de autenticación que
     * contiene los tokens y los datos del usuario.
     */
    fun saveLogin(
        response: LoginResponse
    ) {

        saveSession(
            response.accessToken,
            response.refreshToken,
            response.user
        )
    }

    /**
     * Actualiza la información de sesión después
     * de renovar los tokens de autenticación.
     *
     * Utiliza [saveSession] para guardar los nuevos
     * tokens y los datos del usuario recibidos.
     *
     * @param response Respuesta del backend con
     * la información de la sesión renovada.
     */
    fun saveRefresh(
        response: RefreshResponse
    ) {

        saveSession(
            response.accessToken,
            response.refreshToken,
            response.user
        )
    }

    /**
     * Almacena los tokens de autenticación y los datos
     * básicos del usuario en SharedPreferences.
     *
     * Guarda el token de acceso, el token de renovación,
     * el identificador del usuario, su nombre, correo
     * electrónico y código de rol.
     *
     * @param accessToken Token utilizado para acceder
     * a los servicios protegidos del backend.
     * @param refreshToken Token utilizado para solicitar
     * la renovación de la sesión.
     * @param user Información del usuario autenticado.
     */
    fun saveSession(
        accessToken: String,
        refreshToken: String,
        user: UserDto
    ) {

        prefs.edit()
            .putString(
                "access_token",
                accessToken
            )
            .putString(
                "refresh_token",
                refreshToken
            )
            .putLong(
                "user_id",
                user.id
            )
            .putString(
                "name",
                user.name
            )
            .putString(
                "email",
                user.email
            )
            .putString(
                "role",
                user.roleCode
            )
            // Solo una renovacion validada por el servidor extiende la sesion.
            .putLong("last_successful_auth_ms", System.currentTimeMillis())
            .apply()
    }

    /**
     * Guarda el correo electrónico utilizado durante
     * el proceso de recuperación de contraseña.
     *
     * Antes de almacenarlo, elimina espacios al inicio
     * y final y convierte su contenido a minúsculas.
     *
     * @param email Correo electrónico asociado
     * a la solicitud de recuperación.
     */
    fun saveRecoveryEmail(
        email: String
    ) {
        prefs.edit()
            .putString(
                "recovery_email",
                email.trim().lowercase()
            )
            .apply()
    }

    /**
     * Obtiene el correo electrónico almacenado
     * para el proceso de recuperación de contraseña.
     *
     * @return Correo electrónico guardado o null
     * si no existe un valor almacenado.
     */
    fun recoveryEmail(): String? =
        prefs.getString(
            "recovery_email",
            null
        )

    /**
     * Recupera el token de acceso de la sesión.
     *
     * @return Token de acceso almacenado o null
     * si no existe.
     */
    fun accessToken(): String? =
        prefs.getString(
            "access_token",
            null
        )

    /**
     * Recupera el token de renovación de la sesión.
     *
     * @return Token de renovación almacenado o null
     * si no existe.
     */
    fun refreshToken(): String? =
        prefs.getString(
            "refresh_token",
            null
        )

    /**
     * Obtiene el correo electrónico del usuario
     * asociado a la sesión almacenada.
     *
     * @return Correo electrónico del usuario o null
     * si no se encuentra almacenado.
     */
    fun userEmail(): String? =
        prefs.getString(
            "email",
            null
        )
    /**
     * Obtiene el nombre del usuario autenticado
     * a partir de las preferencias almacenadas.
     *
     * @return Nombre del usuario o null
     * si no existe un valor guardado.
     */
    fun userName(): String? =
        prefs.getString(
            "name",
            null
        )

    /**
     * Recupera el código del rol del usuario
     * asociado a la sesión almacenada.
     *
     * @return Código del rol o null
     * si no se encuentra disponible.
     */
    fun roleCode(): String? =
        prefs.getString(
            "role",
            null
        )

    /**
     * Comprueba si existe un token de renovación
     * almacenado y si su contenido no está vacío.
     *
     * Esta comprobación no verifica la vigencia
     * ni la validez del token ante el backend.
     *
     * @return true si existe un token de renovación
     * no vacío; false en caso contrario.
     */
    fun hasSession(): Boolean =
        !refreshToken().isNullOrBlank() && isWithinSevenDays()

    /**
     * La app exige iniciar sesion otra vez tras siete dias sin una
     * autenticacion correcta. Las renovaciones autorizadas reinician
     * esta ventana; abrir la app sin red no la reinicia.
     * Autor: Said Valseca Figueroa.
     */
    fun lastSuccessfulAuthAt(): Long =
        prefs.getLong("last_successful_auth_ms", 0L)

    fun isWithinSevenDays(): Boolean {
        val last = prefs.getLong("last_successful_auth_ms", 0L)
        if (last == 0L) return true // Sesiones anteriores: validar con el servidor.
        val diff = System.currentTimeMillis() - last
        return diff >= 0 && diff < 7L * 24L * 60L * 60L * 1000L
    }

    /**
     * Elimina toda la información almacenada
     * en las preferencias de sesión de RIETI.
     *
     * Borra los tokens, los datos del usuario
     * y el correo de recuperación guardado.
     *
     * La eliminación local no implica por sí misma
     * la revocación de los tokens en el servidor.
     */
    fun clear() {
        prefs.edit()
            .clear()
            .apply()
    }
}
