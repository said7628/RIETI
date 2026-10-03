package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme


@Composable
fun IniciarsePantalla2(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {

    // =========================================================
    // ESTADOS DEL FORMULARIO
    // =========================================================

    var correo by remember {
        mutableStateOf("")
    }

    var contrasena by remember {
        mutableStateOf("")
    }

    val colors = AppTheme.colors


    // =========================================================
    // COLORES DE LOS BOTONES
    // =========================================================

    val loginBackground =
        if (darkTheme) {
            Color(0xFF24A194)
        } else {
            Color(0xFF16BFAF)
        }

    val loginContent =
        Color.White


    val registerBackground =
        if (darkTheme) {
            Color(0xFFFFEEF4)
        } else {
            Color(0xFFFFF3F7)
        }

    val registerContent =
        if (darkTheme) {
            Color(0xFF160A10)
        } else {
            Color(0xFF131026)
        }


    // =========================================================
    // COLOR DEL BORDE DE LOS CAMPOS SIN SELECCIONAR
    // =========================================================

    val unfocusedFieldColor =
        if (darkTheme) {
            Color.White
        } else {
            Color(0xFF16BFAF)
        }


    // =========================================================
    // PANTALLA
    // =========================================================

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.surface
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    WindowInsets.safeDrawing.asPaddingValues()
                )
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                // =====================================================
                // CABECERA RIETI
                // =====================================================

                CabeceraLogin(
                    darkTheme = darkTheme,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )


                Spacer(
                    modifier = Modifier.height(25.dp)
                )


                // =====================================================
                // TÍTULO
                // =====================================================

                Text(
                    text = "Iniciar Sesión",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    color = colors.text
                )


                Spacer(
                    modifier = Modifier.height(13.dp)
                )


                // =====================================================
                // LÍNEA DEBAJO DEL TÍTULO
                // =====================================================

                Surface(
                    modifier = Modifier.size(
                        width = 290.dp,
                        height = 2.dp
                    ),
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFFF0060)
                ) {}


                Spacer(
                    modifier = Modifier.height(22.dp)
                )


                // =====================================================
                // CORREO
                // =====================================================

                Text(
                    text = "Correo:",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left,
                    color = colors.text
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(58.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(14.dp),

                    colors = OutlinedTextFieldDefaults.colors(

                        // TEXTO
                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text,

                        // CURSOR ROSA
                        cursorColor = Color(0xFFFF0060),

                        // SELECCIONADO = ROSA
                        focusedBorderColor = Color(0xFFFF0060),

                        // SIN SELECCIONAR
                        // CLARO = TURQUESA
                        // OSCURO = BLANCO
                        unfocusedBorderColor = unfocusedFieldColor,

                        // FONDO TRANSPARENTE
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )


                Spacer(
                    modifier = Modifier.height(18.dp)
                )


                // =====================================================
                // CONTRASEÑA
                // =====================================================

                Text(
                    text = "Contraseña:",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left,
                    color = colors.text
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                OutlinedTextField(
                    value = contrasena,
                    onValueChange = {
                        contrasena = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(58.dp),
                    singleLine = true,
                    visualTransformation =
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape = RoundedCornerShape(14.dp),

                    colors = OutlinedTextFieldDefaults.colors(

                        // TEXTO
                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text,

                        // CURSOR ROSA
                        cursorColor = Color(0xFFFF0060),

                        // SELECCIONADO = ROSA
                        focusedBorderColor = Color(0xFFFF0060),

                        // SIN SELECCIONAR
                        // CLARO = TURQUESA
                        // OSCURO = BLANCO
                        unfocusedBorderColor = unfocusedFieldColor,

                        // FONDO TRANSPARENTE
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )


                // =====================================================
                // RECUPERAR CONTRASEÑA
                // =====================================================

                Text(
                    text = "Recuperar contraseña",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 8.dp,
                            end = 32.dp
                        )
                        .clickable {
                            onForgotPasswordClick()
                        },
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Right,
                    textDecoration = TextDecoration.Underline,
                    color = Color(0xFFFF0060)
                )


                Spacer(
                    modifier = Modifier.height(24.dp)
                )


                // =====================================================
                // BOTÓN INICIAR SESIÓN
                // =====================================================

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(70.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = loginBackground,
                        contentColor = loginContent
                    )
                ) {

                    Text(
                        text = "INICIAR SESIÓN",
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = loginContent
                    )
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                // =====================================================
                // BOTÓN CREAR CUENTA
                // =====================================================

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(70.dp)
                        .clickable {
                            onRegisterClick()
                        },
                    shape = RoundedCornerShape(15.dp),
                    color = registerBackground,
                    shadowElevation = 4.dp
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = 18.dp,
                                end = 18.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Outlined.PersonOutline,
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
                            tint = Color(0xFFFF0060)
                        )


                        Spacer(
                            modifier = Modifier.size(12.dp)
                        )


                        Box(
                            modifier = Modifier
                                .size(
                                    width = 1.dp,
                                    height = 32.dp
                                )
                                .background(
                                    Color(0xFFFF0060)
                                        .copy(alpha = 0.35f)
                                )
                        )


                        Text(
                            text = "CREAR CUENTA",
                            modifier = Modifier.weight(1f),
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = registerContent
                        )


                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
                            tint =
                                if (darkTheme) {
                                    registerContent
                                } else {
                                    Color(0xFFFF0060)
                                }
                        )
                    }
                }


                // =====================================================
                // ESPACIO FLEXIBLE
                // =====================================================

                Spacer(
                    modifier = Modifier.weight(1f)
                )


                // =====================================================
                // SELECTOR DE TEMA
                // =====================================================

                SelectorTemaLogin(
                    darkTheme = darkTheme,
                    onThemeChange = onThemeChange,
                    textColor = colors.text
                )


                Spacer(
                    modifier = Modifier.height(25.dp)
                )
            }
        }
    }
}


// =====================================================================
// CABECERA RIETI
// =====================================================================

@Composable
private fun CabeceraLogin(
    darkTheme: Boolean,
    modifier: Modifier = Modifier
) {

    val coloresCabecera =
        if (darkTheme) {

            // =========================================================
            // MODO OSCURO
            // Rosa intenso -> frambuesa -> vino
            // =========================================================

            listOf(
                Color(0xFFFF0060),
                Color(0xFFD71964),
                Color(0xFF85113F)
            )

        } else {

            // =========================================================
            // MODO CLARO
            // Rosa intenso -> rosa medio -> rosa pastel
            // =========================================================

            listOf(
                Color(0xFFE4007C),
                Color(0xFFFF62A0),
                Color(0xFFECABBE)
            )
        }


    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = coloresCabecera
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            // =========================================================
            // RIETI
            // =========================================================

            Text(
                text = "RIETI",
                fontSize = 55.sp,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // =========================================================
            // LÍNEA DEBAJO DE RIETI
            // =========================================================

            Surface(
                modifier = Modifier.size(
                    width = 90.dp,
                    height = 3.dp
                ),
                shape = RoundedCornerShape(50),
                color = Color(0xFFE770A0)
            ) {}
        }
    }
}


// =====================================================================
// SELECTOR DE TEMA
// =====================================================================

@Composable
private fun SelectorTemaLogin(
    darkTheme: Boolean,
    onThemeChange: () -> Unit,
    textColor: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 45.dp)
            .clickable {
                onThemeChange()
            },
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.Center
    ) {


        // =============================================================
        // LÍNEA IZQUIERDA
        // =============================================================

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color(0xFFE4007C)
                .copy(alpha = 0.35f)
        )


        Spacer(
            modifier = Modifier.size(10.dp)
        )


        // =============================================================
        // ICONO DEL TEMA
        // =============================================================

        Icon(
            imageVector =
                if (darkTheme) {
                    Icons.Outlined.DarkMode
                } else {
                    Icons.Outlined.LightMode
                },
            contentDescription = null,
            modifier = Modifier.size(21.dp),
            tint = Color(0xFFE4007C)
        )


        Spacer(
            modifier = Modifier.size(7.dp)
        )


        // =============================================================
        // TEXTO
        // =============================================================

        Text(
            text =
                if (darkTheme) {
                    "Modo claro"
                } else {
                    "Modo oscuro"
                },
            fontSize = 12.sp,
            fontFamily = FontFamily.Serif,
            color = textColor
        )


        Spacer(
            modifier = Modifier.size(10.dp)
        )


        // =============================================================
        // LÍNEA DERECHA
        // =============================================================

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color(0xFFE4007C)
                .copy(alpha = 0.35f)
        )
    }
}


// =====================================================================
// PREVIEW LIGHT
// =====================================================================

@Preview(
    name = "RIETI Login - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun IniciarsePantalla2LightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        IniciarsePantalla2(
            onLoginClick = {},
            onRegisterClick = {},
            onForgotPasswordClick = {},
            darkTheme = false,
            onThemeChange = {}
        )
    }
}


// =====================================================================
// PREVIEW DARK
// =====================================================================

@Preview(
    name = "RIETI Login - Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun IniciarsePantalla2DarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        IniciarsePantalla2(
            onLoginClick = {},
            onRegisterClick = {},
            onForgotPasswordClick = {},
            darkTheme = true,
            onThemeChange = {}
        )
    }
}