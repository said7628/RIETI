package com.example.rieti.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Define los endpoints de autenticación y administración
 * de sesiones utilizados por la aplicación Android de RIETI.
 *
 * La interfaz utiliza Retrofit para declarar solicitudes HTTP
 * de tipo POST hacia el backend.
 *
 * Las funciones suspend permiten ejecutar las solicitudes
 * desde corrutinas sin bloquear el hilo principal.
 *
 * Los datos de entrada se envían en el cuerpo de las
 * solicitudes y las respuestas incluyen información HTTP
 * junto con los modelos definidos para cada operación.
 *
 * @author Equipo 3:
 *  Lorena Antonio Monraga,
 *  Eduardo Márquez López,
 *  Ángel Adrik Merino Cedeño,
 *  Marco Polo Peña Rivera,
 *  Said Valseca Figueroa.
 */
interface RietiApi {
    /**
     * Solicita el registro de una nueva cuenta ciudadana.
     *
     * Endpoint: POST /auth/register
     *
     * @param request Datos requeridos para registrar
     * una cuenta ciudadana.
     * @return Respuesta HTTP que contiene un
     * [RegisterResponse] con el resultado del registro.
     */
    @POST("auth/register/request")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    /** Verifica el codigo recibido por correo y activa la cuenta. */
    @POST("auth/register/verify")
    suspend fun verifyRegistration(
        @Body request: RegisterVerifyRequest
    ): Response<RegisterResponse>

    /**
     * Solicita la autenticación de un usuario en RIETI.
     *
     * Endpoint: POST /auth/login
     *
     * @param request Credenciales proporcionadas
     * por el usuario para iniciar sesión.
     * @return Respuesta HTTP que contiene un
     * [LoginResponse] con el resultado de la autenticación.
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    /**
     * Solicita la renovación de una sesión de usuario.
     *
     * Endpoint: POST /auth/refresh
     *
     * @param request Datos necesarios para solicitar
     * la renovación de la sesión.
     * @return Respuesta HTTP que contiene un
     * [RefreshResponse] con el resultado de la operación.
     */
    @POST("auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequest
    ): Response<RefreshResponse>

    /**
     * Solicita el cierre de sesión de un usuario.
     *
     * Endpoint: POST /auth/logout
     *
     * @param request Datos requeridos por el backend
     * para procesar el cierre de sesión.
     * @return Respuesta HTTP que contiene un
     * [BasicResponse] con el resultado de la operación.
     */
    @POST("auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): Response<BasicResponse>

    /**
     * Solicita el inicio del proceso de recuperación
     * de contraseña de una cuenta.
     *
     * Endpoint: POST /auth/recovery/request
     *
     * @param request Información necesaria para
     * solicitar la recuperación de contraseña.
     * @return Respuesta HTTP que contiene un
     * [BasicResponse] con el resultado de la solicitud.
     */
    @POST("auth/recovery/request")
    suspend fun requestRecovery(
        @Body request: RecoveryRequest
    ): Response<BasicResponse>

    /**
     * Solicita el restablecimiento de la contraseña
     * de una cuenta mediante el proceso de recuperación.
     *
     * Endpoint: POST /auth/recovery/reset
     *
     * @param request Datos necesarios para validar
     * la solicitud y establecer una nueva contraseña.
     * @return Respuesta HTTP que contiene un
     * [BasicResponse] con el resultado del restablecimiento.
     */
    @POST("auth/recovery/reset")
    suspend fun resetPassword(
        @Body request: RecoveryResetRequest
    ): Response<BasicResponse>
}
