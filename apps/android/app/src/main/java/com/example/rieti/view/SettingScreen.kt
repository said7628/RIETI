package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.TopBar


// =====================================================================
// CONFIGURACIÓN
// =====================================================================

@Composable
fun SettingsScreen(
    email: String,
    canDeleteAccount: Boolean = true,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onToggleThemeClick: () -> Unit,
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit
) {

    val colors = AppTheme.colors
    val isDarkTheme = AppTheme.isDarkTheme

    val navy =
        if (isDarkTheme) {
            colors.text
        } else {
            Color(0xFF123A68)
        }

    val background =
        if (isDarkTheme) {
            colors.surface
        } else {
            Color(0xFFFCFBF9)
        }

    val cardBackground =
        if (isDarkTheme) {
            colors.navVar
        } else {
            Color.White
        }

    val secondaryText =
        if (isDarkTheme) {
            colors.text.copy(alpha = 0.65f)
        } else {
            Color(0xFF627894)
        }

    val divider =
        if (isDarkTheme) {
            colors.text.copy(alpha = 0.12f)
        } else {
            Color(0xFFE5E7EA)
        }


    // =================================================================
    // ESTADOS PARA BORRAR CUENTA
    // =================================================================

    /*
     * PASO 1:
     * Preguntar al usuario si realmente desea borrar su cuenta.
     */
    var showDeleteConfirmationDialog by rememberSaveable {
        mutableStateOf(false)
    }

    /*
     * PASO 2:
     * Solicitar la contraseña actual para confirmar identidad.
     */
    var showDeletePasswordDialog by rememberSaveable {
        mutableStateOf(false)
    }


    Scaffold(
        containerColor = background,

        // =============================================================
        // TOP BAR
        // =============================================================

        topBar = {
            TopBar()
        },

        // =============================================================
        // BOTTOM BAR
        // =============================================================

        bottomBar = {

            BottomBar(
                onHomeClick = {
                    onHomeClick()
                },

                onAboutUsClick = {
                    onAboutUsClick()
                },

                onSettingsClick = {
                    // Ya estamos en Configuración.
                }
            )
        }

    ) { innerPadding ->


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(background)
        ) {

            // =========================================================
            // CONTENIDO
            // =========================================================

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 24.dp
                    )
            ) {

                // =====================================================
                // TÍTULO
                // =====================================================

                Text(
                    text = "Configuración",
                    color = navy,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    lineHeight = 38.sp
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                // =====================================================
                // ACENTO ROSA
                // =====================================================

                Box(
                    modifier = Modifier
                        .width(58.dp)
                        .height(3.dp)
                        .background(
                            colors.pinkPrincipal
                        )
                )


                Spacer(
                    modifier = Modifier.height(24.dp)
                )


                // =====================================================
                // CUENTA
                // =====================================================

                SettingsSectionHeader(
                    title = "Cuenta",
                    description =
                        "Administra la información de tu cuenta.",
                    navy = navy,
                    secondaryText = secondaryText
                )


                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                // =====================================================
                // TARJETA DE CUENTA
                // =====================================================

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(10.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = cardBackground
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        // =================================================
                        // CORREO ELECTRÓNICO
                        //
                        // Solo se muestra como información.
                        // No puede modificarse desde Configuración.
                        // =================================================

                        SettingsInformationRow(
                            icon = Icons.Default.Email,
                            title = "Correo electrónico",
                            value = email,
                            navy = navy,
                            secondaryText = secondaryText
                        )


                        SettingsDivider(
                            color = divider
                        )


                        // =================================================
                        // CAMBIAR CONTRASEÑA
                        // =================================================

                        SettingsActionRow(
                            icon = Icons.Default.Key,
                            title = "Cambiar contraseña",
                            navy = navy,

                            onClick = {
                                onChangePasswordClick()
                            }
                        )


                        SettingsDivider(
                            color = divider
                        )


                        // =================================================
                        // SALIR DE LA CUENTA
                        // =================================================

                        SettingsActionRow(
                            icon = Icons.Default.Logout,
                            title = "Salir de la cuenta",
                            navy = navy,

                            onClick = {
                                onLogoutClick()
                            }
                        )


                        SettingsDivider(
                            color = divider
                        )


                        // =================================================
                        // BORRAR CUENTA
                        // =================================================

                        if (canDeleteAccount) SettingsActionRow(
                            icon = Icons.Default.DeleteOutline,
                            title = "Borrar cuenta",
                            navy = navy,

                            onClick = {

                                /*
                                 * Primero mostramos la advertencia.
                                 *
                                 * Todavía NO pedimos contraseña
                                 * ni intentamos eliminar la cuenta.
                                 */

                                showDeleteConfirmationDialog = true
                            }
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(26.dp)
                )


                // =====================================================
                // VISUALIZACIÓN
                // =====================================================

                SettingsSectionHeader(
                    title = "Visualización",
                    description =
                        "Personaliza tu experiencia en la aplicación.",
                    navy = navy,
                    secondaryText = secondaryText
                )


                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                // =====================================================
                // APARIENCIA / MODO OSCURO
                // =====================================================

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(10.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = cardBackground
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onToggleThemeClick()
                            }
                            .padding(
                                horizontal = 17.dp,
                                vertical = 13.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.DarkMode,

                            contentDescription = null,

                            tint = navy,

                            modifier =
                                Modifier.size(29.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.width(18.dp)
                        )


                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Apariencia",
                                color = navy,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(1.dp)
                            )


                            Text(
                                text =
                                    if (isDarkTheme) {
                                        "Modo oscuro"
                                    } else {
                                        "Modo claro"
                                    },

                                color = secondaryText,
                                fontFamily = FontFamily.Serif,
                                fontSize = 13.sp
                            )
                        }


                        Switch(
                            checked = isDarkTheme,

                            onCheckedChange = {
                                onToggleThemeClick()
                            },

                            colors =
                                SwitchDefaults.colors(

                                    checkedThumbColor =
                                        Color.White,

                                    checkedTrackColor =
                                        colors.pinkPrincipal,

                                    uncheckedThumbColor =
                                        Color.White,

                                    uncheckedTrackColor =
                                        Color(0xFFD4D7DA),

                                    uncheckedBorderColor =
                                        Color.Transparent
                                )
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )
            }
        }
    }


    // =================================================================
    // PASO 1:
    // CONFIRMAR QUE EL USUARIO DESEA BORRAR SU CUENTA
    // =================================================================

    if (canDeleteAccount && showDeleteConfirmationDialog) {

        DeleteAccountConfirmationDialog(
            navy = navy,
            cardBackground = cardBackground,
            secondaryText = secondaryText,

            onDismiss = {

                showDeleteConfirmationDialog = false
            },

            onContinue = {

                /*
                 * Cerramos la primera confirmación
                 * y pasamos a la verificación de identidad.
                 */

                showDeleteConfirmationDialog = false

                showDeletePasswordDialog = true
            }
        )
    }


    // =================================================================
    // PASO 2:
    // CONFIRMAR IDENTIDAD CON CONTRASEÑA
    // =================================================================

    if (canDeleteAccount && showDeletePasswordDialog) {

        DeleteAccountDialog(
            navy = navy,
            cardBackground = cardBackground,
            secondaryText = secondaryText,

            onDismiss = {

                showDeletePasswordDialog = false
            },

            onConfirm = { password ->

                /*
                 * TODO(BACKEND):
                 *
                 * Enviar la contraseña actual al backend
                 * para volver a autenticar al usuario.
                 *
                 *
                 * SI LA CONTRASEÑA ES CORRECTA:
                 *
                 * 1. El backend elimina la cuenta.
                 * 2. Se invalida/cierra la sesión.
                 * 3. Se ejecuta onDeleteAccountClick().
                 * 4. AppNavigation navega a AccountDeletedScreen.
                 *
                 *
                 * SI LA CONTRASEÑA ES INCORRECTA:
                 *
                 * 1. NO se elimina la cuenta.
                 * 2. El diálogo permanece abierto.
                 * 3. Se muestra un mensaje:
                 *
                 *    "Contraseña incorrecta."
                 *
                 *
                 * IMPORTANTE:
                 *
                 * La contraseña NO debe validarse localmente.
                 * La verificación real corresponde al backend.
                 */


                if (password.isNotBlank()) {

                    /*
                     * TEMPORAL:
                     *
                     * Mientras todavía no está conectado
                     * el backend, únicamente comprobamos
                     * que exista texto.
                     *
                     * Esto NO significa que la contraseña
                     * ingresada sea realmente correcta.
                     */

                    showDeletePasswordDialog = false

                    onDeleteAccountClick()
                }
            }
        )
    }
}


// =====================================================================
// TÍTULO DE SECCIÓN
// =====================================================================

@Composable
private fun SettingsSectionHeader(
    title: String,
    description: String,
    navy: Color,
    secondaryText: Color
) {

    Column {

        Text(
            text = title,
            color = navy,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )


        Text(
            text = description,
            color = secondaryText,
            fontFamily = FontFamily.Serif,
            fontSize = 14.sp,
            lineHeight = 17.sp
        )
    }
}


// =====================================================================
// FILA DE INFORMACIÓN
//
// No incluye flecha porque no representa una acción.
// =====================================================================

@Composable
private fun SettingsInformationRow(
    icon: ImageVector,
    title: String,
    value: String,
    navy: Color,
    secondaryText: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 17.dp,
                vertical = 11.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = navy,
            modifier = Modifier.size(25.dp)
        )


        Spacer(
            modifier =
                Modifier.width(18.dp)
        )


        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = navy,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )


            Text(
                text = value,
                color = secondaryText,
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


// =====================================================================
// FILA DE ACCIÓN
//
// Las opciones que navegan o realizan una acción conservan la flecha.
// =====================================================================

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    navy: Color,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 17.dp,
                vertical = 14.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = navy,
            modifier = Modifier.size(25.dp)
        )


        Spacer(
            modifier =
                Modifier.width(18.dp)
        )


        Text(
            text = title,
            color = navy,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )


        Icon(
            imageVector =
                Icons.Default.ArrowForwardIos,

            contentDescription = null,

            tint = navy,

            modifier =
                Modifier.size(15.dp)
        )
    }
}


// =====================================================================
// DIVISOR
// =====================================================================

@Composable
private fun SettingsDivider(
    color: Color
) {

    HorizontalDivider(
        modifier = Modifier.padding(
            start = 60.dp,
            end = 16.dp
        ),

        thickness = 0.7.dp,

        color = color
    )
}


// =====================================================================
// PRIMERA CONFIRMACIÓN PARA BORRAR CUENTA
//
// Este diálogo únicamente confirma que el usuario realmente desea
// continuar.
//
// Todavía NO solicita la contraseña y NO elimina la cuenta.
// =====================================================================

@Composable
private fun DeleteAccountConfirmationDialog(
    navy: Color,
    cardBackground: Color,
    secondaryText: Color,
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {

    val colors = AppTheme.colors


    Dialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(14.dp)
                )
                .background(
                    cardBackground
                )
                .padding(
                    horizontal = 22.dp,
                    vertical = 20.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // =========================================================
            // ICONO
            // =========================================================

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color =
                            colors.pinkPrincipal.copy(
                                alpha = 0.11f
                            ),

                        shape = CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.DeleteOutline,

                    contentDescription = null,

                    tint =
                        colors.pinkPrincipal,

                    modifier =
                        Modifier.size(28.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // =========================================================
            // TÍTULO
            // =========================================================

            Text(
                text =
                    "¿Deseas borrar tu cuenta?",

                color = navy,

                fontFamily =
                    FontFamily.Serif,

                fontWeight =
                    FontWeight.Bold,

                fontSize = 20.sp,

                lineHeight = 22.sp,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(9.dp)
            )


            // =========================================================
            // ADVERTENCIA
            // =========================================================

            Text(
                text =
                    "Esta acción es permanente y no se puede deshacer. " +
                            "Para continuar, deberás confirmar tu identidad " +
                            "ingresando tu contraseña.",

                color =
                    secondaryText,

                fontFamily =
                    FontFamily.Serif,

                fontSize = 12.sp,

                lineHeight = 17.sp,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            // =========================================================
            // CONTINUAR
            // =========================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(
                        RoundedCornerShape(6.dp)
                    )
                    .background(
                        colors.pinkPrincipal
                    )
                    .clickable {
                        onContinue()
                    },

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "Continuar",

                    color =
                        Color.White,

                    fontSize = 14.sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }


            Spacer(
                modifier =
                    Modifier.height(9.dp)
            )


            // =========================================================
            // CANCELAR
            // =========================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .border(
                        width = 1.dp,

                        color =
                            colors.pinkPrincipal,

                        shape =
                            RoundedCornerShape(6.dp)
                    )
                    .clip(
                        RoundedCornerShape(6.dp)
                    )
                    .clickable {
                        onDismiss()
                    },

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "Cancelar",

                    // Contraste reforzado en modo claro y oscuro.
                    color = colors.text,

                    fontSize = 16.sp,

                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// =====================================================================
// SEGUNDA CONFIRMACIÓN PARA BORRAR CUENTA
//
// Aquí sí se solicita la contraseña actual para verificar la identidad.
// =====================================================================

@Composable
private fun DeleteAccountDialog(
    navy: Color,
    cardBackground: Color,
    secondaryText: Color,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {

    val colors = AppTheme.colors

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }


    Dialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(14.dp)
                )
                .background(
                    cardBackground
                )
                .padding(
                    horizontal = 22.dp,
                    vertical = 20.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // =========================================================
            // ICONO
            // =========================================================

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color =
                            colors.pinkPrincipal.copy(
                                alpha = 0.11f
                            ),

                        shape = CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.DeleteOutline,

                    contentDescription = null,

                    tint =
                        colors.pinkPrincipal,

                    modifier =
                        Modifier.size(28.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // =========================================================
            // TÍTULO
            // =========================================================

            Text(
                text =
                    "Confirma tu identidad",

                color = navy,

                fontFamily =
                    FontFamily.Serif,

                fontWeight =
                    FontWeight.Bold,

                fontSize = 20.sp,

                lineHeight = 22.sp,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(9.dp)
            )


            // =========================================================
            // TEXTO
            // =========================================================

            Text(
                text =
                    "Ingresa tu contraseña actual para confirmar " +
                            "que deseas borrar tu cuenta.",

                color =
                    secondaryText,

                fontFamily =
                    FontFamily.Serif,

                fontSize = 12.sp,

                lineHeight = 17.sp,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            // =========================================================
            // CONTRASEÑA ACTUAL
            // =========================================================

            Column(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Contraseña actual",

                    color = navy,

                    fontFamily =
                        FontFamily.Serif,

                    fontWeight =
                        FontWeight.Medium,

                    fontSize = 13.sp
                )


                Spacer(
                    modifier =
                        Modifier.height(7.dp)
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,

                            color =
                                navy.copy(
                                    alpha = 0.20f
                                ),

                            shape =
                                RoundedCornerShape(8.dp)
                        )
                        .padding(
                            start = 13.dp,
                            end = 4.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    BasicTextField(
                        value = password,

                        onValueChange = {
                            password = it
                        },

                        singleLine = true,

                        visualTransformation =
                            if (passwordVisible) {

                                VisualTransformation.None

                            } else {

                                PasswordVisualTransformation()
                            },

                        textStyle =
                            TextStyle(
                                color = navy,
                                fontSize = 14.sp
                            ),

                        cursorBrush =
                            SolidColor(
                                colors.pinkPrincipal
                            ),

                        modifier =
                            Modifier.weight(1f),

                        decorationBox = { innerTextField ->

                            Box(
                                contentAlignment =
                                    Alignment.CenterStart
                            ) {

                                if (password.isEmpty()) {

                                    Text(
                                        text =
                                            "Ingresa tu contraseña",

                                        color =
                                            secondaryText.copy(
                                                alpha = 0.65f
                                            ),

                                        fontSize = 13.sp
                                    )
                                }


                                innerTextField()
                            }
                        }
                    )


                    // =================================================
                    // MOSTRAR / OCULTAR CONTRASEÑA
                    // =================================================

                    IconButton(
                        onClick = {

                            passwordVisible =
                                !passwordVisible
                        }
                    ) {

                        Icon(
                            imageVector =
                                if (passwordVisible) {

                                    Icons.Default.Visibility

                                } else {

                                    Icons.Default.VisibilityOff
                                },

                            contentDescription =
                                if (passwordVisible) {

                                    "Mostrar contraseña"

                                } else {

                                    "Contraseña oculta"
                                },

                            tint = navy,

                            modifier =
                                Modifier.size(20.dp)
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            // =========================================================
            // BOTÓN BORRAR CUENTA
            // =========================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(
                        RoundedCornerShape(6.dp)
                    )
                    .background(
                        if (password.isNotBlank()) {

                            colors.pinkPrincipal

                        } else {

                            colors.pinkPrincipal.copy(
                                alpha = 0.40f
                            )
                        }
                    )
                    .clickable(
                        enabled =
                            password.isNotBlank()
                    ) {

                        onConfirm(password)
                    },

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        "Borrar cuenta",

                    color =
                        Color.White,

                    fontSize = 14.sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }


            Spacer(
                modifier =
                    Modifier.height(9.dp)
            )


            // =========================================================
            // CANCELAR
            // =========================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .border(
                        width = 1.dp,

                        color =
                            colors.pinkPrincipal,

                        shape =
                            RoundedCornerShape(6.dp)
                    )
                    .clip(
                        RoundedCornerShape(6.dp)
                    )
                    .clickable {
                        onDismiss()
                    },

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "Cancelar",

                    // Contraste reforzado en modo claro y oscuro.
                    color = colors.text,

                    fontSize = 16.sp,

                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// =====================================================================
// PREVIEW LIGHT
// =====================================================================

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


// =====================================================================
// PREVIEW DARK
// =====================================================================

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