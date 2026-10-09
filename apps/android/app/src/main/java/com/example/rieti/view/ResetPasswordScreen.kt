package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.rieti.data.repository.AuthRepository
import com.example.rieti.data.repository.AuthResult
import com.example.rieti.data.session.SessionManager
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme
import kotlinx.coroutines.launch


// =====================================================================
// RESTABLECER CONTRASEÑA
// =====================================================================

@Composable
fun ResetPasswordScreen(
    onResetPasswordClick: (
        recoveryCode: String,
        newPassword: String,
        confirmPassword: String
    ) -> Unit,

    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {

    // =============================================================
    // ESTADOS
    // =============================================================

    var recoveryCode by remember {
        mutableStateOf("")
    }

    var newPassword by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var errorCode by remember {
        mutableStateOf<String?>(null)
    }

    var errorNewPassword by remember {
        mutableStateOf<String?>(null)
    }

    var errorConfirmPassword by remember {
        mutableStateOf<String?>(null)
    }


    val colors = AppTheme.colors

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authRepository = remember {
        AuthRepository()
    }

    val sessionManager = remember {
        SessionManager(context.applicationContext)
    }

    var cargando by remember {
        mutableStateOf(false)
    }

    DisposableEffect(cargando) {
        if (cargando) RietiFeedback.loading = "Actualizando contraseña..."
        onDispose { RietiFeedback.loading = null }
    }


    // =============================================================
    // COLORES
    // =============================================================

    val primaryButtonColor =
        if (darkTheme) {
            Color(0xFF24A194)
        } else {
            Color(0xFF16BFAF)
        }


    // =============================================================
    // VALIDACIÓN LOCAL
    // =============================================================

    fun validarFormulario(): Boolean {

        errorCode = null
        errorNewPassword = null
        errorConfirmPassword = null

        var formularioValido = true


        // ---------------------------------------------------------
        // CÓDIGO
        // ---------------------------------------------------------

        if (recoveryCode.isBlank()) {

            errorCode =
                "Ingresa el código de recuperación."

            formularioValido = false
        }


        // ---------------------------------------------------------
        // NUEVA CONTRASEÑA
        // ---------------------------------------------------------

        if (newPassword.isBlank()) {

            errorNewPassword =
                "Ingresa una nueva contraseña."

            formularioValido = false

        } else if (newPassword.length < 10) {

            errorNewPassword =
                "La contraseña debe tener al menos 10 caracteres."

            formularioValido = false
        }


        // ---------------------------------------------------------
        // CONFIRMAR CONTRASEÑA
        // ---------------------------------------------------------

        if (confirmPassword.isBlank()) {

            errorConfirmPassword =
                "Confirma tu nueva contraseña."

            formularioValido = false

        } else if (
            newPassword.isNotBlank() &&
            newPassword != confirmPassword
        ) {

            errorConfirmPassword =
                "Las contraseñas no coinciden."

            formularioValido = false
        }


        return formularioValido
    }


    // =============================================================
    // PANTALLA
    // =============================================================

    Scaffold(
        contentWindowInsets =
            WindowInsets.safeDrawing
    ) { innerPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),

            color = colors.surface
        ) {

            Column(
                modifier =
                    Modifier.fillMaxSize()
            ) {

                // =================================================
                // CABECERA
                // =================================================

                ResetPasswordHeader(
                    darkTheme = darkTheme
                )


                // =================================================
                // CONTENIDO
                // =================================================

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    // =============================================
                    // TÍTULO
                    // =============================================

                    Text(
                        text =
                            "Restablecer Contraseña",

                        modifier =
                            Modifier.fillMaxWidth(),

                        fontSize = 30.sp,

                        fontFamily =
                            FontFamily.Serif,

                        textAlign =
                            TextAlign.Center,

                        color = colors.titles
                    )


                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )


                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 80.dp
                            ),

                        thickness = 2.dp,

                        color =
                            Color(0xFFFF0060)
                    )


                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    // =============================================
                    // TEXTO DE APOYO
                    // =============================================

                    Text(
                        text =
                            "Ingresa el código que recibiste " +
                                    "y establece una nueva contraseña.",

                        modifier =
                            Modifier.padding(
                                horizontal = 32.dp
                            ),

                        fontSize = 13.sp,

                        lineHeight = 16.sp,

                        fontFamily =
                            FontFamily.Serif,

                        textAlign =
                            TextAlign.Center,

                        color = colors.text
                    )


                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    // =============================================
                    // CÓDIGO DE RECUPERACIÓN
                    // =============================================

                    ResetPasswordField(
                        label =
                            "Código de recuperación",

                        value =
                            recoveryCode,

                        onValueChange = {

                            recoveryCode = it
                            errorCode = null
                        },

                        error =
                            errorCode,

                        darkTheme =
                            darkTheme,

                        keyboardType =
                            KeyboardType.Number
                    )


                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )


                    // =============================================
                    // NUEVA CONTRASEÑA
                    // =============================================

                    ResetPasswordField(
                        label =
                            "Nueva contraseña",

                        value =
                            newPassword,

                        onValueChange = {

                            newPassword = it

                            errorNewPassword = null


                            if (
                                errorConfirmPassword ==
                                "Las contraseñas no coinciden."
                            ) {

                                errorConfirmPassword = null
                            }
                        },

                        error =
                            errorNewPassword,

                        darkTheme =
                            darkTheme,

                        isPassword = true
                    )


                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )


                    // =============================================
                    // CONFIRMAR CONTRASEÑA
                    // =============================================

                    ResetPasswordField(
                        label =
                            "Confirmar contraseña",

                        value =
                            confirmPassword,

                        onValueChange = {

                            confirmPassword = it

                            errorConfirmPassword = null
                        },

                        error =
                            errorConfirmPassword,

                        darkTheme =
                            darkTheme,

                        isPassword = true
                    )


                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    // =============================================
                    // RESTABLECER CONTRASEÑA
                    // =============================================

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 32.dp
                            )
                            .height(62.dp)
                            .clickable(enabled = !cargando) {

                                if (
                                    validarFormulario()
                                ) {

                                    /*
                                     * TODO(BACKEND):
                                     *
                                     * REEMPLAZAR esta ejecución
                                     * temporal por la integración
                                     * con ViewModel/API.
                                     *
                                     * Datos disponibles:
                                     *
                                     * recoveryCode
                                     * newPassword
                                     * confirmPassword
                                     *
                                     *
                                     * El backend deberá:
                                     *
                                     * 1. Validar que el código de
                                     *    recuperación sea correcto
                                     *    y siga vigente.
                                     *
                                     * 2. Validar la nueva contraseña.
                                     *
                                     * 3. Actualizar la contraseña.
                                     *
                                     * 4. Invalidar cualquier sesión
                                     *    activa asociada al usuario.
                                     *
                                     * 5. Informar si la operación
                                     *    fue exitosa.
                                     *
                                     *
                                     * Si el código es inválido
                                     * o expiró:
                                     *
                                     * errorCode =
                                     *     mensaje del backend
                                     *
                                     *
                                     * Si existe un problema con
                                     * la contraseña:
                                     *
                                     * errorNewPassword =
                                     *     mensaje correspondiente
                                     *
                                     *
                                     * IMPORTANTE:
                                     *
                                     * Utilizar los estados de error
                                     * existentes.
                                     *
                                     * NO agregar Text nuevos debajo
                                     * de los campos.
                                     *
                                     *
                                     * SOLO si el backend confirma
                                     * que el restablecimiento fue
                                     * exitoso ejecutar:
                                     *
                                     * onResetPasswordClick(
                                     *     recoveryCode,
                                     *     newPassword,
                                     *     confirmPassword
                                     * )
                                     *
                                     * AppNavigation será responsable
                                     * de limpiar la navegación y
                                     * regresar a WelcomeScreen.
                                     *
                                     *
                                     * TEMPORAL:
                                     *
                                     * Actualmente se ejecuta
                                     * directamente para probar
                                     * el flujo sin backend.
                                     */

                                    val recoveryEmail =
                                        sessionManager.recoveryEmail()

                                    if (recoveryEmail.isNullOrBlank()) {
                                        RietiFeedback.show("Vuelve a solicitar un código de recuperación.", error = true)
                                    } else {
                                        scope.launch {
                                            cargando = true

                                            when (
                                                val result = authRepository.resetPassword(
                                                    email = recoveryEmail,
                                                    code = recoveryCode,
                                                    password = newPassword
                                                )
                                            ) {
                                                is AuthResult.Success -> {
                                                    sessionManager.clear()

                                                    RietiFeedback.show("Contraseña restablecida correctamente.")

                                                    onResetPasswordClick(
                                                        recoveryCode,
                                                        newPassword,
                                                        confirmPassword
                                                    )
                                                }

                                                is AuthResult.Error -> {
                                                    when (result.code) {
                                                        "INVALID_RECOVERY_CODE" -> {
                                                            errorCode = result.message
                                                        }

                                                        "WEAK_PASSWORD" -> {
                                                            errorNewPassword = result.message
                                                        }

                                                        else -> {
                                                            RietiFeedback.show(result.message, error = true)
                                                        }
                                                    }
                                                }
                                            }

                                            cargando = false
                                        }
                                    }
                                }
                            },

                        shape =
                            RoundedCornerShape(15.dp),

                        color =
                            primaryButtonColor
                    ) {

                        Box(
                            modifier =
                                Modifier.fillMaxSize(),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text =
                                    "RESTABLECER CONTRASEÑA",

                                color =
                                    Color.White,

                                fontSize =
                                    17.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                fontFamily =
                                    FontFamily.Serif,

                                textAlign =
                                    TextAlign.Center
                            )
                        }
                    }
                }


                // =================================================
                // SELECTOR DE TEMA
                // =================================================

                HorizontalDivider(
                    thickness = 2.dp,

                    color =
                        Color(0xFFFF0060)
                )


                Text(
                    text =
                        if (darkTheme) {
                            "Modo oscuro"
                        } else {
                            "Modo claro"
                        },

                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onThemeChange()
                        }
                        .padding(
                            vertical = 14.dp
                        ),

                    fontSize = 18.sp,

                    fontFamily =
                        FontFamily.Serif,

                    color = colors.text,

                    textAlign =
                        TextAlign.Center
                )
            }
        }
    }
}


// =====================================================================
// CAMPO REUTILIZABLE
// =====================================================================

@Composable
private fun ResetPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    darkTheme: Boolean,
    isPassword: Boolean = false,
    keyboardType: KeyboardType =
        KeyboardType.Text
) {

    val colors =
        AppTheme.colors


    // =========================================================
    // VISIBILIDAD DE LA CONTRASEÑA
    // =========================================================
    //
    // Cada llamada a ResetPasswordField mantiene su propio
    // estado.
    //
    // Por eso "Nueva contraseña" y "Confirmar contraseña"
    // pueden mostrarse u ocultarse de manera independiente.
    //
    // Este estado es únicamente de interfaz y NO requiere
    // conexión con el backend.
    // =========================================================

    var passwordVisible by remember {
        mutableStateOf(false)
    }


    val unfocusedFieldColor =
        if (darkTheme) {
            Color.White
        } else {
            Color(0xFF16BFAF)
        }


    val focusedFieldColor =
        Color(0xFFFF0060)


    val errorColor =
        Color(0xFFD32F2F)


    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Text(
            text = label,

            modifier =
                Modifier.padding(
                    horizontal = 32.dp
                ),

            color =
                colors.text,

            fontSize =
                14.sp,

            fontFamily =
                FontFamily.Serif
        )


        Spacer(
            modifier =
                Modifier.height(4.dp)
        )


        OutlinedTextField(
            value = value,

            onValueChange =
                onValueChange,

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 32.dp
                )
                .height(54.dp),

            singleLine = true,

            isError =
                error != null,


            // =====================================================
            // MOSTRAR / OCULTAR CONTRASEÑA
            // =====================================================

            visualTransformation =
                if (
                    isPassword &&
                    !passwordVisible
                ) {

                    PasswordVisualTransformation()

                } else {

                    VisualTransformation.None
                },


            // =====================================================
            // OJITO
            //
            // Solamente aparece cuando el campo es una contraseña.
            // El código de recuperación NO mostrará este icono.
            // =====================================================

            trailingIcon =
                if (isPassword) {

                    {
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
                                        "Contraseña visible"
                                    } else {
                                        "Contraseña oculta"
                                    },

                                tint =
                                    if (error != null) {
                                        errorColor
                                    } else {
                                        colors.text
                                    }
                            )
                        }
                    }

                } else {

                    null
                },


            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        if (isPassword) {
                            KeyboardType.Password
                        } else {
                            keyboardType
                        }
                ),

            shape =
                RoundedCornerShape(14.dp),

            colors =
                OutlinedTextFieldDefaults.colors(

                    focusedTextColor =
                        colors.text,

                    unfocusedTextColor =
                        colors.text,

                    cursorColor =
                        focusedFieldColor,

                    focusedBorderColor =
                        focusedFieldColor,

                    unfocusedBorderColor =
                        unfocusedFieldColor,

                    errorBorderColor =
                        focusedFieldColor,

                    errorCursorColor =
                        focusedFieldColor,

                    focusedContainerColor =
                        Color.Transparent,

                    unfocusedContainerColor =
                        Color.Transparent,

                    errorContainerColor =
                        Color.Transparent
                )
        )


        // =========================================================
        // ESPACIO FIJO PARA ERROR
        //
        // Siempre ocupa el mismo espacio para evitar que los
        // componentes inferiores cambien de posición.
        // =========================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .padding(
                    horizontal = 32.dp
                ),

            contentAlignment =
                Alignment.CenterStart
        ) {

            if (
                error != null
            ) {

                Text(
                    text = error,

                    color =
                        errorColor,

                    fontSize =
                        10.sp,

                    fontFamily =
                        FontFamily.Serif,

                    maxLines = 1
                )
            }
        }
    }
}


// =====================================================================
// CABECERA
// =====================================================================

@Composable
private fun ResetPasswordHeader(
    darkTheme: Boolean
) {

    val gradientColors =
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
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                brush =
                    Brush.verticalGradient(
                        colors =
                            gradientColors
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

                color =
                    Color.White,

                fontFamily =
                    FontFamily.Serif,

                fontSize =
                    55.sp
            )


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            Surface(
                modifier = Modifier
                    .width(90.dp)
                    .height(3.dp),
                shape = RoundedCornerShape(50),
                color = Color(0xFFE770A0)
            ) {}
        }
    }
}


// =====================================================================
// PREVIEW LIGHT
// =====================================================================

@Preview(
    name = "Reset Password - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ResetPasswordScreenLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        ResetPasswordScreen(

            onResetPasswordClick = {
                    _,
                    _,
                    _ ->
            },

            darkTheme = false,

            onThemeChange = {}
        )
    }
}


// =====================================================================
// PREVIEW DARK
// =====================================================================

@Preview(
    name = "Reset Password - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode =
        Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ResetPasswordScreenDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        ResetPasswordScreen(

            onResetPasswordClick = {
                    _,
                    _,
                    _ ->
            },

            darkTheme = true,

            onThemeChange = {}
        )
    }
}