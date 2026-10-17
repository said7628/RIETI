package com.example.rieti.data.repository

import com.example.rieti.data.network.ApiClient
import com.example.rieti.data.network.ApiErrorResponse
import com.example.rieti.data.network.LoginRequest
import com.example.rieti.data.network.LoginResponse
import com.example.rieti.data.network.LogoutRequest
import com.example.rieti.data.network.RecoveryRequest
import com.example.rieti.data.network.RecoveryResetRequest
import com.example.rieti.data.network.RegisterVerifyRequest
import com.example.rieti.data.network.RegisterRequest
import com.example.rieti.data.network.RefreshRequest
import com.example.rieti.data.network.RefreshResponse
import com.google.gson.Gson

/**
 * Representa los posibles resultados de una operación
 * de autenticación realizada mediante el backend de RIETI.
 *
 * Permite distinguir entre una operación exitosa y una
 * operación que devuelve un error.
 *
 * @author Equipo 3:
 *  Lorena Antonio Monraga,
 *  Eduardo Márquez López,
 *  Ángel Adrik Merino Cedeño,
 *  Marco Polo Peña Rivera,
 *  Said Valseca Figueroa.
 *
 * @param T Tipo de dato devuelto cuando la operación
 * se completa correctamente.
 */
sealed class AuthResult<out T> {
    /**
     * Representa el resultado exitoso de una operación.
     *
     * @property data Información obtenida como resultado
     * de la operación realizada.
     */

    data class Success<T>(
        val data: T
    ) : AuthResult<T>()

    /**
     * Representa un resultado de error durante
     * una operación de autenticación.
     *
     * @property message Mensaje descriptivo del error.
     * @property code Código opcional proporcionado
     * por el backend para identificar el error.
     */
    data class Error(
        val message: String,
        val code: String? = null
    ) : AuthResult<Nothing>()
}

/**
 * Repositorio encargado de gestionar las operaciones
 * de autenticación y recuperación de cuentas en RIETI.
 *
 * Utiliza [ApiClient] para comunicarse con el backend
 * y transforma las respuestas HTTP en objetos [AuthResult].
 *
 * Incluye operaciones de registro, inicio de sesión,
 * renovación y cierre de sesión, así como recuperación
 * y restablecimiento de contraseñas.
 */
class AuthRepository {
    /**
     * Instancia de la API utilizada para ejecutar
     * las solicitudes HTTP de autenticación.
     */
    private val api =
        ApiClient.api

    /**
     * Conversor JSON utilizado para interpretar
     * las respuestas de error enviadas por el backend.
     */
    private val gson =
        Gson()

    /**
     * Solicita el registro de una nueva cuenta en RIETI.
     *
     * Elimina espacios al inicio y final del nombre y correo,
     * y convierte el correo electrónico a minúsculas antes
     * de enviar la solicitud.
     *
     * @param name Nombre proporcionado para el registro.
     * @param email Correo electrónico de la nueva cuenta.
     * @param password Contraseña proporcionada por el usuario.
     * @return [AuthResult.Success] si la respuesta HTTP es
     * exitosa, o [AuthResult.Error] si ocurre un error.
     */
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): AuthResult<Unit> {

        return try {

            val response =
                api.register(
                    RegisterRequest(
                        name = name.trim(),
                        email = email.trim().lowercase(),
                        password = password
                    )
                )

            if (response.isSuccessful) {

                AuthResult.Success(Unit)

            } else {

                parseError(
                    response.errorBody()?.string()
                )
            }

        } catch (e: Exception) {

            AuthResult.Error(
                "No fue posible conectar con RIETI. Revisa tu conexión."
            )
        }
    }

    /** Confirma el codigo del correo para terminar el alta de la cuenta. */
    suspend fun verifyRegistration(email: String, code: String): AuthResult<Unit> {
        return try {
            val response = api.verifyRegistration(
                RegisterVerifyRequest(email.trim().lowercase(), code.trim())
            )
            if (response.isSuccessful && response.body()?.ok == true) {
                AuthResult.Success(Unit)
            } else {
                parseError(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            AuthResult.Error("No pudimos validar el codigo. Revisa tu conexion.")
        }
    }

    /**
     * Solicita la autenticación de un usuario en RIETI.
     *
     * Normaliza el correo electrónico y envía las credenciales
     * indicando que la solicitud proviene de Android.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña de acceso.
     * @return [AuthResult.Success] con la información
     * de [LoginResponse], o [AuthResult.Error] si la
     * autenticación falla o la respuesta es inválida.
     */
    suspend fun login(
        email: String,
        password: String
    ): AuthResult<LoginResponse> {

        return try {

            val response =
                api.login(
                    LoginRequest(
                        email = email.trim().lowercase(),
                        password = password,
                        platform = "android"
                    )
                )

            if(response.isSuccessful) {

                val body =
                    response.body()

                if(body != null) {
                    AuthResult.Success(body)
                } else {
                    AuthResult.Error(
                        "Respuesta inválida del servidor"
                    )
                }

            } else {

                parseError(
                    response.errorBody()?.string()
                )
            }

        } catch(e: Exception) {

            AuthResult.Error(
                "No fue posible conectar con RIETI. Revisa tu conexión."
            )
        }
    }

    /**
     * Solicita la renovación de una sesión autenticada.
     *
     * Envía el token de renovación al backend y procesa
     * los nuevos datos de sesión recibidos.
     *
     * @param refreshToken Token utilizado para solicitar
     * la renovación de la sesión.
     * @return [AuthResult.Success] con [RefreshResponse]
     * cuando la operación es exitosa, o [AuthResult.Error]
     * cuando ocurre un error.
     */
    suspend fun refresh(
        refreshToken: String
    ): AuthResult<RefreshResponse> {

        return try {

            val response =
                api.refresh(
                    RefreshRequest(
                        refreshToken
                    )
                )

            if(response.isSuccessful) {

                val body =
                    response.body()

                if(body != null) {
                    AuthResult.Success(body)
                } else {
                    AuthResult.Error(
                        "Respuesta inválida del servidor"
                    )
                }

            } else {

                parseError(
                    response.errorBody()?.string()
                )
            }

        } catch(e: Exception) {

            AuthResult.Error(
                "No fue posible renovar la sesión"
            )
        }
    }

    /**
     * Solicita al backend el cierre de una sesión.
     *
     * Envía el token de renovación correspondiente
     * para que el servidor procese la solicitud.
     *
     * @param refreshToken Token asociado a la sesión.
     * @return [AuthResult.Success] si la respuesta HTTP
     * es exitosa, o [AuthResult.Error] si ocurre un error.
     */
    suspend fun logout(
        refreshToken: String
    ): AuthResult<Unit> {

        return try {

            val response =
                api.logout(
                    LogoutRequest(
                        refreshToken = refreshToken
                    )
                )

            if (response.isSuccessful) {
                AuthResult.Success(Unit)
            } else {
                parseError(
                    response.errorBody()?.string()
                )
            }

        } catch (e: Exception) {

            AuthResult.Error(
                "No fue posible cerrar la sesión en el servidor"
            )
        }
    }

    /**
     * Solicita el inicio del proceso de recuperación
     * de contraseña de una cuenta.
     *
     * Normaliza el correo electrónico y procesa
     * el mensaje devuelto por el backend.
     *
     * @param email Correo electrónico de la cuenta.
     * @return [AuthResult.Success] con el mensaje
     * de respuesta, o [AuthResult.Error] si falla
     * la solicitud.
     */
    suspend fun requestRecovery(
        email: String
    ): AuthResult<String> {

        return try {

            val response =
                api.requestRecovery(
                    RecoveryRequest(
                        email = email.trim().lowercase()
                    )
                )

            if (response.isSuccessful) {

                AuthResult.Success(
                    response.body()?.message
                        ?: "Si existe una cuenta activa con ese correo, recibirás un código de recuperación."
                )

            } else {

                val parsed = parseError(response.errorBody()?.string())
                if (parsed.message.startsWith("El servidor ") || parsed.message.startsWith("No se pudo interpretar")) {
                    AuthResult.Error("No fue posible enviar el codigo (HTTP ${response.code()}).", parsed.code)
                } else parsed
            }

        } catch (e: Exception) {

            AuthResult.Error(
                "No fue posible solicitar el codigo. Revisa tu conexion."
            )
        }
    }

    /**
     * Solicita el restablecimiento de la contraseña
     * mediante el código de recuperación.
     *
     * Normaliza el correo y elimina espacios adicionales
     * del código antes de enviar los datos al backend.
     *
     * @param email Correo electrónico asociado a la cuenta.
     * @param code Código utilizado para la recuperación.
     * @param password Nueva contraseña proporcionada.
     * @return [AuthResult.Success] si la respuesta HTTP
     * es exitosa, o [AuthResult.Error] si ocurre un error.
     */
    suspend fun resetPassword(
        email: String,
        code: String,
        password: String
    ): AuthResult<Unit> {

        return try {

            val response =
                api.resetPassword(
                    RecoveryResetRequest(
                        email = email.trim().lowercase(),
                        code = code.trim(),
                        password = password
                    )
                )

            if (response.isSuccessful) {
                AuthResult.Success(Unit)
            } else {
                parseError(
                    response.errorBody()?.string()
                )
            }

        } catch (e: Exception) {

            AuthResult.Error(
                "No fue posible restablecer la contraseña."
            )
        }
    }

    /**
     * Interpreta el contenido de una respuesta de error
     * recibida desde el backend de RIETI.
     *
     * Utiliza Gson para convertir el JSON en un objeto
     * [ApiErrorResponse] y extraer su código y mensaje.
     *
     * Cuando la respuesta está vacía, no contiene detalles
     * o no puede interpretarse, devuelve un mensaje
     * alternativo para informar del problema.
     *
     * @param raw Contenido JSON de la respuesta de error,
     * que puede ser nulo.
     * @return [AuthResult.Error] con el mensaje y código
     * identificados, cuando están disponibles.
     */
    private fun parseError(raw: String?): AuthResult.Error {
        if (raw.isNullOrBlank()) {
            return AuthResult.Error("El servidor no devolvio detalles del error. Intenta mas tarde.")
        }
        return try {
            val parsed = gson.fromJson(raw, ApiErrorResponse::class.java)
            val code = parsed?.error?.code
            val detail = parsed?.error?.message?.trim()
            AuthResult.Error(
                message = if (!detail.isNullOrBlank()) detail else "El servidor rechazo la solicitud. Intenta mas tarde.",
                code = code
            )
        } catch (e: Exception) {
            AuthResult.Error("No se pudo interpretar la respuesta del servidor.")
        }
    }
}
