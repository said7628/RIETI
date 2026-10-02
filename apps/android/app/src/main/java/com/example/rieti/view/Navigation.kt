package com.example.rieti.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


// =================================================================
// RUTAS
// =================================================================

object Routes {

    // =============================================================
    // FLUJO DE INICIO / CUENTA
    // =============================================================

    const val START = "start"

    const val LOGIN = "login"

    const val REGISTER = "register"

    const val RECOVER_PASSWORD = "recover_password"


    // =============================================================
    // APP PRINCIPAL
    // =============================================================

    const val HOME = "home"

    const val REPORT = "report"

    const val REPORT_SUCCESS = "report_success"

    const val SETTINGS = "settings"

    const val CHANGE_PASSWORD = "change_password"

    const val ACCOUNT_DELETED = "account_deleted"

    const val VERIFICATE_CODE = "verificate_code"

    // NUEVO
    const val ANONYMOUS_REPORT = "anonymous_report"
}


// =================================================================
// NAVEGACIÓN PRINCIPAL
// =================================================================

@Composable
fun AppNavigation() {

    val navController = rememberNavController()


    NavHost(

        navController = navController,

        startDestination = Routes.START

    ) {


        // =========================================================
        // 1. PANTALLA INICIAL
        // =========================================================

        composable(
            route = Routes.START
        ) {

            InicioPantalla1(

                onLoginClick = {

                    navController.navigate(
                        route = Routes.LOGIN
                    ) {

                        launchSingleTop = true
                    }
                },


                onRegisterClick = {

                    navController.navigate(
                        route = Routes.REGISTER
                    ) {

                        launchSingleTop = true
                    }
                }
            )
        }


        // =========================================================
        // 2. INICIAR SESIÓN
        // =========================================================

        composable(
            route = Routes.LOGIN
        ) {

            IniciarsePantalla2(

                onLoginClick = {

                    navController.navigate(
                        route = Routes.HOME
                    ) {

                        popUpTo(
                            route = Routes.START
                        ) {

                            inclusive = true
                        }


                        launchSingleTop = true
                    }
                },


                onRegisterClick = {

                    navController.navigate(
                        route = Routes.REGISTER
                    ) {

                        popUpTo(
                            route = Routes.START
                        ) {

                            inclusive = false
                        }


                        launchSingleTop = true
                    }
                },


                onForgotPasswordClick = {

                    navController.navigate(
                        route = Routes.RECOVER_PASSWORD
                    ) {

                        launchSingleTop = true
                    }
                }
            )
        }


        // =========================================================
        // 3. CREAR CUENTA
        // =========================================================

        composable(
            route = Routes.REGISTER
        ) {

            CrearPantalla3(

                onCreateAccountClick = {

                    navController.navigate(
                        route = Routes.LOGIN
                    ) {

                        popUpTo(
                            route = Routes.START
                        ) {

                            inclusive = true
                        }


                        launchSingleTop = true
                    }
                },


                // INICIAR SESIÓN DESDE CREAR CUENTA
                onLoginClick = {

                    navController.navigate(
                        route = Routes.LOGIN
                    ) {

                        popUpTo(
                            route = Routes.START
                        ) {

                            inclusive = false
                        }


                        launchSingleTop = true
                    }
                }
            )
        }


        // =========================================================
        // 4. RECUPERAR CONTRASEÑA
        // =========================================================

        composable(
            route = Routes.RECOVER_PASSWORD
        ) {

            RecuperarContraseñaPantalla4(


                // -------------------------------------------------
                // ENVIAR CÓDIGO
                // -------------------------------------------------

                onSendCodeClick = {

                    // NUEVO:
                    // abre la pantalla para introducir código
                    // y nueva contraseña

                    navController.navigate(
                        route = Routes.VERIFICATE_CODE
                    ) {

                        launchSingleTop = true
                    }
                },


                // -------------------------------------------------
                // INICIAR SESIÓN
                // -------------------------------------------------

                onLoginClick = {

                    navController.navigate(
                        route = Routes.LOGIN
                    ) {

                        popUpTo(
                            route = Routes.START
                        ) {

                            inclusive = false
                        }


                        launchSingleTop = true
                    }
                },


                // -------------------------------------------------
                // CREAR CUENTA
                // -------------------------------------------------

                onRegisterClick = {

                    navController.navigate(
                        route = Routes.REGISTER
                    ) {

                        popUpTo(
                            route = Routes.START
                        ) {

                            inclusive = false
                        }


                        launchSingleTop = true
                    }
                }
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

                    // CAMBIO:
                    // ahora abre AnonymousReportScreen

                    navController.navigate(
                        route = Routes.ANONYMOUS_REPORT
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // REALIZAR REPORTE
                // -------------------------------------------------

                onReportClick = {

                    navController.navigate(
                        route = Routes.REPORT
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // QUIÉNES SOMOS
                // -------------------------------------------------

                onAboutUsClick = {

                    // Pendiente
                },


                // -------------------------------------------------
                // SEGUIMIENTO
                // -------------------------------------------------

                onTrackingClick = {

                    // Pendiente
                },


                // -------------------------------------------------
                // SETTINGS
                // -------------------------------------------------

                onSettingsClick = {

                    navController.navigate(
                        route = Routes.SETTINGS
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                }
            )
        }


        // =========================================================
        // REPORT
        // =========================================================

        composable(
            route = Routes.REPORT
        ) {

            ReportScreen(


                // -------------------------------------------------
                // HOME
                // -------------------------------------------------

                onHomeClick = {

                    navController.navigate(
                        route = Routes.HOME
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // REPORT
                // -------------------------------------------------

                onReportClick = {

                    navController.navigate(
                        route = Routes.REPORT
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // SETTINGS
                // -------------------------------------------------

                onSettingsClick = {

                    navController.navigate(
                        route = Routes.SETTINGS
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // MENÚ
                // -------------------------------------------------

                onMenuClick = {

                    // Descartado
                },


                // -------------------------------------------------
                // UBICACIÓN ACTUAL
                // -------------------------------------------------

                onCurrentLocationClick = {

                    // Funcionalidad después
                },


                // -------------------------------------------------
                // ELEGIR ARCHIVOS
                // -------------------------------------------------

                onSelectFilesClick = {

                    // Funcionalidad después
                },


                // -------------------------------------------------
                // ENVIAR REPORTE
                // -------------------------------------------------

                onSendReportClick = {

                    navController.navigate(
                        route = Routes.REPORT_SUCCESS
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
                // HOME
                // -------------------------------------------------

                onHomeClick = {

                    navController.navigate(
                        route = Routes.HOME
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // REPORT NORMAL
                // -------------------------------------------------

                onReportClick = {

                    navController.navigate(
                        route = Routes.REPORT
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // SETTINGS
                // -------------------------------------------------

                onSettingsClick = {

                    navController.navigate(
                        route = Routes.SETTINGS
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // UBICACIÓN
                // -------------------------------------------------

                onCurrentLocationClick = {

                    // Funcionalidad después
                },


                // -------------------------------------------------
                // ARCHIVOS
                // -------------------------------------------------

                onSelectFilesClick = {

                    // Funcionalidad después
                },


                // -------------------------------------------------
                // ENVIAR REPORTE ANÓNIMO
                // -------------------------------------------------

                onSendReportClick = {

                    navController.navigate(
                        route = Routes.REPORT_SUCCESS
                    ) {

                        launchSingleTop = true
                    }
                }
            )
        }


        // =========================================================
        // REPORTE CREADO CON ÉXITO
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

                    navController.navigate(
                        route = Routes.REPORT
                    ) {

                        popUpTo(
                            route = Routes.REPORT_SUCCESS
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
                        route = Routes.HOME
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // HOME
                // -------------------------------------------------

                onHomeClick = {

                    navController.navigate(
                        route = Routes.HOME
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // REPORT
                // -------------------------------------------------

                onReportClick = {

                    navController.navigate(
                        route = Routes.REPORT
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // SETTINGS
                // -------------------------------------------------

                onSettingsClick = {

                    navController.navigate(
                        route = Routes.SETTINGS
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
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
                        route = Routes.CHANGE_PASSWORD
                    ) {

                        launchSingleTop = true
                    }
                },


                // -------------------------------------------------
                // SALIR DE LA CUENTA
                // -------------------------------------------------

                onLogoutClick = {

                    navController.navigate(
                        route = Routes.START
                    ) {

                        popUpTo(
                            route = Routes.HOME
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

                    navController.navigate(
                        route = Routes.ACCOUNT_DELETED
                    ) {

                        popUpTo(
                            route = Routes.SETTINGS
                        ) {

                            inclusive = true
                        }


                        launchSingleTop = true
                    }
                },


                // -------------------------------------------------
                // CAMBIAR TEMA
                // -------------------------------------------------

                onToggleThemeClick = {

                    // Pendiente
                },


                // -------------------------------------------------
                // HOME
                // -------------------------------------------------

                onHomeClick = {

                    navController.navigate(
                        route = Routes.HOME
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // REPORT
                // -------------------------------------------------

                onReportClick = {

                    navController.navigate(
                        route = Routes.REPORT
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
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


                    // Después irá al ViewModel.
                    // Por ahora solamente regresamos.

                    navController.popBackStack()
                },


                // -------------------------------------------------
                // HOME
                // -------------------------------------------------

                onHomeClick = {

                    navController.navigate(
                        route = Routes.HOME
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // REPORT
                // -------------------------------------------------

                onReportClick = {

                    navController.navigate(
                        route = Routes.REPORT
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // SETTINGS
                // -------------------------------------------------

                onSettingsClick = {

                    navController.popBackStack()
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


                // -------------------------------------------------
                // HOME
                // -------------------------------------------------

                onHomeClick = {

                    navController.navigate(
                        route = Routes.HOME
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // REPORT
                // -------------------------------------------------

                onReportClick = {

                    navController.navigate(
                        route = Routes.REPORT
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // SETTINGS
                // -------------------------------------------------

                onSettingsClick = {

                    navController.navigate(
                        route = Routes.SETTINGS
                    ) {

                        popUpTo(
                            route = Routes.HOME
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                }
            )
        }


        // =========================================================
        // CÓDIGO DE RECUPERACIÓN + NUEVA CONTRASEÑA
        // =========================================================

        composable(
            route = Routes.VERIFICATE_CODE
        ) {

            ResetPasswordScreen(


                // -------------------------------------------------
                // RESTABLECER CONTRASEÑA
                // -------------------------------------------------

                onResetPasswordClick = {
                        recoveryCode,
                        newPassword,
                        confirmPassword ->


                    /*
                     * Después estos tres datos irán al ViewModel.
                     *
                     * Por ahora simulamos que todo salió bien
                     * y regresamos a iniciar sesión.
                     */

                    navController.navigate(
                        route = Routes.LOGIN
                    ) {

                        popUpTo(
                            route = Routes.RECOVER_PASSWORD
                        ) {

                            inclusive = true
                        }


                        launchSingleTop = true
                    }
                },


                // -------------------------------------------------
                // INICIAR SESIÓN
                // -------------------------------------------------

                onLoginClick = {

                    navController.navigate(
                        route = Routes.LOGIN
                    ) {

                        popUpTo(
                            route = Routes.RECOVER_PASSWORD
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