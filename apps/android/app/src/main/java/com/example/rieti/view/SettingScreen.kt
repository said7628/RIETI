package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.TopBar


// =================================================================
// CONFIGURACIÓN
// =================================================================

@Composable
fun SettingsScreen(
    email: String,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onToggleThemeClick: () -> Unit,
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit
) {

    val colors = AppTheme.colors
    val isDarkTheme = AppTheme.isDarkTheme


    /*
     * Controla si el diálogo de borrar cuenta
     * está visible o no.
     */
    var showDeleteAccountDialog by rememberSaveable {
        mutableStateOf(false)
    }


    Scaffold(

        containerColor = colors.surface,


        // =========================================================
        // TOP BAR
        // =========================================================

        topBar = {

            Column {

                TopBar(

                )


                // =================================================
                // TÍTULO CONFIGURACIÓN
                // =================================================

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .background(
                            colors.pinkPrincipal
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Configuración",
                        color = colors.text,
                        fontFamily = FontFamily.Serif,
                        fontSize = 25.sp
                    )
                }
            }
        },


        // =========================================================
        // BOTTOM BAR
        //
        // Inicio | Quiénes somos | Configuración
        // =========================================================

        bottomBar = {

            BottomBar(

                onHomeClick = {
                    onHomeClick()
                },

                onAboutUsClick = {
                    onAboutUsClick()
                },

                /*
                 * Ya estamos dentro de Configuración,
                 * así que no necesitamos navegar otra vez.
                 */
                onSettingsClick = {}
            )
        }

    ) { innerPadding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    colors.surface
                )
        ) {


            // =====================================================
            // ACCESO A LA CUENTA
            // =====================================================

            SettingsSectionTitle(
                title = "Acceso a la cuenta"
            )


            // =====================================================
            // CORREO
            // =====================================================

            SettingsInfoRow(
                label = "Correo electrónico",
                value = email
            )


            // =====================================================
            // CONTRASEÑA
            // =====================================================

            SettingsInfoRow(
                label = "Contraseña",
                value = "**************"
            )


            // =====================================================
            // CAMBIAR CONTRASEÑA
            // =====================================================

            SettingsActionRow(

                text = "Cambiar contraseña",

                onClick = {
                    onChangePasswordClick()
                }
            )


            // =====================================================
            // CERRAR SESIÓN
            // =====================================================

            SettingsActionRow(

                text = "Salir de la cuenta",

                onClick = {
                    onLogoutClick()
                }
            )


            // =====================================================
            // BORRAR CUENTA
            // =====================================================

            SettingsActionRow(

                text = "Borrar cuenta",

                onClick = {

                    /*
                     * Todavía NO borramos la cuenta.
                     *
                     * Solo mostramos la confirmación.
                     */
                    showDeleteAccountDialog = true
                }
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // =====================================================
            // VISUALIZACIÓN
            // =====================================================

            SettingsSectionTitle(
                title = "Visualización"
            )


            // =====================================================
            // MODO CLARO / OSCURO
            // =====================================================

            SettingsActionRow(

                text =
                    if (isDarkTheme) {
                        "Modo claro"
                    } else {
                        "Modo oscuro"
                    },

                onClick = {
                    onToggleThemeClick()
                }
            )
        }
    }


    // =============================================================
    // DIÁLOGO PARA BORRAR CUENTA
    // =============================================================

    if (showDeleteAccountDialog) {

        DeleteAccountDialog(

            // -----------------------------------------------------
            // NO / TOCAR FUERA
            // -----------------------------------------------------

            onDismiss = {

                showDeleteAccountDialog = false
            },


            // -----------------------------------------------------
            // SÍ
            // -----------------------------------------------------

            onConfirm = {

                /*
                 * Primero cerramos el disclaimer.
                 */
                showDeleteAccountDialog = false


                /*
                 * Ahora sí avisamos que el usuario
                 * confirmó borrar la cuenta.
                 */
                onDeleteAccountClick()
            }
        )
    }
}


// =================================================================
// TÍTULO DE SECCIÓN
// =================================================================

@Composable
private fun SettingsSectionTitle(
    title: String
) {

    val colors = AppTheme.colors


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                top = 15.dp,
                bottom = 5.dp
            )
    ) {

        Text(
            text = title,
            color = colors.titles,
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp
        )


        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(
                    top = 3.dp
                ),

            thickness = 1.5.dp,

            color = colors.titles
        )
    }
}


// =================================================================
// FILA CON INFORMACIÓN
// =================================================================

@Composable
private fun SettingsInfoRow(
    label: String,
    value: String
) {

    val colors = AppTheme.colors


    Column(
        modifier = Modifier.fillMaxWidth()
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(
                    horizontal = 8.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {


            Text(
                text = label,
                color = colors.text,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )


            Text(
                text = value,
                color = colors.text,
                fontSize = 13.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }


        HorizontalDivider(
            thickness = 1.dp,
            color = colors.titles
        )
    }
}


// =================================================================
// FILA CLICKEABLE
// =================================================================

@Composable
private fun SettingsActionRow(
    text: String,
    onClick: () -> Unit
) {

    val colors = AppTheme.colors


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                onClick()
            }
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(
                    horizontal = 8.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {


            Text(
                text = text,
                color = colors.text,
                fontSize = 15.sp
            )
        }


        HorizontalDivider(
            thickness = 1.dp,
            color = colors.titles
        )
    }
}


// =================================================================
// DIÁLOGO DE BORRAR CUENTA
// =================================================================

@Composable
private fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    val colors = AppTheme.colors


    Dialog(

        /*
         * Si el usuario toca afuera del diálogo
         * o presiona atrás:
         */
        onDismissRequest = {

            onDismiss()
        }

    ) {


        Column(
            modifier = Modifier

                .width(220.dp)

                .clip(
                    RoundedCornerShape(14.dp)
                )

                .background(
                    colors.navVar
                )

                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = "¿Estás seguro de que quieres borrar tu cuenta?",

                color = colors.text,

                fontFamily = FontFamily.Serif,

                fontSize = 18.sp,

                lineHeight = 22.sp,

                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =====================================================
            // NO
            // =====================================================

            DeleteDialogButton(

                text = "No",

                onClick = {

                    onDismiss()
                }
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            // =====================================================
            // SÍ
            // =====================================================

            DeleteDialogButton(

                text = "Sí",

                onClick = {

                    onConfirm()
                }
            )
        }
    }
}


// =================================================================
// BOTÓN DEL DIÁLOGO
// =================================================================

@Composable
private fun DeleteDialogButton(
    text: String,
    onClick: () -> Unit
) {

    val colors = AppTheme.colors


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(
                RoundedCornerShape(9.dp)
            )
            .background(
                colors.pinkPrincipal
            )
            .clickable {

                onClick()
            },

        contentAlignment = Alignment.Center
    ) {


        Text(
            text = text,
            color = colors.onPrimary,
            fontSize = 16.sp
        )
    }
}


// =================================================================
// PREVIEW LIGHT
// =================================================================

@Preview(
    name = "Settings - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SettingsLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        SettingsScreen(

            email = "correo@gmail.com",

            onChangePasswordClick = {},

            onLogoutClick = {},

            onDeleteAccountClick = {},

            onToggleThemeClick = {},

            onHomeClick = {},

            onAboutUsClick = {}
        )
    }
}


// =================================================================
// PREVIEW DARK
// =================================================================

@Preview(
    name = "Settings - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SettingsDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        SettingsScreen(

            email = "correo@gmail.com",

            onChangePasswordClick = {},

            onLogoutClick = {},

            onDeleteAccountClick = {},

            onToggleThemeClick = {},

            onHomeClick = {},

            onAboutUsClick = {}
        )
    }
}