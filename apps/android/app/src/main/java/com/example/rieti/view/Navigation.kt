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
                     * Enviar al backend:
                     *
                     * - recoveryCode
                     * - newPassword
                     * - confirmPassword
                     *
                     * El backend deberá:
                     *
                     * 1. Validar que el código de recuperación
                     *    sea correcto y siga vigente.
                     *
                     * 2. Validar la nueva contraseña.
                     *
                     * 3. Actualizar la contraseña.
                     *
                     * 4. Invalidar cualquier sesión activa
                     *    asociada al usuario.
                     *
                     * IMPORTANTE:
                     *
                     * La navegación hacia START deberá
                     * realizarse únicamente cuando el backend
                     * confirme que el restablecimiento fue exitoso.
                     */

                    /*
                     * TEMPORAL:
                     *
                     * Mientras todavía no se conecta el backend,
                     * simulamos que el restablecimiento fue exitoso.
                     *
                     * Después regresamos a WelcomeScreen
                     * y eliminamos el historial anterior.
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
                // UBICACIÓN ACTUAL
                // -------------------------------------------------

                onCurrentLocationClick = {

                    /*
                     * TODO:
                     * Conectar ubicación posteriormente.
                     */
                },


                // -------------------------------------------------
                // SELECCIONAR EVIDENCIA
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
                     * Posteriormente se navegará únicamente
                     * cuando el backend confirme que el
                     * reporte fue registrado correctamente.
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
                     * Esperar la respuesta exitosa del
                     * ViewModel/API antes de navegar.
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
                 *
                 * Folio temporal.
                 *
                 * Posteriormente deberá recibirse el folio
                 * generado realmente por el backend.
                 */

                folio = "261908-1017S",


                // -------------------------------------------------
                // CONSULTAR ESTADO
                //
                // VA A LA MISMA PANTALLA QUE
                // HOME -> SEGUIMIENTO
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

                        popUpTo(
                            Routes.HOME
                        ) {
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
                     * Posteriormente primero deberá
                     * eliminarse la cuenta mediante
                     * ViewModel/API.
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

                /*
                 * TODO(BACKEND):
                 *
                 * Reemplazar posteriormente por
                 * el correo del usuario autenticado.
                 */

                email = "correo@gmail.com",


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
                     * - contraseña actual
                     * - nueva contraseña
                     * - confirmación de contraseña
                     *
                     * El backend deberá:
                     *
                     * 1. Verificar que la contraseña actual
                     *    sea correcta.
                     *
                     * 2. Validar la nueva contraseña.
                     *
                     * 3. Actualizar la contraseña.
                     *
                     * 4. Invalidar/cerrar la sesión actual.
                     *
                     * IMPORTANTE:
                     *
                     * La navegación hacia START deberá
                     * ejecutarse ÚNICAMENTE cuando el backend
                     * confirme que el cambio fue exitoso.
                     *
                     * Si la contraseña actual es incorrecta,
                     * NO se debe cerrar la sesión ni navegar.
                     */

                    /*
                     * TEMPORAL:
                     *
                     * Mientras todavía no se conecta el backend,
                     * simulamos que el cambio fue exitoso.
                     *
                     * Regresamos a WelcomeScreen y eliminamos
                     * el historial de navegación anterior.
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
                     * Reutilizamos el flujo de recuperación
                     * existente.
                     *
                     * El usuario pasará por:
                     *
                     * 1. Solicitud del código.
                     * 2. Ingreso del código.
                     * 3. Creación de una nueva contraseña.
                     *
                     * Cuando termine correctamente,
                     * RESET_PASSWORD lo regresará a START.
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

            /*
             * IMPORTANTE:
             *
             * AccountDeletedScreen NO recibe:
             *
             * onHomeClick
             * onAboutUsClick
             * onSettingsClick
             *
             * Su callback correcto es:
             *
             * onBackToStartClick
             */

            AccountDeletedScreen(

                onBackToStartClick = {

                    /*
                     * Después de eliminar una cuenta,
                     * regresamos al inicio y limpiamos
                     * el historial de navegación.
                     *
                     * TODO(BACKEND):
                     *
                     * También deberá limpiarse la sesión
                     * real del usuario.
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