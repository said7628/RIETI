package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.BrandPink
import com.example.rieti.ui.theme.BrandPinkMedium
import com.example.rieti.ui.theme.RIETITheme


@Composable
fun ResetPasswordScreen(
    onResetPasswordClick: (
        recoveryCode: String,
        newPassword: String,
        confirmPassword: String
    ) -> Unit,
    onLoginClick: () -> Unit
) {

    val colors = AppTheme.colors

    var recoveryCode by rememberSaveable {
        mutableStateOf("")
    }

    var newPassword by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    val passwordsMatch =
        newPassword == confirmPassword


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surface)
            .navigationBarsPadding()
    ) {


        // =====================================================
        // ENCABEZADO
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()

                /*
                 * El fondo rosa sí puede llegar hasta arriba,
                 * pero el contenido respeta la barra del sistema.
                 */
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BrandPink,
                            BrandPinkMedium
                        )
                    )
                )
                .statusBarsPadding()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "RIETI",
                    color = colors.text,
                    fontFamily = FontFamily.Serif,
                    fontSize = 55.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }


        // =====================================================
        // CONTENIDO
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 18.dp
                )
        ) {


            /*
             * Antes todo comenzaba inmediatamente
             * después del encabezado.
             *
             * Ahora le damos más aire.
             */
            Spacer(
                modifier = Modifier.height(30.dp)
            )


            // =================================================
            // TÍTULO
            // =================================================

            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(230.dp)
            ) {

                Text(
                    text = "Restablecer contraseña",
                    color = colors.titles,
                    fontFamily = FontFamily.Serif,
                    fontSize = 27.sp,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(
                    modifier = Modifier.height(7.dp)
                )


                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 2.dp,
                    color = colors.titles
                )
            }


            Spacer(
                modifier = Modifier.height(32.dp)
            )


            // =================================================
            // CÓDIGO
            // =================================================

            ResetPasswordField(
                label = "Código de recuperación",
                value = recoveryCode,

                onValueChange = {
                    recoveryCode = it
                }
            )


            Spacer(
                modifier = Modifier.height(25.dp)
            )


            // =================================================
            // NUEVA CONTRASEÑA
            // =================================================

            ResetPasswordField(
                label = "Nueva contraseña",
                value = newPassword,
                isPassword = true,

                onValueChange = {
                    newPassword = it
                }
            )


            Spacer(
                modifier = Modifier.height(25.dp)
            )


            // =================================================
            // CONFIRMAR CONTRASEÑA
            // =================================================

            ResetPasswordField(
                label = "Confirmar contraseña",
                value = confirmPassword,
                isPassword = true,

                onValueChange = {
                    confirmPassword = it
                }
            )


            // =================================================
            // ERROR
            // =================================================

            if (
                confirmPassword.isNotEmpty() &&
                !passwordsMatch
            ) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Las contraseñas no coinciden",
                    color = colors.titles,
                    fontSize = 13.sp
                )
            }


            Spacer(
                modifier = Modifier.height(35.dp)
            )


            // =================================================
            // RESTABLECER CONTRASEÑA
            // =================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(
                        RoundedCornerShape(7.dp)
                    )
                    .background(
                        colors.bluePrincipal
                    )
                    .clickable {

                        if (
                            recoveryCode.isNotBlank() &&
                            newPassword.isNotBlank() &&
                            confirmPassword.isNotBlank() &&
                            passwordsMatch
                        ) {

                            onResetPasswordClick(
                                recoveryCode,
                                newPassword,
                                confirmPassword
                            )
                        }
                    },

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Restablecer contraseña",
                    color = colors.onPrimary,
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }


            /*
             * Empuja el botón de login un poco más abajo
             * para que la pantalla no se vea amontonada.
             */
            Spacer(
                modifier = Modifier.height(36.dp)
            )


            // =================================================
            // INICIAR SESIÓN
            // =================================================

            Box(
                modifier = Modifier
                    .align(
                        Alignment.CenterHorizontally
                    )
                    .width(150.dp)
                    .height(38.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .background(
                        colors.bluePrincipal
                    )
                    .clickable {
                        onLoginClick()
                    },

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Iniciar sesión",
                    color = colors.onPrimary,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp
                )
            }
        }
    }
}


// =================================================================
// CAMPO
// =================================================================

@Composable
private fun ResetPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {

    val colors = AppTheme.colors


    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = label,
            color = colors.text,

            // Lo que me pediste:
            fontSize = 15.sp
        )


        Spacer(
            modifier = Modifier.height(9.dp)
        )


        BasicTextField(
            value = value,

            onValueChange = {
                onValueChange(it)
            },

            singleLine = true,

            visualTransformation =
                if (isPassword) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },

            textStyle = TextStyle(
                color = colors.textTyping,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),

            cursorBrush = SolidColor(
                colors.textTyping
            ),

            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(
                    RoundedCornerShape(7.dp)
                )
                .background(
                    colors.blueBoxes
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                )
        )
    }
}


// =================================================================
// PREVIEW LIGHT
// =================================================================

@Preview(
    name = "Reset Password - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ResetPasswordLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        ResetPasswordScreen(
            onResetPasswordClick = { _, _, _ -> },
            onLoginClick = {}
        )
    }
}


// =================================================================
// PREVIEW DARK
// =================================================================

@Preview(
    name = "Reset Password - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ResetPasswordDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        ResetPasswordScreen(
            onResetPasswordClick = { _, _, _ -> },
            onLoginClick = {}
        )
    }
}