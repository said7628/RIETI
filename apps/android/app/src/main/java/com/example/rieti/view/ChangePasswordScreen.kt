package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.TopBar


@Composable
fun ChangePasswordScreen(
    email: String,
    onChangePasswordClick: (
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) -> Unit,
    onHomeClick: () -> Unit,
    onReportClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val colors = AppTheme.colors

    var currentPassword by rememberSaveable {
        mutableStateOf("")
    }

    var newPassword by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }


    Scaffold(

        containerColor = colors.surface,

        // =====================================================
        // TOP BAR
        // =====================================================

        topBar = {

            Column {

                TopBar(
                    onMenuClick = {}
                )

                /*
                 * Franja decorativa debajo del TopBar.
                 */
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .background(colors.pinkPrincipal)
                )
            }
        },


        // =====================================================
        // BOTTOM BAR
        // =====================================================

        bottomBar = {

            BottomBar(

                onHomeClick = {
                    onHomeClick()
                },

                onReportClick = {
                    onReportClick()
                },

                onSettingsClick = {
                    onSettingsClick()
                }
            )
        }

    ) { innerPadding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.surface)
                .padding(horizontal = 8.dp)
        ) {


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =================================================
            // TÍTULO
            // =================================================

            Text(
                text = "Cambiar contraseña",
                color = colors.titles,
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )


            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.64f)
                    .padding(
                        start = 8.dp,
                        top = 4.dp
                    ),
                thickness = 1.5.dp,
                color = colors.titles
            )


            Spacer(
                modifier = Modifier.height(26.dp)
            )


            // =================================================
            // CORREO ELECTRÓNICO
            // =================================================

            EmailRow(
                email = email
            )


            HorizontalDivider(
                thickness = 1.dp,
                color = colors.titles
            )


            Spacer(
                modifier = Modifier.height(26.dp)
            )


            // =================================================
            // CONTRASEÑA ACTUAL
            // =================================================

            PasswordField(
                label = "Introduce tu contraseña actual",
                value = currentPassword,

                onValueChange = {
                    currentPassword = it
                }
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =================================================
            // NUEVA CONTRASEÑA
            // =================================================

            PasswordField(
                label = "Nueva contraseña",
                value = newPassword,

                onValueChange = {
                    newPassword = it
                }
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =================================================
            // CONFIRMAR CONTRASEÑA
            // =================================================

            PasswordField(
                label = "Confirmar contraseña",
                value = confirmPassword,

                onValueChange = {
                    confirmPassword = it
                }
            )


            Spacer(
                modifier = Modifier.height(45.dp)
            )


            // =================================================
            // BOTÓN CAMBIAR CONTRASEÑA
            // =================================================

            ChangePasswordButton(

                onClick = {

                    onChangePasswordClick(
                        currentPassword,
                        newPassword,
                        confirmPassword
                    )
                }
            )
        }
    }
}


// =================================================================
// CORREO ELECTRÓNICO
// =================================================================

@Composable
private fun EmailRow(
    email: String
) {

    val colors = AppTheme.colors

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 4.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Correo electrónico",
            color = colors.text,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = email,
            color = colors.text,
            fontSize = 15.sp
        )
    }
}


// =================================================================
// CAMPO DE CONTRASEÑA
// =================================================================

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {

    val colors = AppTheme.colors

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = label,
            color = colors.text,
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

            textStyle = TextStyle(
                color = colors.textTyping,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),

            cursorBrush = SolidColor(
                colors.textTyping
            ),

            modifier = Modifier
                .fillMaxWidth(0.78f)
                .height(28.dp)
                .clip(
                    RoundedCornerShape(7.dp)
                )
                .background(
                    colors.blueBoxes
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 5.dp
                )
        )
    }
}


// =================================================================
// BOTÓN CAMBIAR CONTRASEÑA
// =================================================================

@Composable
private fun ChangePasswordButton(
    onClick: () -> Unit
) {

    val colors = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clip(
                RoundedCornerShape(7.dp)
            )
            .background(
                colors.bluePrincipal
            )
            .then(
                Modifier
                    .padding(horizontal = 4.dp)
            )
            .clickable {
                onClick()
            },

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = "Cambiar\ncontraseña",
            color = colors.onPrimary,
            fontFamily = FontFamily.Serif,
            fontSize = 31.sp,
            lineHeight = 37.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}


// =================================================================
// BORRAR LAS PRUEBAS DESPUES DE IMPLEMENTAR LA NAVEGACIÓN

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

            onChangePasswordClick = { _, _, _ -> },

            onHomeClick = {},

            onReportClick = {},

            onSettingsClick = {}
        )
    }
}



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

            onChangePasswordClick = { _, _, _ -> },

            onHomeClick = {},

            onReportClick = {},

            onSettingsClick = {}
        )
    }
}