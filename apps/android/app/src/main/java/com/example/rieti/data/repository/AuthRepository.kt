package com.example.rieti.data.repository

import com.example.rieti.data.network.ApiClient
import com.example.rieti.data.network.ApiErrorResponse
import com.example.rieti.data.network.LoginRequest
import com.example.rieti.data.network.LoginResponse
import com.example.rieti.data.network.LogoutRequest
import com.example.rieti.data.network.RecoveryRequest
import com.example.rieti.data.network.RecoveryResetRequest
import com.example.rieti.data.network.RegisterRequest
import com.example.rieti.data.network.RefreshRequest
import com.example.rieti.data.network.RefreshResponse
import com.google.gson.Gson

sealed class AuthResult<out T> {

    data class Success<T>(
        val data: T
    ) : AuthResult<T>()

    data class Error(
        val message: String,
        val code: String? = null
    ) : AuthResult<Nothing>()
}

class AuthRepository {

    private val api =
        ApiClient.api

    private val gson =
        Gson()

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

            if(response.isSuccessful) {

                AuthResult.Success(Unit)

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
