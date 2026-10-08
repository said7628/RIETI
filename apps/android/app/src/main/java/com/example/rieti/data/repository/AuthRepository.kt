package com.example.rieti.data.repository

import com.example.rieti.data.network.ApiClient
import com.example.rieti.data.network.ApiErrorResponse
import com.example.rieti.data.network.LoginRequest
import com.example.rieti.data.network.LoginResponse
import com.example.rieti.data.network.LogoutRequest
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

    private fun parseError(
        raw: String?
    ): AuthResult.Error {

        if(raw.isNullOrBlank()) {
            return AuthResult.Error(
                "Ocurrió un error"
            )
        }

        return try {

            val parsed =
                gson.fromJson(
                    raw,
                    ApiErrorResponse::class.java
                )

            AuthResult.Error(
                message =
                    parsed.error?.message
                        ?: "Ocurrió un error",

                code =
                    parsed.error?.code
            )

        } catch(e: Exception) {

            AuthResult.Error(
                "Ocurrió un error"
            )
        }
    }
}
