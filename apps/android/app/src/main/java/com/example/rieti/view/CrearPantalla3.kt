package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme


@Composable
fun CrearPantalla3(
    onLoginClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {

    // =========================================================
    // ESTADOS
    // =========================================================

    var correo by remember {
        mutableStateOf("")
    }

    var contrasena by remember {
        mutableStateOf("")
    }

    var confirmarContrasena by remember {
        mutableStateOf("")
    }

    val colors = AppTheme.colors


    // =========================================================
    // COLOR DE CAMPOS SIN SELECCIONAR
    //
    // CLARO  = TURQUESA
    // OSCURO = BLANCO
    // =========================================================

    val unfocusedFieldColor =
        if (darkTheme) {
            Color.White
        } else {
            Color(0xFF16BFAF)
        }


    // =========================================================
    // COLORES BOTÓN CREAR CUENTA
    // =========================================================

    val createAccountBackground =
        if (darkTheme) {
            Color(0xFF24A194)
        } else {
            Color(0xFF16BFAF)
        }

    val createAccountContent =
        Color.White


    // =========================================================
    // COLORES BOTÓN INICIAR SESIÓN
    // =========================================================

    val loginBackground =
        if (darkTheme) {
            Color(0xFFFFEEF4)
        } else {
            Color(0xFFFFF3F7)
        }

    val loginContent =
        if (darkTheme) {
            Color(0xFF160A10)
        } else {
            Color(0xFF131026)
        }


    // =========================================================
    // PANTALLA
    // =========================================================

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = colors.surface
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                // =====================================================
                // CABECERA RIETI
                // =====================================================

                CabeceraCrearCuenta(
                    darkTheme = darkTheme,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                // =====================================================
                // TÍTULO
                // =====================================================

                Text(
                    text = "Crear Cuenta",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    color = colors.text
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                // =====================================================
                // LÍNEA DEBAJO DE CREAR CUENTA
                // =====================================================

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 80.dp),
                    thickness = 2.dp,
                    color = Color(0xFFFF0060)
                )


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                // =====================================================
                // CORREO
                // =====================================================

                Text(
                    text = "Correo:",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 5.dp,
                            bottom = 5.dp,
                            start = 32.dp,
                            end = 32.dp
                        ),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left,
                    color = colors.text
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

                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text,

                        // CURSOR ROSA
                        cursorColor = Color(0xFFFF0060),

                        // SELECCIONADO = ROSA
                        focusedBorderColor = Color(0xFFFF0060),

                        // CLARO = TURQUESA
                        // OSCURO = BLANCO
                        unfocusedBorderColor = unfocusedFieldColor,

                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                // =====================================================
                // CONTRASEÑA
                // =====================================================

                Text(
                    text = "Contraseña:",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 5.dp,
                            bottom = 5.dp,
                            start = 32.dp,
                            end = 32.dp
                        ),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left,
                    color = colors.text
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

                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text,

                        // CURSOR ROSA
                        cursorColor = Color(0xFFFF0060),

                        // SELECCIONADO = ROSA
                        focusedBorderColor = Color(0xFFFF0060),

                        // CLARO = TURQUESA
                        // OSCURO = BLANCO
                        unfocusedBorderColor = unfocusedFieldColor,

                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                // =====================================================
                // CONFIRMAR CONTRASEÑA
                // =====================================================

                Text(
                    text = "Confirmar Contraseña:",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 5.dp,
                            bottom = 5.dp,
                            start = 32.dp,
                            end = 32.dp
                        ),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left,
                    color = colors.text
                )


                OutlinedTextField(
                    value = confirmarContrasena,
                    onValueChange = {
                        confirmarContrasena = it
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

                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text,

                        // CURSOR ROSA
                        cursorColor = Color(0xFFFF0060),

                        // SELECCIONADO = ROSA
                        focusedBorderColor = Color(0xFFFF0060),

                        // CLARO = TURQUESA
                        // OSCURO = BLANCO
                        unfocusedBorderColor = unfocusedFieldColor,

                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )


                // =====================================================
                // ESPACIO ANTES DE BOTONES
                // =====================================================

                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                // =====================================================
                // BOTÓN CREAR CUENTA
                // MISMO ESTILO PRINCIPAL DE PANTALLA 2
                // =====================================================

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(70.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = createAccountBackground,
                        contentColor = createAccountContent
                    )
                ) {

                    Text(
                        text = "CREAR CUENTA",
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = createAccountContent
                    )
                }


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                // =====================================================
                // BOTÓN INICIAR SESIÓN
                // MISMO ESTILO SECUNDARIO DE PANTALLA 2
                // =====================================================

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(65.dp)
                        .clickable {
                            onCreateAccountClick()
                        },
                    shape = RoundedCornerShape(15.dp),
                    color = loginBackground,
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


                        // =================================================
                        // ICONO
                        // =================================================

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Outlined.Login,
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
                            tint = Color(0xFFFF0060)
                        )


                        Spacer(
                            modifier = Modifier.size(12.dp)
                        )


                        // =================================================
                        // DIVISOR VERTICAL
                        // =================================================

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


                        // =================================================
                        // TEXTO
                        // =================================================

                        Text(
                            text = "INICIAR SESIÓN",
                            modifier = Modifier.weight(1f),
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = loginContent
                        )


                        // =================================================
                        // FLECHA
                        // =================================================

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
                            tint =
                                if (darkTheme) {
                                    loginContent
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

                SelectorTemaCrearCuenta(
                    darkTheme = darkTheme,
                    onThemeChange = onThemeChange,
                    textColor = colors.text
                )


                Spacer(
                    modifier = Modifier.height(18.dp)
                )
            }
        }
    }
}


// =====================================================================
// CABECERA RIETI
// MISMA CONFIGURACIÓN DE PANTALLA 2
// =====================================================================

@Composable
private fun CabeceraCrearCuenta(
    darkTheme: Boolean,
    modifier: Modifier = Modifier
) {

    val coloresCabecera =
        if (darkTheme) {

            // =========================================================
            // MODO OSCURO
            // =========================================================

            listOf(
                Color(0xFFFF0060),
                Color(0xFFD71964),
                Color(0xFF85113F)
            )

        } else {

            // =========================================================
            // MODO CLARO
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
            // TU NUEVA CONFIGURACIÓN
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
private fun SelectorTemaCrearCuenta(
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
        verticalAlignment = Alignment.CenterVertically
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
        // ICONO
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
    name = "RIETI Crear Cuenta - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun CrearPantalla3LightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        CrearPantalla3(
            onLoginClick = {},
            onCreateAccountClick = {},
            darkTheme = false,
            onThemeChange = {}
        )
    }
}


// =====================================================================
// PREVIEW DARK
// =====================================================================

@Preview(
    name = "RIETI Crear Cuenta - Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CrearPantalla3DarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        CrearPantalla3(
            onLoginClick = {},
            onCreateAccountClick = {},
            darkTheme = true,
            onThemeChange = {}
        )
    }
}