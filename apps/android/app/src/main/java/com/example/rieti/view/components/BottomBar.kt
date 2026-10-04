package com.example.rieti.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rieti.ui.theme.AppTheme


// =================================================================
// BOTTOM BAR
// =================================================================

@Composable
fun BottomBar(
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = AppTheme.colors


    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {


        // =========================================================
        // BARRA PRINCIPAL
        // =========================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(colors.pinkPrincipal)
        ) {


            // =====================================================
            // INICIO
            // =====================================================

            IconButton(
                onClick = {
                    onHomeClick()
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 22.dp)
                    .size(58.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(38.dp)
                )
            }


            // =====================================================
            // QUIÉNES SOMOS
            // =====================================================

            IconButton(
                onClick = {
                    onAboutUsClick()
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(62.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = "Quiénes somos",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(42.dp)
                )
            }


            // =====================================================
            // CONFIGURACIÓN
            // =====================================================

            IconButton(
                onClick = {
                    onSettingsClick()
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 22.dp)
                    .size(58.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(38.dp)
                )
            }
        }


        // =========================================================
        // ESPACIO PARA NAVEGACIÓN DEL SISTEMA
        // =========================================================

        SpacerForSystemNavigation()
    }
}


// =================================================================
// NAVEGACIÓN DEL SISTEMA
// =================================================================

@Composable
private fun SpacerForSystemNavigation() {

    Box(
        modifier = Modifier
            .fillMaxWidth()

            // Toma exactamente la altura requerida
            // por la barra de navegación de Android.
            .windowInsetsBottomHeight(
                WindowInsets.navigationBars
            )

            // Fondo de ■ ● ◀
            .background(Color.Black)
    )
}