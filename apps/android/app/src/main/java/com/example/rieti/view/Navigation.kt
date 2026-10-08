
package com.example.rieti.view

import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import com.example.rieti.data.repository.AuthRepository
import com.example.rieti.data.repository.AuthResult
import com.example.rieti.data.session.SessionManager
import kotlinx.coroutines.launch
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// =================================================================
// RUTAS
// =================================================================

object Routes {

    // =============================================================
    // CUENTA
    // =============================================================

    const val START = "start"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val RECOVER_PASSWORD = "recover_password"
    const val RESET_PASSWORD = "reset_password"

    // =============================================================
    // APP PRINCIPAL
    // =============================================================

    const val HOME = "home"
    const val ABOUT_US = "about_us"
    const val ORIENTATION = "orientation"
    const val SETTINGS = "settings"

    // =============================================================
    // REPORTES
    // =============================================================

    const val REPORT = "report"
    const val ANONYMOUS_REPORT = "anonymous_report"
    const val REPORT_SUCCESS = "report_success"
    const val TRACKING = "tracking"

    // =============================================================
    // RECUPERACIÓN DE BORRADORES
    // =============================================================

    const val RESUME_REPORT = "resume_report/{draftId}/{mode}"

    fun resumeReport(
        draftId: String,
        mode: String
    ): String {
        return "resume_report/$draftId/$mode"
    }

    // =============================================================
    // CONFIGURACIÓN
    // =============================================================

    const val CHANGE_PASSWORD = "change_password"
    const val ACCOUNT_DELETED = "account_deleted"
}

// =================================================================
// APP NAVIGATION
// =================================================================

@Composable
fun AppNavigation(
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authRepository = remember {
        AuthRepository()
    }

    val sessionManager = remember {
        SessionManager(context.applicationContext)
    }

    var startDestination by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        val refreshToken = sessionManager.refreshToken()

        if (refreshToken.isNullOrBlank()) {
            startDestination = Routes.START
        } else {
            when (val result = authRepository.refresh(refreshToken)) {
                is AuthResult.Success -> {
                    sessionManager.saveRefresh(result.data)
                    startDestination = Routes.HOME
                }

                is AuthResult.Error -> {
                    sessionManager.clear()
                    startDestination = Routes.START
                }
            }
        }
    }

    if (startDestination == null) {
        CircularProgressIndicator()
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination!!
    ) {

        // =========================================================
        // START
        // =========================================================

        composable(
            route = Routes.START
        ) {

            WelcomeScreen(
                onLoginClick = {
                    navController.navigate(
                        Routes.LOGIN
                    ) {
                        launchSingleTop = true
                    }
                },

                onRegisterClick = {
                    navController.navigate(
                        Routes.REGISTER
                    ) {
                        launchSingleTop = true
                    }
                },

                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        }

        // =========================================================
        // LOGIN
        // =========================================================

        composable(
            route = Routes.LOGIN
        ) {

            LoginScreen(
                onLoginClick = { email, password ->

                    scope.launch {

                        when (
                            val result = authRepository.login(
                                email = email,
                                password = password
                            )
                        ) {

                            is AuthResult.Success -> {

                                sessionManager.saveLogin(
                                    result.data
                                )

                                navController.navigate(
                                    Routes.HOME
                                ) {
                                    popUpTo(
                                        Routes.START
                                    ) {
                                        inclusive = true
                                    }

                                    launchSingleTop = true
                                }
                            }

                            is AuthResult.Error -> {
                                Toast.makeText(
                                    context,
                                    result.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                },

                onRegisterClick = {
                    navController.navigate(
                        Routes.REGISTER
                    ) {
                        popUpTo(
                            Routes.START
                        ) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },

                onForgotPasswordClick = {
                    navController.navigate(
                        Routes.RECOVER_PASSWORD
                    ) {
                        launchSingleTop = true
                    }
                },

                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        }

        // =========================================================
        // REGISTER
        // =========================================================

        composable(
            route = Routes.REGISTER
        ) {

            RegisterScreen(
                onCreateAccountClick = { name, email, password ->

                    scope.launch {

                        when (
                            val result = authRepository.register(
                                name = name,
                                email = email,
                                password = password
                            )
                        ) {

                            is AuthResult.Success -> {

                                Toast.makeText(
                                    context,
                                    "Cuenta creada correctamente. Inicia sesión.",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.navigate(
                                    Routes.LOGIN
                                ) {
                                    popUpTo(
                                        Routes.START
                                    ) {
                                        inclusive = false
                                    }

                                    launchSingleTop = true
                                }
                            }

                            is AuthResult.Error -> {
                                Toast.makeText(
                                    context,
                                    result.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                },

                onLoginClick = {
                    navController.navigate(
                        Routes.LOGIN
                    ) {
                        popUpTo(
                            Routes.START
                        ) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },

                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        }

        // =========================================================
        // RECUPERAR CONTRASEÑA
        // =========================================================

        composable(
            route = Routes.RECOVER_PASSWORD
        ) {

            ForgotPasswordScreen(
                onSendCodeClick = {
                    navController.navigate(
                        Routes.RESET_PASSWORD
                    ) {
                        launchSingleTop = true
                    }
                },

                onLoginClick = {
                    navController.navigate(
                        Routes.LOGIN
                    ) {
                        popUpTo(
                            Routes.START
                        ) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },

                onRegisterClick = {
                    navController.navigate(
                        Routes.REGISTER
                    ) {
                        popUpTo(
                            Routes.START
                        ) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },

                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        }

        // =========================================================
        // RESET PASSWORD
        // =========================================================

        composable(
            route = Routes.RESET_PASSWORD
        ) {

            ResetPasswordScreen(
                onResetPasswordClick = {
                        recoveryCode,
                        newPassword,
                        confirmPassword ->

                    /*
                     * TODO(BACKEND):
                     *
                     * Enviar al backend:
                     * - recoveryCode
                     * - newPassword
                     * - confirmPassword
                     *
                     * Validar el código, actualizar la contraseña
                     * e invalidar las sesiones correspondientes.
                     *
                     * IMPORTANTE:
                     * Navegar únicamente cuando el backend
                     * confirme el restablecimiento.
                     */

                    // TEMPORAL: comportamiento existente.
                    navController.navigate(
                        Routes.START
                    ) {
                        popUpTo(
                            navController.graph.id
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },

                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        }

        // =========================================================
        // HOME
        // =========================================================

        composable(
            route = Routes.HOME
        ) {

            HomeScreen(

                // -------------------------------------------------
                // REPORTE ANÓNIMO
                // -------------------------------------------------

                onAnonymousReportClick = {
                    navController.navigate(
                        Routes.ANONYMOUS_REPORT
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // REPORTE CON SEGUIMIENTO
                // -------------------------------------------------

                onReportClick = {
                    navController.navigate(
                        Routes.REPORT
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // QUIÉNES SOMOS
                // -------------------------------------------------

                onAboutUsClick = {
                    navController.navigate(
                        Routes.ABOUT_US
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // SEGUIMIENTO
                // -------------------------------------------------

                onTrackingClick = {
                    navController.navigate(
                        Routes.TRACKING
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // ORIENTACIÓN
                // -------------------------------------------------

                onOrientationClick = {
                    navController.navigate(
                        Routes.ORIENTATION
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // CONFIGURACIÓN
                // -------------------------------------------------

                onSettingsClick = {
                    navController.navigate(
                        Routes.SETTINGS
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // CONTINUAR BORRADOR
                // -------------------------------------------------

                onContinueDraftClick = { draftId, mode ->

                    // La modalidad proviene del borrador guardado.
                    // Solo permitimos las modalidades conocidas.

                    if (
                        mode == "ANONYMOUS" ||
                        mode == "TRACKING"
                    ) {

                        navController.navigate(
                            Routes.resumeReport(
                                draftId = draftId,
                                mode = mode
                            )
                        ) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }


// =========================================================
// QUIÉNES SOMOS
// =========================================================

        composable(
            route = Routes.ABOUT_US
        ) {

            AboutUsScreen(
                onHomeClick = {
                    navController.navigate(
                        Routes.HOME
                    ) {
                        popUpTo(Routes.HOME) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },

                onSettingsClick = {
                    navController.navigate(
                        Routes.SETTINGS
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

// =========================================================
// ORIENTACIÓN
// =========================================================

        composable(
            route = Routes.ORIENTATION
        ) {

            OrientationScreen(
                onHomeClick = {
                    navController.navigate(
                        Routes.HOME
                    ) {
                        popUpTo(Routes.HOME) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },

                onAboutUsClick = {
                    navController.navigate(
                        Routes.ABOUT_US
                    ) {
                        launchSingleTop = true
                    }
                },

                onSettingsClick = {
                    navController.navigate(
                        Routes.SETTINGS
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

// =========================================================
// REPORTE CON SEGUIMIENTO
// =========================================================

        composable(
            route = Routes.REPORT
        ) {

            ReportScreen(

                onHomeClick = {
                    navController.navigate(
                        Routes.HOME
                    ) {
                        popUpTo(Routes.HOME) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },

                onAboutUsClick = {
                    navController.navigate(
                        Routes.ABOUT_US
                    ) {
                        launchSingleTop = true
                    }
                },

                onSettingsClick = {
                    navController.navigate(
                        Routes.SETTINGS
                    ) {
                        launchSingleTop = true
                    }
                },

                onCurrentLocationClick = {
                    /*
                     * TODO(LOCATION):
                     * Conectar la ubicación actual.
                     */
                },

                onSelectFilesClick = {
                    /*
                     * TODO(EVIDENCE):
                     * Conectar selector de evidencias.
                     */
                },

                onSendReportClick = {
                    /*
                     * TODO(BACKEND):
                     *
                     * Enviar el reporte mediante ViewModel/API.
                     *
                     * Navegar a REPORT_SUCCESS únicamente
                     * cuando el servidor confirme el registro
                     * y devuelva un folio válido.
                     *
                     * No mostrar éxito de forma simulada.
                     */
                }
            )
        }

// =========================================================
// REPORTE ANÓNIMO
// =========================================================

        composable(
            route = Routes.ANONYMOUS_REPORT
        ) {

            AnonymousReportScreen(

                onHomeClick = {
                    navController.navigate(
                        Routes.HOME
                    ) {
                        popUpTo(Routes.HOME) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },

                onAboutUsClick = {
                    navController.navigate(
                        Routes.ABOUT_US
                    ) {
                        launchSingleTop = true
                    }
                },

                onSettingsClick = {
                    navController.navigate(
                        Routes.SETTINGS
                    ) {
                        launchSingleTop = true
                    }
                },

                onCurrentLocationClick = {
                    /*
                     * TODO(LOCATION):
                     * Conectar la ubicación actual.
                     */
                },

                onSelectFilesClick = {
                    /*
                     * TODO(EVIDENCE):
                     * Conectar selector de evidencias.
                     */
                },

                onSendReportClick = {
                    /*
                     * TODO(BACKEND):
                     *
                     * Enviar el reporte en modalidad ANONYMOUS.
                     *
                     * Navegar a REPORT_SUCCESS únicamente
                     * cuando el backend confirme el registro.
                     */
                }
            )
        }

// =========================================================
// CONTINUAR REPORTE GUARDADO
// =========================================================

        composable(
            route = Routes.RESUME_REPORT,

            arguments = listOf(
                navArgument("draftId") {
                    type = NavType.StringType
                },

                navArgument("mode") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val draftId =
                backStackEntry.arguments
                    ?.getString("draftId")

            val mode =
                backStackEntry.arguments
                    ?.getString("mode")

            // =====================================================
            // VALIDACIÓN DE PARÁMETROS
            // =====================================================

            val reportMode =
                when (mode) {
                    "ANONYMOUS" -> ReportMode.ANONYMOUS
                    "TRACKING" -> ReportMode.TRACKING
                    else -> null
                }

            if (
                !draftId.isNullOrBlank() &&
                reportMode != null
            ) {

                // =================================================
                // RECUPERAR FORMULARIO
                // =================================================

                ReportFormScreen(
                    mode = reportMode,
                    draftId = draftId,

                    onHomeClick = {
                        navController.navigate(
                            Routes.HOME
                        ) {
                            popUpTo(Routes.HOME) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },

                    onAboutUsClick = {
                        navController.navigate(
                            Routes.ABOUT_US
                        ) {
                            launchSingleTop = true
                        }
                    },

                    onSettingsClick = {
                        navController.navigate(
                            Routes.SETTINGS
                        ) {
                            launchSingleTop = true
                        }
                    },

                    onCurrentLocationClick = {
                        /*
                         * TODO(LOCATION):
                         * Conectar ubicación actual.
                         */
                    },

                    onSelectFilesClick = {
                        /*
                         * TODO(EVIDENCE):
                         * Recuperar y administrar evidencias.
                         */
                    },

                    onSendReportClick = {
                        /*
                         * TODO(BACKEND):
                         *
                         * Enviar reporte recuperado.
                         *
                         * No eliminar el borrador ni mostrar
                         * éxito hasta confirmar el registro.
                         */
                    }
                )

            } else {

                // =================================================
                // RUTA INVÁLIDA
                // =================================================

                androidx.compose.material3.Text(
                    text = "No fue posible abrir el borrador solicitado."
                )
            }
        }


// =========================================================
// REPORTE EXITOSO
// =========================================================

        composable(
            route = Routes.REPORT_SUCCESS
        ) {

            ReportSuccessScreen(

                /*
                 * TODO(BACKEND):
                 *
                 * El folio debe provenir del backend después
                 * de confirmar que el reporte fue registrado.
                 *
                 * Este valor es temporal y solo se conserva
                 * para permitir visualizar la pantalla.
                 *
                 * Ningún formulario debe navegar aquí
                 * antes de recibir confirmación real.
                 */

                folio = "261908-1017S",

                // -------------------------------------------------
                // CONSULTAR ESTADO
                // -------------------------------------------------

                onTrackingClick = {
                    navController.navigate(
                        Routes.TRACKING
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // VOLVER AL INICIO
                // -------------------------------------------------

                onMainMenuClick = {
                    navController.navigate(
                        Routes.HOME
                    ) {
                        popUpTo(Routes.HOME) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // HOME - BOTTOM BAR
                // -------------------------------------------------

                onHomeClick = {
                    navController.navigate(
                        Routes.HOME
                    ) {
                        popUpTo(Routes.HOME) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // QUIÉNES SOMOS - BOTTOM BAR
                // -------------------------------------------------

                onAboutUsClick = {
                    navController.navigate(
                        Routes.ABOUT_US
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // CONFIGURACIÓN - BOTTOM BAR
                // -------------------------------------------------

                onSettingsClick = {
                    navController.navigate(
                        Routes.SETTINGS
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

// =========================================================
// SEGUIMIENTO
// =========================================================

        composable(
            route = Routes.TRACKING
        ) {

            TrackingScreen(

                // -------------------------------------------------
                // HOME
                // -------------------------------------------------

                onHomeClick = {
                    navController.navigate(
                        Routes.HOME
                    ) {
                        popUpTo(Routes.HOME) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // QUIÉNES SOMOS
                // -------------------------------------------------

                onAboutUsClick = {
                    navController.navigate(
                        Routes.ABOUT_US
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // CONFIGURACIÓN
                // -------------------------------------------------

                onSettingsClick = {
                    navController.navigate(
                        Routes.SETTINGS
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

// =========================================================
// SETTINGS
// =========================================================

        composable(
            route = Routes.SETTINGS
        ) {

            SettingsScreen(

                /*
                 * TODO(BACKEND):
                 *
                 * Reemplazar por el correo real
                 * del usuario autenticado.
                 */

                email = sessionManager.userEmail().orEmpty(),

                // -------------------------------------------------
                // CAMBIAR CONTRASEÑA
                // -------------------------------------------------

                onChangePasswordClick = {
                    navController.navigate(
                        Routes.CHANGE_PASSWORD
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // CERRAR SESIÓN
                // -------------------------------------------------

                onLogoutClick = {

                    scope.launch {

                        val refreshToken = sessionManager.refreshToken()

                        if (!refreshToken.isNullOrBlank()) {
                            authRepository.logout(refreshToken)
                        }

                        sessionManager.clear()

                        navController.navigate(
                            Routes.START
                        ) {
                            popUpTo(
                                navController.graph.id
                            ) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                },

                // -------------------------------------------------
                // BORRAR CUENTA
                // -------------------------------------------------

                onDeleteAccountClick = {

                    /*
                     * TODO(BACKEND):
                     *
                     * Posteriormente se deberá eliminar
                     * la cuenta mediante ViewModel/API.
                     *
                     * Solo navegar cuando el backend
                     * confirme la eliminación.
                     */

                    navController.navigate(
                        Routes.ACCOUNT_DELETED
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // CAMBIAR TEMA
                // -------------------------------------------------

                onToggleThemeClick = {
                    onThemeChange()
                },

                // -------------------------------------------------
                // HOME
                // -------------------------------------------------

                onHomeClick = {
                    navController.navigate(
                        Routes.HOME
                    ) {
                        popUpTo(Routes.HOME) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // QUIÉNES SOMOS
                // -------------------------------------------------

                onAboutUsClick = {
                    navController.navigate(
                        Routes.ABOUT_US
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

// =========================================================
// CAMBIAR CONTRASEÑA
// =========================================================

        composable(
            route = Routes.CHANGE_PASSWORD
        ) {

            ChangePasswordScreen(

                /*
                 * TODO(BACKEND):
                 *
                 * Reemplazar posteriormente por
                 * el correo del usuario autenticado.
                 */

                email = sessionManager.userEmail().orEmpty(),

                // -------------------------------------------------
                // ACTUALIZAR CONTRASEÑA
                // -------------------------------------------------

                onChangePasswordClick = {
                        currentPassword,
                        newPassword,
                        confirmPassword ->

                    /*
                     * TODO(BACKEND):
                     *
                     * Enviar al backend:
                     *
                     * - Contraseña actual.
                     * - Nueva contraseña.
                     * - Confirmación de contraseña.
                     *
                     * El backend deberá:
                     *
                     * 1. Verificar la contraseña actual.
                     * 2. Validar la nueva contraseña.
                     * 3. Actualizar la contraseña.
                     * 4. Invalidar la sesión correspondiente.
                     *
                     * IMPORTANTE:
                     *
                     * Solo regresar a START cuando el
                     * backend confirme el cambio.
                     */

                    /*
                     * TEMPORAL:
                     *
                     * Conservamos la navegación existente
                     * mientras se conecta el backend.
                     */

                    navController.navigate(
                        Routes.START
                    ) {
                        popUpTo(
                            navController.graph.id
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // ¿OLVIDASTE TU CONTRASEÑA?
                // -------------------------------------------------

                onForgotPasswordClick = {

                    /*
                     * Reutilizamos el flujo de recuperación:
                     *
                     * 1. Solicitud del código.
                     * 2. Ingreso del código.
                     * 3. Creación de una nueva contraseña.
                     */

                    navController.navigate(
                        Routes.RECOVER_PASSWORD
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // HOME
                // -------------------------------------------------

                onHomeClick = {
                    navController.navigate(
                        Routes.HOME
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // QUIÉNES SOMOS
                // -------------------------------------------------

                onAboutUsClick = {
                    navController.navigate(
                        Routes.ABOUT_US
                    ) {
                        launchSingleTop = true
                    }
                },

                // -------------------------------------------------
                // SETTINGS
                // -------------------------------------------------

                onSettingsClick = {
                    navController.navigate(
                        Routes.SETTINGS
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

// =========================================================
// CUENTA BORRADA
// =========================================================

        composable(
            route = Routes.ACCOUNT_DELETED
        ) {

            AccountDeletedScreen(

                onBackToStartClick = {

                    /*
                     * TODO(BACKEND):
                     *
                     * Cuando se implemente la eliminación
                     * real de cuentas, también deberán
                     * limpiarse la sesión y los datos
                     * locales asociados al usuario.
                     */

                    navController.navigate(
                        Routes.START
                    ) {
                        popUpTo(
                            navController.graph.id
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

    } // Fin de NavHost
} // Fin de AppNavigation
