package com.example.rieti.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


object Routes {

    const val HOME = "home"

    const val REPORT = "report"

    const val SETTINGS = "settings"
}


@Composable
fun AppNavigation() {

    val navController = rememberNavController()


    NavHost(

        navController = navController,

        startDestination = Routes.REPORT

    ) {


        // =====================================================
        // HOME
        // =====================================================

        composable(
            route = Routes.HOME
        ) {

            HomeScreen(

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



        // =====================================================
        // REPORT
        // =====================================================

        composable(
            route = Routes.REPORT
        ) {

            ReportScreen(


                // ---------------------------------------------
                // HOME
                // ---------------------------------------------

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


                // ---------------------------------------------
                // REPORT
                // ---------------------------------------------

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


                // ---------------------------------------------
                // SETTINGS
                // ---------------------------------------------

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


                // ---------------------------------------------
                // MENU HAMBURGUESA
                // ---------------------------------------------

                onMenuClick = {

                    /*
                     * Por ahora no hacemos nada.
                     *
                     * Después aquí puede abrirse:
                     *
                     * Drawer
                     * DropdownMenu
                     * Otra pantalla
                     */
                },


                // ---------------------------------------------
                // UBICACIÓN ACTUAL
                // ---------------------------------------------

                onCurrentLocationClick = {

                    /*
                     * Por ahora vacío.
                     *
                     * Después aquí se conectará
                     * la lógica de ubicación.
                     */
                },


                // ---------------------------------------------
                // ELEGIR ARCHIVOS
                // ---------------------------------------------

                onSelectFilesClick = {

                    /*
                     * Por ahora vacío.
                     *
                     * Después se conectará
                     * el selector de imágenes.
                     */
                },


                // ---------------------------------------------
                // ENVIAR REPORTE
                // ---------------------------------------------

                onSendReportClick = {

                    /*
                     * Por ahora vacío.
                     *
                     * Después podríamos navegar
                     * a una pantalla como:
                     *
                     * ReportSuccessScreen
                     */
                }
            )
        }



        // =====================================================
        // SETTINGS
        // =====================================================

        composable(
            route = Routes.SETTINGS
        ) {

            SettingsScreen(

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
    }
}