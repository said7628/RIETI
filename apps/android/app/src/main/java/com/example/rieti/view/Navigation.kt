package com.example.rieti.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


// =================================================================
// RUTAS
// =================================================================

object Routes {

    // CUENTA
    const val START = "start"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val RECOVER_PASSWORD = "recover_password"
    const val RESET_PASSWORD = "reset_password"

    // APP PRINCIPAL
    const val HOME = "home"
    const val ABOUT_US = "about_us"
    const val SETTINGS = "settings"

    // REPORTES
    const val REPORT = "report"
    const val ANONYMOUS_REPORT = "anonymous_report"
    const val REPORT_SUCCESS = "report_success"

    /*
     * TODO(NAVIGATION):
     *
     * Cuando creemos TrackingScreen agregaremos:
     *
     * const val TRACKING = "tracking"
     */

    // CONFIGURACIÓN
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


    NavHost(
        navController = navController,
        startDestination = Routes.START
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

                onLoginClick = {

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

                onCreateAccountClick = {

                    navController.navigate(
                        Routes.LOGIN
                    ) {

                        popUpTo(
                            Routes.START
                        ) {
                            inclusive = true
                        }

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
                     * El ViewModel/API deberá validar el código
                     * y realizar el cambio de contraseña.
                     *
                     * Por ahora simulamos éxito.
                     */

                    navController.navigate(
                        Routes.LOGIN
                    ) {

                        popUpTo(
                            Routes.RECOVER_PASSWORD
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },

                onLoginClick = {

                    navController.navigate(
                        Routes.LOGIN
                    ) {

                        popUpTo(
                            Routes.RECOVER_PASSWORD
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

                onAnonymousReportClick = {

                    navController.navigate(
                        Routes.ANONYMOUS_REPORT
                    ) {
                        launchSingleTop = true
                    }
                },

                onReportClick = {

                    navController.navigate(
                        Routes.REPORT
                    ) {
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

                onTrackingClick = {

                    /*
                     * TODO(NAVIGATION):
                     *
                     * Cuando exista TrackingScreen:
                     *
                     * navController.navigate(
                     *     Routes.TRACKING
                     * ) {
                     *     launchSingleTop = true
                     * }
                     */
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

                        popUpTo(
                            Routes.HOME
                        ) {
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

                        popUpTo(
                            Routes.HOME
                        ) {
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

                    // TODO: Conectar ubicación
                },

                onSelectFilesClick = {

                    // TODO: Conectar selector de evidencia
                },

                onSendReportClick = {

                    /*
                     * TODO(BACKEND):
                     *
                     * Navegar únicamente cuando el backend
                     * confirme el registro del reporte.
                     */

                    navController.navigate(
                        Routes.REPORT_SUCCESS
                    ) {
                        launchSingleTop = true
                    }
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

                        popUpTo(
                            Routes.HOME
                        ) {
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

                    // TODO: Conectar ubicación
                },

                onSelectFilesClick = {

                    // TODO: Conectar selector de evidencia
                },

                onSendReportClick = {

                    /*
                     * TODO(BACKEND):
                     *
                     * Esperar confirmación del backend antes
                     * de navegar.
                     */

                    navController.navigate(
                        Routes.REPORT_SUCCESS
                    ) {
                        launchSingleTop = true
                    }
                }
            )
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
                 * Folio temporal.
                 */

                folio = "261908-1017S",

                onTrackingClick = {

                    /*
                     * TODO(NAVIGATION):
                     *
                     * Debe ir al mismo TrackingScreen que
                     * el botón Seguimiento de HomeScreen.
                     */
                },

                onMainMenuClick = {

                    navController.navigate(
                        Routes.HOME
                    ) {

                        popUpTo(
                            Routes.HOME
                        ) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },

                onHomeClick = {

                    navController.navigate(
                        Routes.HOME
                    ) {

                        popUpTo(
                            Routes.HOME
                        ) {
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
        // SETTINGS
        // =========================================================

        composable(
            route = Routes.SETTINGS
        ) {

            SettingsScreen(

                email = "correo@gmail.com",

                onChangePasswordClick = {

                    navController.navigate(
                        Routes.CHANGE_PASSWORD
                    ) {
                        launchSingleTop = true
                    }
                },

                onLogoutClick = {

                    navController.navigate(
                        Routes.START
                    ) {

                        popUpTo(
                            Routes.HOME
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },

                onDeleteAccountClick = {

                    /*
                     * TODO(BACKEND):
                     *
                     * Primero eliminar la cuenta mediante
                     * ViewModel/API.
                     *
                     * Navegar únicamente cuando el backend
                     * confirme la eliminación.
                     */

                    navController.navigate(
                        Routes.ACCOUNT_DELETED
                    ) {
                        launchSingleTop = true
                    }
                },

                onToggleThemeClick = {

                    onThemeChange()
                },

                onHomeClick = {

                    navController.navigate(
                        Routes.HOME
                    ) {

                        popUpTo(
                            Routes.HOME
                        ) {
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

                email = "correo@gmail.com",

                onChangePasswordClick = {
                        currentPassword,
                        newPassword,
                        confirmPassword ->

                    /*
                     * TODO(BACKEND):
                     *
                     * Aquí irá la llamada real para cambiar
                     * la contraseña.
                     *
                     * El backend deberá validar:
                     *
                     * - currentPassword
                     * - newPassword
                     * - confirmPassword
                     *
                     * Sólo después de una respuesta exitosa
                     * deberá mostrarse confirmación o regresar
                     * a Configuración.
                     */

                    navController.popBackStack()
                },

                onHomeClick = {

                    navController.navigate(
                        Routes.HOME
                    ) {
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
        // CUENTA BORRADA
        // =========================================================

        composable(
            route = Routes.ACCOUNT_DELETED
        ) {

            AccountDeletedScreen(

                onBackToStartClick = {

                    /*
                     * Después de borrar la cuenta regresamos
                     * a WelcomeScreen y eliminamos el historial
                     * de la sesión anterior.
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
    }
}