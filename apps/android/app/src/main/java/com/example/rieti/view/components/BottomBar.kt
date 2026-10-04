package com.example.rieti.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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


@Composable
fun BottomBar(
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = AppTheme.colors
    val isDarkTheme = AppTheme.isDarkTheme

    val navigationColor =
        if (isDarkTheme) {
            Color(0xFFF8F5F6)
        } else {
            Color(0xFFD9165D)
        }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {


            // =====================================================
            // INICIO
            // =====================================================

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {

                IconButton(
                    onClick = {
                        onHomeClick()
                    },

                    modifier = Modifier.size(64.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Inicio",
                        tint = navigationColor,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }


            // =====================================================
            // QUIÉNES SOMOS
            // =====================================================

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {

                IconButton(
                    onClick = {
                        onAboutUsClick()
                    },

                    modifier = Modifier.size(64.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Quiénes somos",
                        tint = navigationColor,
                        modifier = Modifier.size(45.dp)
                    )
                }
            }


            // =====================================================
            // CONFIGURACIÓN
            // =====================================================

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {

                IconButton(
                    onClick = {
                        onSettingsClick()
                    },

                    modifier = Modifier.size(64.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configuración",
                        tint = navigationColor,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }


        SpacerForSystemNavigation(
            backgroundColor = colors.surface
        )
    }
}


// =================================================================
// ESPACIO DE NAVEGACIÓN DEL SISTEMA
// =================================================================

@Composable
private fun SpacerForSystemNavigation(
    backgroundColor: Color
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsBottomHeight(
                WindowInsets.navigationBars
            )
            .background(backgroundColor)
    )
}