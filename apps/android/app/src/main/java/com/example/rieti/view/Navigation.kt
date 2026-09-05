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
        startDestination = Routes.HOME
    ) {
        
        // HOME

        composable(Routes.HOME) {

            HomeScreen(
                onReportClick = {

                    navController.navigate(Routes.REPORT) {

                        popUpTo(
                            navController.graph.findStartDestination().id
                        ) {
                            saveState = true
                        }

                        launchSingleTop = true

                        restoreState = true
                    }
                },

                onSettingsClick = {

                    navController.navigate(Routes.SETTINGS) {

                        popUpTo(
                            navController.graph.findStartDestination().id
                        ) {
                            saveState = true
                        }

                        launchSingleTop = true

                        restoreState = true
                    }
                }
            )
        }


        // REPORT

        composable(Routes.REPORT) {

            ReportScreen(

                onHomeClick = {

                    navController.navigate(Routes.HOME) {

                        popUpTo(
                            navController.graph.findStartDestination().id
                        ) {
                            saveState = true
                        }

                        launchSingleTop = true

                        restoreState = true
                    }
                },

                onSettingsClick = {

                    navController.navigate(Routes.SETTINGS) {

                        popUpTo(
                            navController.graph.findStartDestination().id
                        ) {
                            saveState = true
                        }

                        launchSingleTop = true

                        restoreState = true
                    }
                },

                onMenuClick = {
                    // Después  el menú
                },

                onCurrentLocationClick = {
                    // Después ubicación real
                },

                onSelectFilesClick = {
                    // Después selector de archivos
                },

                onSendReportClick = {
                    // Después definimos a qué pantalla va
                }
            )
        }


        // SETTINGS

        composable(Routes.SETTINGS) {

            SettingsScreen(

                onHomeClick = {

                    navController.navigate(Routes.HOME) {

                        popUpTo(
                            navController.graph.findStartDestination().id
                        ) {
                            saveState = true
                        }

                        launchSingleTop = true

                        restoreState = true
                    }
                },

                onReportClick = {

                    navController.navigate(Routes.REPORT) {

                        popUpTo(
                            navController.graph.findStartDestination().id
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