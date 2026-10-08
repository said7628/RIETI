package com.example.rieti.data.network

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val ok: Boolean,
    val message: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String,
    val platform: String = "android"
)

data class UserDto(
    val id: Long,
    val uuid: String,
    val name: String,
    val email: String,
    val phone: String?,
    val role: String,
    val roleCode: String,
    val roleName: String,
    val municipalityId: Long?,
    val dependencyId: Long?,
    val active: Boolean
)

data class LoginResponse(
    val ok: Boolean,
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String
)

data class RefreshRequest(
    val refreshToken: String
)

data class RefreshResponse(
    val ok: Boolean,
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

data class LogoutRequest(
    val refreshToken: String
)

data class BasicResponse(
    val ok: Boolean,
    val message: String? = null
)

data class ApiErrorResponse(
    val ok: Boolean? = null,
    val error: ApiError? = null
)

data class ApiError(
    val code: String? = null,
    val message: String? = null
)
