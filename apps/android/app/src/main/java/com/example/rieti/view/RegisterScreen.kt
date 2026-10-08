package com.example.rieti.view

import android.content.res.Configuration
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme


@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit,
    onCreateAccountClick: (String, String, String) -> Unit,
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {

    // =========================================================
    // ESTADOS DE LOS CAMPOS
    // =========================================================

    var nombre by remember {
        mutableStateOf("")
    }

    var correo by remember {
        mutableStateOf("")
    }

    var contrasena by remember {
        mutableStateOf("")
    }

    var confirmarContrasena by remember {
        mutableStateOf("")
    }


    // =========================================================
    // VISIBILIDAD DE CONTRASEÑAS
    // =========================================================
    //
    // Cada campo tiene su propio estado.
    //
    // De esta forma mostrar la contraseña principal NO muestra
    // automáticamente la contraseña de confirmación.
    // =========================================================

    var contrasenaVisible by remember {
        mutableStateOf(false)
    }

    var confirmarContrasenaVisible by remember {
        mutableStateOf(false)
    }


    // =========================================================
    // ESTADOS DE ERROR
    // =========================================================

    var errorNombre by remember {
        mutableStateOf<String?>(null)
    }

    var errorCorreo by remember {
        mutableStateOf<String?>(null)
    }

    var errorContrasena by remember {
        mutableStateOf<String?>(null)
    }

    var errorConfirmarContrasena by remember {
        mutableStateOf<String?>(null)
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

    val createAccountContent = Color.White


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
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
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
                // NOMBRE
                // =====================================================

                Text(
                    text = "Nombre:",
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
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        errorNombre = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(58.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    isError = errorNombre != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text,
                        cursorColor = Color(0xFFFF0060),
                        focusedBorderColor = Color(0xFFFF0060),
                        unfocusedBorderColor = unfocusedFieldColor,
                        errorBorderColor = Color(0xFFFF0060),
                        errorCursorColor = Color(0xFFFF0060),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (errorNombre != null) {
                        Text(
                            text = errorNombre!!,
                            color = Color(0xFFFF0060),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif,
                            maxLines = 1
                        )
                    }
                }

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
                        errorCorreo = null
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

                    isError = errorCorreo != null,

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text,

                        cursorColor = Color(0xFFFF0060),

                        focusedBorderColor = Color(0xFFFF0060),
                        unfocusedBorderColor = unfocusedFieldColor,

                        errorBorderColor = Color(0xFFFF0060),
                        errorCursorColor = Color(0xFFFF0060),

                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    )
                )


                // =====================================================
                // ESPACIO FIJO PARA ALERTA DEL CORREO
                //
                // IMPORTANTE:
                // Este Box siempre existe.
                // Por eso la pantalla NO cambia de tamaño.
                // =====================================================

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.CenterStart
                ) {

                    if (errorCorreo != null) {

                        Text(
                            text = errorCorreo!!,
                            color = Color(0xFFFF0060),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif,
                            maxLines = 1
                        )
                    }
                }


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

                        errorContrasena = null
                        errorConfirmarContrasena = null
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(58.dp),

                    singleLine = true,


                    // =================================================
                    // MOSTRAR / OCULTAR CONTRASEÑA
                    // =================================================

                    visualTransformation =
                        if (contrasenaVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },


                    // =================================================
                    // OJITO DE CONTRASEÑA
                    // =================================================

                    trailingIcon = {

                        IconButton(
                            onClick = {
                                contrasenaVisible =
                                    !contrasenaVisible
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (contrasenaVisible) {
                                        Icons.Default.Visibility
                                    } else {
                                        Icons.Default.VisibilityOff
                                    },

                                contentDescription =
                                    if (contrasenaVisible) {
                                        "Contraseña visible"
                                    } else {
                                        "Contraseña oculta"
                                    },

                                tint =
                                    if (errorContrasena != null) {
                                        Color(0xFFFF0060)
                                    } else {
                                        colors.text
                                    }
                            )
                        }
                    },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),

                    shape = RoundedCornerShape(14.dp),

                    isError = errorContrasena != null,

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text,

                        cursorColor = Color(0xFFFF0060),

                        focusedBorderColor = Color(0xFFFF0060),
                        unfocusedBorderColor = unfocusedFieldColor,

                        errorBorderColor = Color(0xFFFF0060),
                        errorCursorColor = Color(0xFFFF0060),

                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    )
                )


                // =====================================================
                // ESPACIO FIJO PARA ALERTA DE CONTRASEÑA
                // =====================================================

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.CenterStart
                ) {

                    if (errorContrasena != null) {

                        Text(
                            text = errorContrasena!!,
                            color = Color(0xFFFF0060),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif,
                            maxLines = 1
                        )
                    }
                }


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
                        errorConfirmarContrasena = null
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(58.dp),

                    singleLine = true,


                    // =================================================
                    // MOSTRAR / OCULTAR CONFIRMACIÓN
                    // =================================================

                    visualTransformation =
                        if (confirmarContrasenaVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },


                    // =================================================
                    // OJITO DE CONFIRMACIÓN
                    // =================================================

                    trailingIcon = {

                        IconButton(
                            onClick = {
                                confirmarContrasenaVisible =
                                    !confirmarContrasenaVisible
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (confirmarContrasenaVisible) {
                                        Icons.Default.Visibility
                                    } else {
                                        Icons.Default.VisibilityOff
                                    },

                                contentDescription =
                                    if (confirmarContrasenaVisible) {
                                        "Confirmación visible"
                                    } else {
                                        "Confirmación oculta"
                                    },

                                tint =
                                    if (errorConfirmarContrasena != null) {
                                        Color(0xFFFF0060)
                                    } else {
                                        colors.text
                                    }
                            )
                        }
                    },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),

                    shape = RoundedCornerShape(14.dp),

                    isError = errorConfirmarContrasena != null,

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text,

                        cursorColor = Color(0xFFFF0060),

                        focusedBorderColor = Color(0xFFFF0060),
                        unfocusedBorderColor = unfocusedFieldColor,

                        errorBorderColor = Color(0xFFFF0060),
                        errorCursorColor = Color(0xFFFF0060),

                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    )
                )


                // =====================================================
                // ESPACIO FIJO PARA ALERTA DE CONFIRMACIÓN
                // =====================================================

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.CenterStart
                ) {

                    if (errorConfirmarContrasena != null) {

                        Text(
                            text = errorConfirmarContrasena!!,
                            color = Color(0xFFFF0060),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif,
                            maxLines = 1
                        )
                    }
                }


                // =====================================================
                // ESPACIO ANTES DE BOTONES
                // =====================================================

                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                // =====================================================
                // BOTÓN CREAR CUENTA
                // =====================================================

                Button(
                    onClick = {

                        // =============================================
                        // LIMPIAR ERRORES ANTERIORES
                        // =============================================

                        errorNombre = null
                        errorCorreo = null
                        errorContrasena = null
                        errorConfirmarContrasena = null

                        var formularioValido = true

                        if (nombre.trim().length < 2) {
                            errorNombre = "Ingresa tu nombre."
                            formularioValido = false
                        }


                        // =============================================
                        // VALIDACIÓN DEL CORREO
                        // =============================================

                        if (correo.isBlank()) {

                            errorCorreo =
                                "Ingresa tu correo electrónico."

                            formularioValido = false

                        } else if (
                            !Patterns.EMAIL_ADDRESS
                                .matcher(correo)
                                .matches()
                        ) {

                            errorCorreo =
                                "Ingresa un correo electrónico válido."

                            formularioValido = false
                        }


                        // =============================================
                        // VALIDACIÓN DE CONTRASEÑA
                        // =============================================

                        if (contrasena.isBlank()) {

                            errorContrasena =
                                "Ingresa una contraseña."

                            formularioValido = false

                        } else if (contrasena.length < 10) {

                            errorContrasena =
                                "Usa al menos 10 caracteres."

                            formularioValido = false
                        }


                        // =============================================
                        // VALIDACIÓN DE CONFIRMACIÓN
                        // =============================================

                        if (confirmarContrasena.isBlank()) {

                            errorConfirmarContrasena =
                                "Confirma tu contraseña."

                            formularioValido = false

                        } else if (
                            contrasena != confirmarContrasena
                        ) {

                            errorConfirmarContrasena =
                                "Las contraseñas no coinciden."

                            formularioValido = false
                        }


                        // =============================================
                        // TODO(BACKEND)
                        // =============================================
                        //
                        // Cuando se conecte el backend:
                        //
                        // 1. Enviar correo y contraseña al ViewModel.
                        // 2. El ViewModel realizará la petición a la API.
                        // 3. Mostrar estado de carga durante el registro.
                        // 4. Procesar los errores del servidor.
                        // 5. Navegar solamente si el registro fue exitoso.
                        //
                        // Por ahora mantenemos la navegación temporal.
                        // =============================================

                        if (formularioValido) {
                            onCreateAccountClick(
                                nombre.trim(),
                                correo.trim(),
                                contrasena
                            )
                        }
                    },

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
                // =====================================================

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(65.dp)
                        .clickable {
                            onLoginClick()
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
                    modifier = Modifier.height(24.dp)
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
// =====================================================================

@Composable
private fun CabeceraCrearCuenta(
    darkTheme: Boolean,
    modifier: Modifier = Modifier
) {

    val coloresCabecera =
        if (darkTheme) {

            listOf(
                Color(0xFFFF0060),
                Color(0xFFD71964),
                Color(0xFF85113F)
            )

        } else {

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

            Text(
                text = "RIETI",
                fontSize = 55.sp,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


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

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color(0xFFE4007C)
                .copy(alpha = 0.35f)
        )


        Spacer(
            modifier = Modifier.size(10.dp)
        )


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


        Text(
            text =
                if (darkTheme) {
                    "Modo oscuro"
                } else {
                    "Modo claro"
                },
            fontSize = 12.sp,
            fontFamily = FontFamily.Serif,
            color = textColor
        )


        Spacer(
            modifier = Modifier.size(10.dp)
        )


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
private fun RegisterScreenLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        RegisterScreen(
            onLoginClick = {},
            onCreateAccountClick = { _, _, _ -> },
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
private fun RegisterScreenDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        RegisterScreen(
            onLoginClick = {},
            onCreateAccountClick = { _, _, _ -> },
            darkTheme = true,
            onThemeChange = {}
        )
    }
}