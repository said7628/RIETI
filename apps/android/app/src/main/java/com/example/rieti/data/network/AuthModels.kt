package com.example.rieti.data.network

/**
 * Representa los datos enviados al backend para registrar
 * una nueva cuenta de usuario en RIETI.
 *
 * @author Equipo 3:
 *  Lorena Antonio Monraga,
 *  Eduardo Márquez López,
 *  Ángel Adrik Merino Cedeño,
 *  Marco Polo Peña Rivera,
 *  Said Valseca Figueroa.
 *
 * @property name Nombre del usuario que solicita el registro.
 * @property email Correo electrónico asociado a la cuenta.
 * @property password Contraseña proporcionada para el registro.
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

/**
 * Representa la respuesta del backend ante una solicitud
 * de registro de usuario.
 *
 * @property ok Indica si la operación fue exitosa.
 * @property message Mensaje opcional devuelto por el servidor.
 */
data class RegisterResponse(
    val ok: Boolean,
    val message: String? = null
)

/**
 * Contiene las credenciales enviadas al backend
 * para autenticar a un usuario de RIETI.
 *
 * @property email Correo electrónico del usuario.
 * @property password Contraseña de acceso a la cuenta.
 * @property platform Plataforma desde la que se solicita
 * la autenticación. Su valor predeterminado es "android".
 */
data class LoginRequest(
    val email: String,
    val password: String,
    val platform: String = "android"
)

/**
 * Representa la información de un usuario recibida
 * desde los servicios del backend de RIETI.
 *
 * @property id Identificador numérico del usuario.
 * @property uuid Identificador único del usuario.
 * @property name Nombre del usuario.
 * @property email Correo electrónico registrado.
 * @property phone Número telefónico opcional.
 * @property role Identificador textual del rol del usuario.
 * @property roleCode Código correspondiente al rol.
 * @property roleName Nombre descriptivo del rol.
 * @property municipalityId Identificador opcional del municipio.
 * @property dependencyId Identificador opcional de la dependencia.
 * @property active Indica el estado de activación de la cuenta.
 */
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

/**
 * Representa la respuesta del backend cuando
 * se realiza una solicitud de inicio de sesión.
 *
 * @property ok Indica si la operación fue exitosa.
 * @property user Información del usuario autenticado.
 * @property accessToken Token utilizado para acceder
 * a los servicios protegidos de la API.
 * @property refreshToken Token utilizado para solicitar
 * la renovación de la sesión.
 */
data class LoginResponse(
    val ok: Boolean,
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String
)

/**
 * Contiene la información requerida para solicitar
 * la renovación de una sesión de usuario.
 *
 * @property refreshToken Token de renovación
 * correspondiente a la sesión.
 */
data class RefreshRequest(
    val refreshToken: String
)

/**
 * Representa la respuesta del backend después
 * de solicitar la renovación de una sesión.
 *
 * @property ok Indica si la operación fue exitosa.
 * @property accessToken Nuevo token de acceso.
 * @property refreshToken Token de renovación
 * devuelto por el backend.
 * @property user Información del usuario asociado
 * a la sesión renovada.
 */
data class RefreshResponse(
    val ok: Boolean,
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

/**
 * Contiene el token requerido para solicitar
 * el cierre de sesión de un usuario.
 *
 * @property refreshToken Token de renovación
 * asociado a la sesión que se desea cerrar.
 */
data class LogoutRequest(
    val refreshToken: String
)

/**
 * Representa la solicitud para iniciar el proceso
 * de recuperación de contraseña de una cuenta.
 *
 * @property email Correo electrónico de la cuenta
 * para la cual se solicita la recuperación.
 */
data class RecoveryRequest(
    val email: String
)

/**
 * Contiene los datos necesarios para solicitar
 * el restablecimiento de una contraseña.
 *
 * @property email Correo electrónico asociado a la cuenta.
 * @property code Código de recuperación proporcionado
 * para el proceso de restablecimiento.
 * @property password Nueva contraseña solicitada
 * para la cuenta.
 */
data class RecoveryResetRequest(
    val email: String,
    val code: String,
    val password: String
)

/**
 * Representa una respuesta general del backend
 * para operaciones que no requieren devolver
 * información adicional estructurada.
 *
 * @property ok Indica si la operación fue exitosa.
 * @property message Mensaje opcional del servidor.
 */
data class BasicResponse(
    val ok: Boolean,
    val message: String? = null
)

/**
* Representa la estructura de una respuesta de error
* recibida desde la API de RIETI.
*
* @property ok Indicador opcional del resultado
* de la operación.
* @property error Información opcional que describe
* el error devuelto por el backend.
*/
data class ApiErrorResponse(
    val ok: Boolean? = null,
    val error: ApiError? = null
)

/**
 * Contiene los detalles de un error generado
 * durante el procesamiento de una solicitud.
 *
 * @property code Código opcional que identifica
 * el tipo de error.
 * @property message Descripción opcional del error.
 */
data class ApiError(
    val code: String? = null,
    val message: String? = null
)
