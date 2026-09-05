package com.example.rieti.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.rieti.R
import com.example.rieti.ui.theme.AppTheme
import androidx.compose.foundation.layout.requiredSize

@Composable
fun BottomBar(
    onHomeClick: () -> Unit,
    onReportClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = AppTheme.colors
    val isDark = AppTheme.isDarkTheme


    val reportResource = if (isDark) {
        R.drawable.central_button_dark
    } else {
        R.drawable.central_button_light
    }


    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {

        // =====================================================
        // BARRA DE NUESTRA APP
        // =====================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(colors.pinkPrincipal)
        ) {


            // =================================================
            // HOME
            // =================================================

            IconButton(
                onClick = {
                    onHomeClick()
                },

                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 22.dp)
                    .size(52.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(55.dp)
                )
            }


            // =================================================
            // BOTÓN REPORTE
            // =================================================

            Image(
                painter = painterResource(
                    id = reportResource
                ),

                contentDescription = "Crear reporte",

                contentScale = ContentScale.Fit,

                modifier = Modifier
                    .align(Alignment.TopCenter)

                    // Sobresale de la barra sin crear
                    // espacio extra en el Scaffold
                    .offset(y = (-22).dp)

                    .requiredSize(100.dp)

                    .zIndex(1f)

                    .clickable {
                        onReportClick()
                    }
            )


            // =================================================
            // SETTINGS
            // =================================================

            IconButton(
                onClick = {
                    onSettingsClick()
                },

                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 22.dp)
                    .size(65.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(55.dp)
                )
            }
        }


        // =====================================================
        // ESPACIO EXCLUSIVO PARA LOS BOTONES DE ANDROID
        // =====================================================

        SpacerForSystemNavigation()
    }
}


@Composable
private fun SpacerForSystemNavigation() {

    Box(
        modifier = Modifier
            .fillMaxWidth()

            // Toma exactamente la altura que Android necesita
            .windowInsetsBottomHeight(
                WindowInsets.navigationBars
            )

            // Este será el fondo de ■ ● ◀
            .background(Color.Black)
    )
}