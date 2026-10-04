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

                    /*
                     * Pendiente:
                     * conectar pantalla de seguimiento.
                     */
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

                // -------------------------------------------------
                // INICIO
                // -------------------------------------------------

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
                },


                // -------------------------------------------------
                // MENÚ
                // -------------------------------------------------

                onMenuClick = {

                    // Pendiente
                },


                // -------------------------------------------------
                // UBICACIÓN ACTUAL
                // -------------------------------------------------

                onCurrentLocationClick = {

                    // Pendiente de implementar
                },


                // -------------------------------------------------
                // SELECCIONAR EVIDENCIA
                // -------------------------------------------------

                onSelectFilesClick = {

                    // Pendiente de implementar
                },


                // -------------------------------------------------
                // ENVIAR REPORTE
                // -------------------------------------------------

                onSendReportClick = {

                    /*
                     * TODO(BACKEND):
                     *
                     * Posteriormente la navegación deberá ocurrir
                     * únicamente cuando el backend confirme que
                     * el reporte fue registrado correctamente.
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

                // -------------------------------------------------
                // INICIO
                // -------------------------------------------------

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
                },


                // -------------------------------------------------
                // UBICACIÓN ACTUAL
                // -------------------------------------------------

                onCurrentLocationClick = {

                    /*
                     * TODO:
                     * Conectar ubicación posteriormente.
                     */
                },


                // -------------------------------------------------
                // SELECCIONAR ARCHIVOS
                // -------------------------------------------------

                onSelectFilesClick = {

                    /*
                     * TODO:
                     * Conectar selector de evidencia.
                     */
                },


                // -------------------------------------------------
                // ENVIAR REPORTE
                // -------------------------------------------------

                onSendReportClick = {

                    /*
                     * TODO(BACKEND):
                     *
                     * Aquí después esperaremos la respuesta
                     * exitosa del ViewModel/API antes de navegar.
                     */

                    navController.navigate(
                        Routes.REPORT_SUCCESS
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }


        //=========================================================
// REPORTE EXITOSO
// =========================================================

        composable(
            route = Routes.REPORT_SUCCESS
        ) {

            ReportSuccessScreen(

                folio = "261908-1017S",


                // -------------------------------------------------
                // NUEVO REPORTE
                // -------------------------------------------------

                onNewReportClick = {

                    /*
                     * Por ahora manda al reporte con seguimiento.
                     *
                     * Más adelante podríamos hacer que "Nuevo Reporte"
                     * vuelva al Home y muestre nuevamente el selector
                     * Anónimo / Con seguimiento.
                     */

                    navController.navigate(
                        Routes.REPORT
                    ) {

                        popUpTo(
                            Routes.REPORT_SUCCESS
                        ) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },


                // -------------------------------------------------
                // MENÚ PRINCIPAL
                // -------------------------------------------------

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


                // -------------------------------------------------
                // INICIO - BOTTOM BAR
                // -------------------------------------------------

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
// SETTINGS
// =========================================================

        composable(
            route = Routes.SETTINGS
        ) {

            SettingsScreen(

                email = "correo@gmail.com",


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


                // -------------------------------------------------
                // BORRAR CUENTA
                // -------------------------------------------------

                onDeleteAccountClick = {

                    /*
                     * TODO(BACKEND):
                     *
                     * Posteriormente aquí deberá realizarse
                     * primero la eliminación real mediante
                     * ViewModel/API.
                     *
                     * Por ahora continuamos con el flujo visual.
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
                // INICIO
                // -------------------------------------------------

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

                email = "correo@gmail.com",

                onChangePasswordClick = {
                        currentPassword,
                        newPassword,
                        confirmPassword ->

                    /*
                     * TODO(BACKEND):
                     *
                     * Aquí después irá la llamada real
                     * para cambiar la contraseña.
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
    }
}