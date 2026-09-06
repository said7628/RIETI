package com.example.rieti.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


// =================================================================
// RUTAS
// =================================================================

object Routes {

    const val HOME = "home"

    const val REPORT = "report"

    const val REPORT_SUCCESS = "report_success"

    const val SETTINGS = "settings"

    const val CHANGE_PASSWORD = "change_password"

    const val ACCOUNT_DELETED = "account_deleted"
}


// =================================================================
// NAVEGACIÓN PRINCIPAL
// =================================================================

@Composable
fun AppNavigation() {

    val navController = rememberNavController()


    NavHost(

        navController = navController,

        // Por ahora iniciamos directamente en Home
        startDestination = Routes.HOME

    ) {


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

                    /*
                     * Por ahora mandamos al mismo formulario.
                     *
                     * Después le pasaremos algo como:
                     *
                     * ReportMode.ANONYMOUS
                     */

                    navController.navigate(
                        route = Routes.REPORT
                    ) {

                        popUpTo(
                            navController.graph
                                .findStartDestination()
                                .id
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                },


                // -------------------------------------------------
                // REPORTE
                // -------------------------------------------------

                onReportClick = {

                    navController.navigate(
                        route = Routes.REPORT
                    ) {

                        popUpTo(
                            navController.graph
                                .findStartDestination()
                                .id
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

                    /*
                     * Todavía no veo una pantalla en inglés
                     * para "Quiénes somos".
                     *
                     * La conectamos después.
                     */
                },


                // -------------------------------------------------
                // SEGUIMIENTO
                // -------------------------------------------------

                onTrackingClick = {

                    /*
                     * Igual: la conectamos cuando esté
                     * creada esa pantalla.
                     */
                },


                // -------------------------------------------------
                // SETTINGS
                // -------------------------------------------------

                onSettingsClick = {

                    navController.navigate(
                        route = Routes.SETTINGS
                    ) {

                        popUpTo(
                            navController.graph
                                .findStartDestination()
                                .id
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
                            navController.graph
                                .findStartDestination()
                                .id
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
                            navController.graph
                                .findStartDestination()
                                .id
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
                            navController.graph
                                .findStartDestination()
                                .id
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

                    // Descartado por ahora
                },


                // -------------------------------------------------
                // UBICACIÓN
                // -------------------------------------------------

                onCurrentLocationClick = {

                    // Después se conecta funcionalidad real
                },


                // -------------------------------------------------
                // ARCHIVOS
                // -------------------------------------------------

                onSelectFilesClick = {

                    // Después selector real
                },


                // -------------------------------------------------
                // ENVIAR REPORTE
                // -------------------------------------------------

                onSendReportClick = {

                    navController.navigate(
                        route = Routes.REPORT_SUCCESS
                    )
                }
            )
        }


        // =========================================================
        // REPORTE ENVIADO CON ÉXITO
        // =========================================================

        composable(
            route = Routes.REPORT_SUCCESS
        ) {

            ReportSuccessScreen(

                /*
                 * Por ahora está quemado.
                 *
                 * Luego lo pasaremos realmente mediante
                 * ViewModel / backend / argumento.
                 */
                folio = "261908-1017S",


                // -------------------------------------------------
                // NUEVO REPORTE
                // -------------------------------------------------

                onNewReportClick = {

                    navController.navigate(
                        route = Routes.REPORT
                    ) {

                        /*
                         * Quitamos ReportSuccess para que al
                         * regresar no aparezca otra vez.
                         */
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
                            navController.graph
                                .findStartDestination()
                                .id
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
                            navController.graph
                                .findStartDestination()
                                .id
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
                            navController.graph
                                .findStartDestination()
                                .id
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
                            navController.graph
                                .findStartDestination()
                                .id
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

                // Por ahora simulamos correo
                email = "correo@gmail.com",


                // -------------------------------------------------
                // CAMBIAR CONTRASEÑA
                // -------------------------------------------------

                onChangePasswordClick = {

                    navController.navigate(
                        route = Routes.CHANGE_PASSWORD
                    )
                },


                // -------------------------------------------------
                // SALIR DE LA CUENTA
                // -------------------------------------------------

                onLogoutClick = {

                    /*
                     * Luego conectamos esto con el login.
                     */
                },


                // -------------------------------------------------
                // BORRAR CUENTA
                // -------------------------------------------------

                onDeleteAccountClick = {

                    /*
                     * Este callback solamente ocurre DESPUÉS
                     * de que el usuario presiona "Sí"
                     * en el Dialog.
                     */

                    navController.navigate(
                        route = Routes.ACCOUNT_DELETED
                    ) {

                        /*
                         * Quitamos Settings para que no pueda
                         * regresar a una cuenta que ya borró.
                         */
                        popUpTo(
                            route = Routes.SETTINGS
                        ) {

                            inclusive = true
                        }
                    }
                },


                // -------------------------------------------------
                // CAMBIAR TEMA
                // -------------------------------------------------

                onToggleThemeClick = {

                    /*
                     * Después esto irá con ViewModel / estado
                     * global del tema.
                     */
                },


                // -------------------------------------------------
                // HOME
                // -------------------------------------------------

                onHomeClick = {

                    navController.navigate(
                        route = Routes.HOME
                    ) {

                        popUpTo(
                            navController.graph
                                .findStartDestination()
                                .id
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
                            navController.graph
                                .findStartDestination()
                                .id
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


                // -------------------------------------------------
                // CAMBIAR CONTRASEÑA
                // -------------------------------------------------

                onChangePasswordClick = {
                        currentPassword,
                        newPassword,
                        confirmPassword ->


                    /*
                     * Por ahora no validamos nada.
                     *
                     * Después estos valores se mandarán
                     * al ViewModel.
                     *
                     * Cuando el cambio sea exitoso podemos
                     * regresar a Settings.
                     */

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
                            navController.graph
                                .findStartDestination()
                                .id
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
                            navController.graph
                                .findStartDestination()
                                .id
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

                    /*
                     * Como ChangePassword viene de Settings,
                     * podríamos simplemente volver.
                     */

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
                            navController.graph
                                .findStartDestination()
                                .id
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
                            navController.graph
                                .findStartDestination()
                                .id
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
                            navController.graph
                                .findStartDestination()
                                .id
                        ) {

                            saveState = true
                        }


                        launchSingleTop = true

                        restoreState = true
                    }
                }
            )
        }
    }
}