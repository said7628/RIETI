package com.example.rieti.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RietiApi {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequest
    ): Response<RefreshResponse>

    @POST("auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): Response<BasicResponse>

    @POST("auth/recovery/request")
    suspend fun requestRecovery(
        @Body request: RecoveryRequest
    ): Response<BasicResponse>

    @POST("auth/recovery/reset")
    suspend fun resetPassword(
        @Body request: RecoveryResetRequest
    ): Response<BasicResponse>
}
