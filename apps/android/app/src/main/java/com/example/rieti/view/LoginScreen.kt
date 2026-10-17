package com.example.rieti.view

import android.content.res.Configuration
import android.util.Patterns
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme


@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    darkTheme: Boolean,
    onThemeChange: () -> Unit,
    cargando: Boolean = false
) {

    // =========================================================
    // ESTADOS LOCALES DEL FORMULARIO
    // =========================================================
    //
    // Estos estados pertenecen actualmente a la interfaz.
    //
    // correo y contrasena pueden mantenerse como estados locales
    // mientras la arquitectura del formulario lo permita.
    //
    // Cuando se conecte el ViewModel, el equipo puede decidir
    // si estos valores continúan aquí o pasan a formar parte
    // de un UiState.
    // =========================================================

    var correo by remember {
        mutableStateOf("")
    }

    var contrasena by remember {
        mutableStateOf("")
    }


    // =========================================================
    // VISIBILIDAD DE CONTRASEÑA
    // =========================================================
    //
    // Este estado pertenece únicamente a la interfaz.
    //
    // false -> contraseña oculta
    // true  -> contraseña visible
    //
    // No requiere conexión con el backend.
    // =========================================================

    var contrasenaVisible by remember {
        mutableStateOf(false)
    }


    // =========================================================
    // ERRORES DE VALIDACIÓN LOCAL
    // =========================================================
    //
    // IMPORTANTE:
    // Estos errores NO deben eliminarse simplemente por conectar
    // el backend.
    //
    // Android puede comprobar directamente:
    //
    // - Si el correo está vacío.
    // - Si el correo tiene un formato válido.
    // - Si la contraseña está vacía.
    //
    // De esta manera evitamos enviar solicitudes innecesarias
    // al servidor.
    // =========================================================

    var errorCorreo by remember {
        mutableStateOf<String?>(null)
    }

    var errorContrasena by remember {
        mutableStateOf<String?>(null)
    }


    // =========================================================
    // TODO(BACKEND): ESTADO DE CARGA TEMPORAL
    // =========================================================
    //
    // ACTUALMENTE:
    // cargando es un estado local y permanece en false porque
    // todavía no existe una solicitud real al servidor.
    //
    // AL CONECTAR EL BACKEND:
    //
    // 1. Eliminar este estado local si el ViewModel administra
    //    el estado de carga.
    //
    // 2. Obtener el estado desde el ViewModel/UiState.
    //
    // Ejemplo conceptual:
    //
    // val uiState by viewModel.uiState.collectAsState()
    // val cargando = uiState.isLoading
    //
    // NO activar manualmente cargando = true en la implementación
    // actual, ya que todavía no existe una respuesta del servidor
    // que permita regresarlo a false.
    // =========================================================



    // =========================================================
    // TODO(BACKEND): ERROR GENERAL DE AUTENTICACIÓN
    // =========================================================
    //
    // Más adelante deberá existir un estado proveniente del
    // ViewModel para mostrar errores que Android NO puede
    // determinar localmente.
    //
    // Por ejemplo:
    //
    // - Correo o contraseña incorrectos.
    // - Error de conexión.
    // - Error del servidor.
    //
    // NO simulamos estos errores ahora porque requieren una
    // respuesta real del proceso de autenticación.
    //
    // Ejemplo conceptual futuro:
    //
    // val errorAutenticacion = uiState.errorMessage
    //
    // =========================================================


    val colors = AppTheme.colors


    // =========================================================
    // COLORES DE LA INTERFAZ
    // =========================================================

    val loginBackground =
        if (darkTheme) {
            Color(0xFF24A194)
        } else {
            Color(0xFF16BFAF)
        }

    val loginContent = Color.White

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

    val unfocusedFieldColor =
        if (darkTheme) {
            Color.White
        } else {
            Color(0xFF16BFAF)
        }

    val errorColor =
        Color(0xFFD32F2F)


    // =========================================================
    // VALIDACIÓN LOCAL DEL FORMULARIO
    // =========================================================
    //
    // CONSERVAR AL CONECTAR EL BACKEND.
    //
    // Esta función únicamente valida información que puede ser
    // comprobada directamente desde Android.
    //
    // NO comprueba:
    //
    // - Si el usuario existe.
    // - Si el correo está registrado.
    // - Si la contraseña corresponde al usuario.
    //
    // Esas comprobaciones serán responsabilidad del backend.
    // =========================================================

    fun validarFormulario(): Boolean {

        var formularioValido = true


        // -----------------------------------------------------
        // VALIDACIÓN LOCAL DEL CORREO
        // -----------------------------------------------------

        errorCorreo =
            when {

                correo.isBlank() -> {

                    formularioValido = false

                    "El correo electrónico es obligatorio."
                }

                !Patterns.EMAIL_ADDRESS
                    .matcher(correo.trim())
                    .matches() -> {

                    formularioValido = false

                    "Ingresa un correo electrónico válido."
                }

                else -> {
                    null
                }
            }


        // -----------------------------------------------------
        // VALIDACIÓN LOCAL DE LA CONTRASEÑA
        // -----------------------------------------------------

        errorContrasena =
            if (contrasena.isBlank()) {

                formularioValido = false

                "La contraseña es obligatoria."

            } else {

                null
            }


        return formularioValido
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
                horizontalAlignment =
                    Alignment.CenterHorizontally
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
                // LÍNEA DEL TÍTULO
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

                        /*
                         * VALIDACIÓN LOCAL:
                         *
                         * Si el usuario comienza a corregir el campo,
                         * eliminamos el mensaje de error anterior.
                         *
                         * Esta lógica puede conservarse al conectar
                         * el backend.
                         */
                        if (errorCorreo != null) {
                            errorCorreo = null
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(58.dp),

                    singleLine = true,

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),

                    shape =
                        RoundedCornerShape(14.dp),

                    isError =
                        errorCorreo != null,

                    colors =
                        OutlinedTextFieldDefaults.colors(

                            focusedTextColor =
                                colors.text,

                            unfocusedTextColor =
                                colors.text,

                            cursorColor =
                                Color(0xFFFF0060),

                            focusedBorderColor =
                                Color(0xFFFF0060),

                            unfocusedBorderColor =
                                unfocusedFieldColor,

                            errorBorderColor =
                                errorColor,

                            errorCursorColor =
                                errorColor,

                            focusedContainerColor =
                                Color.Transparent,

                            unfocusedContainerColor =
                                Color.Transparent,

                            errorContainerColor =
                                Color.Transparent
                        )
                )


                // =====================================================
                // ESPACIO FIJO PARA ERROR DEL CORREO
                //
                // Este espacio existe incluso cuando no hay error.
                // De esta forma los elementos inferiores no se mueven.
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
                            color = errorColor,
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

                        /*
                         * VALIDACIÓN LOCAL:
                         *
                         * El mensaje de campo obligatorio desaparece
                         * cuando el usuario comienza a escribir.
                         *
                         * Conservar al conectar el backend.
                         */
                        if (errorContrasena != null) {
                            errorContrasena = null
                        }
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
                    // OJITO
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
                                        errorColor
                                    } else {
                                        colors.text
                                    }
                            )
                        }
                    },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),

                    shape =
                        RoundedCornerShape(14.dp),

                    isError =
                        errorContrasena != null,

                    colors =
                        OutlinedTextFieldDefaults.colors(

                            focusedTextColor =
                                colors.text,

                            unfocusedTextColor =
                                colors.text,

                            cursorColor =
                                Color(0xFFFF0060),

                            focusedBorderColor =
                                Color(0xFFFF0060),

                            unfocusedBorderColor =
                                unfocusedFieldColor,

                            errorBorderColor =
                                errorColor,

                            errorCursorColor =
                                errorColor,

                            focusedContainerColor =
                                Color.Transparent,

                            unfocusedContainerColor =
                                Color.Transparent,

                            errorContainerColor =
                                Color.Transparent
                        )
                )


                // =====================================================
                // ESPACIO FIJO PARA ERROR DE CONTRASEÑA
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
                            color = errorColor,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif,
                            maxLines = 1
                        )
                    }
                }


                // =====================================================
                // RECUPERAR CONTRASEÑA
                // =====================================================

                Text(
                    text = "Recuperar contraseña",

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 4.dp,
                            end = 32.dp
                        )
                        .clickable(
                            enabled = !cargando
                        ) {
                            onForgotPasswordClick()
                        },

                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Right,
                    textDecoration =
                        TextDecoration.Underline,
                    color = Color(0xFFFF0060)
                )


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                // =====================================================
                // TODO(BACKEND): MENSAJE GENERAL DE AUTENTICACIÓN
                // =====================================================
                //
                // En esta zona puede mostrarse posteriormente el error
                // recibido desde el ViewModel.
                //
                // Ejemplo conceptual:
                //
                // errorAutenticacion?.let { mensaje ->
                //     Text(
                //         text = mensaje,
                //         color = errorColor
                //     )
                // }
                //
                // Esto permitirá mostrar mensajes como:
                //
                // "Correo o contraseña incorrectos."
                //
                // IMPORTANTE:
                // Cuando se implemente, conviene reservar también
                // un espacio fijo para este mensaje para evitar que
                // los botones se desplacen.
                // =====================================================


                // =====================================================
                // BOTÓN INICIAR SESIÓN
                // =====================================================

                Button(
                    onClick = {

                        /*
                         * PRIMER PASO:
                         *
                         * Esta validación SE CONSERVA después de
                         * conectar el backend.
                         */
                        if (validarFormulario()) {


                            // =========================================
                            // TODO(BACKEND): NAVEGACIÓN PROVISIONAL
                            // =========================================
                            //
                            // ACTUALMENTE:
                            //
                            // Permitimos entrar a la siguiente pantalla
                            // cuando el formulario tiene un correo con
                            // formato válido y una contraseña no vacía.
                            //
                            // Esto existe únicamente para permitir
                            // continuar desarrollando y probando RIETI
                            // mientras todavía no existe autenticación.
                            //
                            //
                            // AL CONECTAR EL BACKEND:
                            //
                            // ELIMINAR/REEMPLAZAR esta llamada directa:
                            //
                            //     onLoginClick()
                            //
                            //
                            // POR la llamada correspondiente al
                            // ViewModel.
                            //
                            // Ejemplo conceptual:
                            //
                            //     viewModel.iniciarSesion(
                            //         correo = correo.trim(),
                            //         contrasena = contrasena
                            //     )
                            //
                            //
                            // Después:
                            //
                            // onLoginClick() debe ejecutarse SOLAMENTE
                            // cuando el ViewModel indique que el backend
                            // confirmó una autenticación exitosa.
                            //
                            //
                            // FLUJO FUTURO:
                            //
                            // validarFormulario()
                            //          ↓
                            // ViewModel
                            //          ↓
                            // API / Backend
                            //          ↓
                            // autenticación
                            //       ↙      ↘
                            //    éxito     error
                            //      ↓         ↓
                            // navegar     mostrar
                            //             mensaje
                            //
                            // =========================================

                            onLoginClick(

                                correo.trim(),

                                contrasena

                            )
                            // TODO(BACKEND):
                            // Reemplazar navegación provisional.
                        }
                    },

                    /*
                     * TODO(BACKEND):
                     *
                     * Cuando cargando provenga del ViewModel,
                     * este botón se deshabilitará automáticamente
                     * mientras se realiza la solicitud.
                     */
                    enabled = !cargando,

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(70.dp),

                    shape =
                        RoundedCornerShape(15.dp),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                loginBackground,

                            contentColor =
                                loginContent,

                            disabledContainerColor =
                                loginBackground.copy(
                                    alpha = 0.65f
                                ),

                            disabledContentColor =
                                loginContent.copy(
                                    alpha = 0.80f
                                )
                        )
                ) {


                    // ================================================
                    // El modal global indica el estado de carga. No duplicar el spinner aquí.
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
                        .clickable(
                            enabled = !cargando
                        ) {
                            onRegisterClick()
                        },

                    shape =
                        RoundedCornerShape(15.dp),

                    color =
                        registerBackground,

                    shadowElevation =
                        4.dp
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = 18.dp,
                                end = 18.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {


                        Icon(
                            imageVector =
                                Icons.Outlined.PersonOutline,
                            contentDescription =
                                null,
                            modifier =
                                Modifier.size(25.dp),
                            tint =
                                Color(0xFFFF0060)
                        )


                        Spacer(
                            modifier =
                                Modifier.size(12.dp)
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
                            modifier =
                                Modifier.weight(1f),
                            fontSize =
                                20.sp,
                            fontFamily =
                                FontFamily.Serif,
                            fontWeight =
                                FontWeight.Bold,
                            textAlign =
                                TextAlign.Center,
                            color =
                                registerContent
                        )


                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled
                                    .KeyboardArrowRight,

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(25.dp),

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

        contentAlignment =
            Alignment.Center
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

                shape =
                    RoundedCornerShape(50),

                color =
                    Color(0xFFE770A0)
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


        HorizontalDivider(
            modifier =
                Modifier.weight(1f),

            thickness =
                1.dp,

            color =
                Color(0xFFE4007C)
                    .copy(alpha = 0.35f)
        )


        Spacer(
            modifier =
                Modifier.size(10.dp)
        )


        Icon(
            imageVector =
                if (darkTheme) {
                    Icons.Outlined.DarkMode
                } else {
                    Icons.Outlined.LightMode
                },

            contentDescription =
                null,

            modifier =
                Modifier.size(21.dp),

            tint =
                Color(0xFFE4007C)
        )


        Spacer(
            modifier =
                Modifier.size(7.dp)
        )


        Text(
            text =
                if (darkTheme) {
                    "Modo oscuro"
                } else {
                    "Modo claro"
                },

            fontSize =
                12.sp,

            fontFamily =
                FontFamily.Serif,

            color =
                textColor
        )


        Spacer(
            modifier =
                Modifier.size(10.dp)
        )


        HorizontalDivider(
            modifier =
                Modifier.weight(1f),

            thickness =
                1.dp,

            color =
                Color(0xFFE4007C)
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
private fun LoginScreenLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        LoginScreen(
            onLoginClick = { _, _ -> },
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
private fun LoginScreenDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        LoginScreen(
            onLoginClick = { _, _ -> },
            onRegisterClick = {},
            onForgotPasswordClick = {},
            darkTheme = true,
            onThemeChange = {}
        )
    }
}