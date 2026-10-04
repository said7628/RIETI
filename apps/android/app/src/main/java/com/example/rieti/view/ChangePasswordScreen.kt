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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.TopBar


// =====================================================================
// CAMBIAR CONTRASEÑA
// =====================================================================

@Composable
fun ChangePasswordScreen(
    email: String,
    onChangePasswordClick: (
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) -> Unit,
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val colors = AppTheme.colors
    val isDarkTheme = AppTheme.isDarkTheme


    // =================================================================
    // ESTADOS
    // =================================================================

    var currentPassword by rememberSaveable {
        mutableStateOf("")
    }

    var newPassword by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    var showCurrentPassword by rememberSaveable {
        mutableStateOf(false)
    }

    var showNewPassword by rememberSaveable {
        mutableStateOf(false)
    }

    var showConfirmPassword by rememberSaveable {
        mutableStateOf(false)
    }


    // =================================================================
    // COLORES
    // =================================================================

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

    val fieldBackground =
        if (isDarkTheme) {
            colors.navVar
        } else {
            Color(0xFFF9FAFB)
        }

    val secondaryText =
        if (isDarkTheme) {
            colors.text.copy(alpha = 0.65f)
        } else {
            Color(0xFF627894)
        }

    val placeholderColor =
        if (isDarkTheme) {
            colors.text.copy(alpha = 0.45f)
        } else {
            Color(0xFF94A6BB)
        }

    val borderColor =
        if (isDarkTheme) {
            colors.text.copy(alpha = 0.15f)
        } else {
            Color(0xFFDCE2E8)
        }

    val infoBackground =
        if (isDarkTheme) {
            colors.navVar
        } else {
            Color(0xFFF2F7FA)
        }


    // =================================================================
    // PANTALLA
    // =================================================================

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
                    onSettingsClick()
                }
            )
        }

    ) { innerPadding ->


        // =============================================================
        // CONTENIDO
        // =============================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(background)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    start = 12.dp,
                    end = 12.dp,

                    // Un poco más abajo respecto al TopBar
                    top = 24.dp,

                    bottom = 16.dp
                )
        ) {


            // =========================================================
            // TÍTULO
            // =========================================================

            Text(
                text = "Cambiar contraseña",
                color = navy,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 31.sp
            )


            Spacer(
                modifier = Modifier.height(6.dp)
            )


            // =========================================================
            // LÍNEA ROSA
            // =========================================================

            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .background(
                        colors.pinkPrincipal
                    )
            )


            Spacer(
                modifier = Modifier.height(7.dp)
            )


            // =========================================================
            // SUBTÍTULO
            // =========================================================

            Text(
                text = "Actualiza la contraseña de tu cuenta.",
                color = secondaryText,
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                lineHeight = 16.sp
            )


            Spacer(
                modifier = Modifier.height(15.dp)
            )


            // =========================================================
            // CORREO ELECTRÓNICO
            // =========================================================

            PasswordFieldLabel(
                text = "Correo electrónico",
                navy = navy
            )


            Spacer(
                modifier = Modifier.height(5.dp)
            )


            ReadOnlyEmailField(
                email = email,
                navy = navy,
                secondaryText = secondaryText,
                background = fieldBackground,
                borderColor = borderColor
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =========================================================
            // CONTRASEÑA ACTUAL
            // =========================================================

            PasswordFieldLabel(
                text = "Contraseña actual",
                navy = navy
            )


            Spacer(
                modifier = Modifier.height(5.dp)
            )


            PasswordInputField(
                value = currentPassword,

                onValueChange = {
                    currentPassword = it
                },

                placeholder = "Ingresa tu contraseña actual",

                passwordVisible = showCurrentPassword,

                onVisibilityClick = {
                    showCurrentPassword = !showCurrentPassword
                },

                navy = navy,
                placeholderColor = placeholderColor,
                background = fieldBackground,
                borderColor = borderColor
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =========================================================
            // NUEVA CONTRASEÑA
            // =========================================================

            PasswordFieldLabel(
                text = "Nueva contraseña",
                navy = navy
            )


            Spacer(
                modifier = Modifier.height(5.dp)
            )


            PasswordInputField(
                value = newPassword,

                onValueChange = {
                    newPassword = it
                },

                placeholder = "Ingresa tu nueva contraseña",

                passwordVisible = showNewPassword,

                onVisibilityClick = {
                    showNewPassword = !showNewPassword
                },

                navy = navy,
                placeholderColor = placeholderColor,
                background = fieldBackground,
                borderColor = borderColor
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =========================================================
            // CONFIRMAR CONTRASEÑA
            // =========================================================

            PasswordFieldLabel(
                text = "Confirmar contraseña",
                navy = navy
            )


            Spacer(
                modifier = Modifier.height(5.dp)
            )


            PasswordInputField(
                value = confirmPassword,

                onValueChange = {
                    confirmPassword = it
                },

                placeholder = "Confirma tu nueva contraseña",

                passwordVisible = showConfirmPassword,

                onVisibilityClick = {
                    showConfirmPassword = !showConfirmPassword
                },

                navy = navy,
                placeholderColor = placeholderColor,
                background = fieldBackground,
                borderColor = borderColor
            )


            Spacer(
                modifier = Modifier.height(13.dp)
            )


            // =========================================================
            // INFORMACIÓN DE SEGURIDAD
            // =========================================================

            PasswordInformationCard(
                navy = navy,
                secondaryText = secondaryText,
                background = infoBackground
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =========================================================
            // BOTÓN CAMBIAR CONTRASEÑA
            // =========================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(
                        colors.pinkPrincipal
                    )
                    .clickable {

                        /*
                         * TODO(BACKEND):
                         *
                         * Posteriormente el ViewModel/API deberá:
                         *
                         * 1. Validar la contraseña actual.
                         * 2. Validar la nueva contraseña.
                         * 3. Confirmar que ambas coincidan.
                         * 4. Solicitar el cambio al backend.
                         * 5. Mostrar éxito solamente cuando
                         *    el backend confirme el cambio.
                         */

                        onChangePasswordClick(
                            currentPassword,
                            newPassword,
                            confirmPassword
                        )
                    },

                contentAlignment = Alignment.Center
            ) {


                Text(
                    text = "Cambiar contraseña",
                    color = Color.White,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )


                Icon(
                    imageVector =
                        Icons.Default.ArrowForwardIos,

                    contentDescription = null,

                    tint = Color.White,

                    modifier = Modifier
                        .size(17.dp)
                        .align(
                            Alignment.CenterEnd
                        )
                        .offset(
                            x = (-18).dp
                        )
                )
            }


            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}


// =====================================================================
// ETIQUETA DE CAMPO
// =====================================================================

@Composable
private fun PasswordFieldLabel(
    text: String,
    navy: Color
) {

    Text(
        text = text,
        color = navy,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 15.sp
    )
}


// =====================================================================
// CORREO ELECTRÓNICO
// =====================================================================

@Composable
private fun ReadOnlyEmailField(
    email: String,
    navy: Color,
    secondaryText: Color,
    background: Color,
    borderColor: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(
                RoundedCornerShape(7.dp)
            )
            .background(background)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(7.dp)
            )
            .padding(
                horizontal = 14.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {


        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            tint = navy,
            modifier = Modifier.size(20.dp)
        )


        Spacer(
            modifier = Modifier.width(16.dp)
        )


        Text(
            text = email,
            color = secondaryText,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            lineHeight = 15.sp
        )
    }
}


// =====================================================================
// CAMPO DE CONTRASEÑA
// =====================================================================

@Composable
private fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    passwordVisible: Boolean,
    onVisibilityClick: () -> Unit,
    navy: Color,
    placeholderColor: Color,
    background: Color,
    borderColor: Color
) {

    TextField(
        value = value,

        onValueChange = onValueChange,

        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(7.dp)
            ),

        textStyle = TextStyle(
            color = navy,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp
        ),

        placeholder = {

            Text(
                text = placeholder,
                color = placeholderColor,
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp
            )
        },

        leadingIcon = {

            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = navy,
                modifier = Modifier.size(20.dp)
            )
        },

        trailingIcon = {

            IconButton(
                onClick = {
                    onVisibilityClick()
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
                            "Ocultar contraseña"
                        } else {
                            "Mostrar contraseña"
                        },

                    tint = navy,

                    modifier = Modifier.size(20.dp)
                )
            }
        },

        visualTransformation =
            if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),

        singleLine = true,

        shape = RoundedCornerShape(7.dp),

        colors = TextFieldDefaults.colors(
            focusedContainerColor = background,
            unfocusedContainerColor = background,
            disabledContainerColor = background,

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,

            cursorColor = navy
        )
    )
}


// =====================================================================
// INFORMACIÓN DE CONTRASEÑA
// =====================================================================

@Composable
private fun PasswordInformationCard(
    navy: Color,
    secondaryText: Color,
    background: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(7.dp)
            )
            .background(background)
            .padding(
                horizontal = 14.dp,
                vertical = 10.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {


        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = navy,
            modifier = Modifier.size(25.dp)
        )


        Spacer(
            modifier = Modifier.width(11.dp)
        )


        Box(
            modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(
                    navy.copy(
                        alpha = 0.15f
                    )
                )
        )


        Spacer(
            modifier = Modifier.width(11.dp)
        )


        Text(
            text =
                "Tu contraseña debe tener al menos 8 caracteres " +
                        "e incluir una combinación de letras, números y un símbolo.",

            color = secondaryText,
            fontFamily = FontFamily.Serif,
            fontSize = 11.sp,
            lineHeight = 14.sp,

            modifier = Modifier.weight(1f)
        )
    }
}


// =====================================================================
// PREVIEW LIGHT
// =====================================================================

@Preview(
    name = "Change Password - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ChangePasswordLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        ChangePasswordScreen(
            email = "correo@gmail.com",

            onChangePasswordClick = {
                    _,
                    _,
                    _ ->
            },

            onHomeClick = {},

            onAboutUsClick = {},

            onSettingsClick = {}
        )
    }
}


// =====================================================================
// PREVIEW DARK
// =====================================================================

@Preview(
    name = "Change Password - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ChangePasswordDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        ChangePasswordScreen(
            email = "correo@gmail.com",

            onChangePasswordClick = {
                    _,
                    _,
                    _ ->
            },

            onHomeClick = {},

            onAboutUsClick = {},

            onSettingsClick = {}
        )
    }
}