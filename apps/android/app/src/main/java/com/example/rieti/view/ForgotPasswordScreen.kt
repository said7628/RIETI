
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
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


/**
 * Presenta la pantalla de recuperación de contraseña de RIETI.
 *
 * Permite ingresar el correo electrónico asociado a una cuenta
 * para solicitar el envío de un código de recuperación.
 *
 * Implementa una validación local que comprueba que el correo
 * no esté vacío y tenga un formato válido.
 *
 * Utiliza AuthRepository para realizar la solicitud de recuperación
 * y SessionManager para conservar el correo cuando la solicitud
 * se completa correctamente.
 *
 * Gestiona un estado de carga durante la solicitud y comunica
 * los resultados mediante RietiFeedback.
 *
 * También proporciona opciones para iniciar sesión,
 * crear una cuenta y cambiar el tema visual de la aplicación.
 *
 * @param onLoginClick Función ejecutada cuando el usuario
 * selecciona la opción de iniciar sesión.
 * @param onRegisterClick Función ejecutada cuando el usuario
 * selecciona la opción de crear una cuenta.
 * @param onSendCodeClick Función ejecutada después de recibir
 * un resultado exitoso de la solicitud de recuperación.
 * @param darkTheme Indica si se encuentra habilitado
 * el modo oscuro de la aplicación.
 * @param onThemeChange Función ejecutada cuando el usuario
 * selecciona el control para cambiar el tema visual.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
@Composable
fun ForgotPasswordScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onSendCodeClick: () -> Unit,
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {

    // ---------------------------------------------------------
    // ESTADO LOCAL
    // ---------------------------------------------------------

    var correo by remember {
        mutableStateOf("")
    }

    var errorCorreo by remember {
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
        if (cargando) RietiFeedback.loading = "Enviando codigo..."
        onDispose { RietiFeedback.loading = null }
    }


    // ---------------------------------------------------------
    // COLORES SEGÚN EL TEMA
    // ---------------------------------------------------------

    val unfocusedFieldColor =
        if (darkTheme) {
            Color.White
        } else {
            Color(0xFF16BFAF)
        }

    val focusedFieldColor = Color(0xFFFF0060)

    val errorColor = Color(0xFFD32F2F)

    val primaryButtonColor =
        if (darkTheme) {
            Color(0xFF24A194)
        } else {
            Color(0xFF16BFAF)
        }

    val secondaryButtonBackground =
        if (darkTheme) {
            Color(0xFFFFEEF4)
        } else {
            Color(0xFFFFF3F7)
        }

    val secondaryButtonContent =
        if (darkTheme) {
            Color(0xFF160A10)
        } else {
            Color(0xFF131026)
        }


    // ---------------------------------------------------------
    // VALIDACIÓN LOCAL
    // ---------------------------------------------------------

    /**
     * Valida el correo electrónico ingresado en el formulario.
     *
     * Elimina los espacios iniciales y finales del valor
     * únicamente para comprobar su contenido y formato.
     *
     * Verifica que el correo no esté vacío y utiliza
     * Patterns.EMAIL_ADDRESS para comprobar su estructura.
     *
     * Cuando la validación falla, actualiza errorCorreo
     * con el mensaje correspondiente.
     *
     * @return true cuando el correo contiene un valor
     * y cumple con el formato esperado; false en caso contrario.
     */
    fun validarCorreo(): Boolean {

        errorCorreo = null

        val correoLimpio = correo.trim()

        return when {

            correoLimpio.isBlank() -> {

                errorCorreo =
                    "El correo electrónico es obligatorio."

                false
            }

            !Patterns.EMAIL_ADDRESS
                .matcher(correoLimpio)
                .matches() -> {

                errorCorreo =
                    "Ingresa un correo electrónico válido."

                false
            }

            else -> true
        }
    }


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
                modifier = Modifier.fillMaxSize()
            ) {


                // =====================================================
                // CABECERA
                // =====================================================

                ForgotPasswordHeader(
                    darkTheme = darkTheme
                )


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                // =====================================================
                // TÍTULO
                // =====================================================

                Text(
                    text = "Recuperar Contraseña",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    color = colors.titles
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
                    modifier = Modifier.height(24.dp)
                )


                // =====================================================
                // TEXTO DE APOYO
                // =====================================================

                Text(
                    text = "Ingresa el correo electrónico asociado a tu cuenta para recibir un código de recuperación.",
                    modifier = Modifier.padding(
                        horizontal = 32.dp
                    ),
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    color = colors.text
                )


                Spacer(
                    modifier = Modifier.height(24.dp)
                )


                // =====================================================
                // CORREO
                // =====================================================

                Text(
                    text = "Correo",
                    modifier = Modifier.padding(
                        horizontal = 32.dp
                    ),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    color = colors.text
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                OutlinedTextField(
                    value = correo,

                    onValueChange = {

                        correo = it

                        // Al volver a escribir quitamos el error anterior.
                        errorCorreo = null
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(58.dp),

                    singleLine = true,

                    isError = errorCorreo != null,

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),

                    shape = RoundedCornerShape(14.dp),

                    colors = OutlinedTextFieldDefaults.colors(

                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text,

                        cursorColor = focusedFieldColor,

                        focusedBorderColor = focusedFieldColor,
                        unfocusedBorderColor = unfocusedFieldColor,

                        errorBorderColor = focusedFieldColor,
                        errorCursorColor = focusedFieldColor,

                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    )
                )


                // =====================================================
                // ESPACIO FIJO PARA EL ERROR
                //
                // Siempre ocupa 16.dp, haya o no haya error.
                // De esta forma el botón NO se mueve.
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


                Spacer(
                    modifier = Modifier.height(24.dp)
                )


                // =====================================================
                // ENVIAR CÓDIGO
                // =====================================================

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(70.dp)
                        .clickable(enabled = !cargando) {

                            if (validarCorreo()) {

                                /*
                                 * TODO(BACKEND):
                                 *
                                 * Actualmente solo se valida localmente
                                 * el formato del correo.
                                 *
                                 * Cuando se conecte el backend:
                                 *
                                 * 1. Enviar el correo al ViewModel.
                                 *
                                 * 2. El ViewModel deberá solicitar al
                                 *    endpoint correspondiente el envío
                                 *    del código de recuperación.
                                 *
                                 * 3. Mostrar estado de carga mientras
                                 *    se procesa la solicitud.
                                 *
                                 * 4. Si el backend indica que el correo
                                 *    no existe o devuelve algún error,
                                 *    mostrar el mensaje correspondiente.
                                 *
                                 * 5. SOLO cuando el backend confirme
                                 *    que la solicitud fue exitosa,
                                 *    continuar hacia ResetPasswordScreen.
                                 *
                                 * TEMPORAL:
                                 * Mientras no exista la integración,
                                 * navegamos directamente para poder
                                 * probar el flujo visual.
                                 */

                                scope.launch {
                                    cargando = true

                                    when (
                                        val result = authRepository.requestRecovery(
                                            correo.trim()
                                        )
                                    ) {
                                        is AuthResult.Success -> {
                                            sessionManager.saveRecoveryEmail(
                                                correo.trim()
                                            )

                                            RietiFeedback.show(result.data)

                                            onSendCodeClick()
                                        }

                                        is AuthResult.Error -> {
                                            RietiFeedback.show(result.message, error = true)
                                        }
                                    }

                                    cargando = false
                                }
                            }
                        },
                    shape = RoundedCornerShape(15.dp),
                    color = primaryButtonColor
                ) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "ENVIAR CÓDIGO",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                // =====================================================
                // INICIAR SESIÓN
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
                    color = secondaryButtonBackground
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "INICIAR SESIÓN",
                            color = secondaryButtonContent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )

                        Text(
                            text = "›",
                            color = Color(0xFFFF0060),
                            fontSize = 30.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                // =====================================================
                // CREAR CUENTA
                // =====================================================

                Text(
                    text = "¿Aún no tienes una cuenta? Crear cuenta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onRegisterClick()
                        }
                        .padding(
                            horizontal = 32.dp,
                            vertical = 8.dp
                        ),
                    color = Color(0xFFFF0060),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )


                // Empuja el selector de tema hasta la parte inferior.
                Spacer(
                    modifier = Modifier.weight(1f)
                )


                // =====================================================
                // SELECTOR DE TEMA
                // =====================================================

                HorizontalDivider(
                    thickness = 2.dp,
                    color = Color(0xFFFF0060)
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
                        .padding(vertical = 18.dp),

                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    color = colors.text,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


// =============================================================
// CABECERA RIETI
// =============================================================

/**
 * Muestra la cabecera visual de la pantalla de
 * recuperación de contraseña.
 *
 * Presenta el nombre RIETI en tipografía Serif,
 * acompañado de una línea decorativa.
 *
 * El fondo utiliza un degradado vertical cuyos
 * colores dependen del tema visual seleccionado.
 *
 * @param darkTheme Indica si debe utilizarse
 * la variante de colores correspondiente al modo oscuro.
 */
@Composable
private fun ForgotPasswordHeader(
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
                brush = Brush.verticalGradient(
                    colors = gradientColors
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "RIETI",
                color = Color.White,
                fontFamily = FontFamily.Serif,
                fontSize = 55.sp
            )


            Spacer(
                modifier = Modifier.height(14.dp)
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


// =============================================================
// PREVIEW LIGHT
// =============================================================

/**
 * Genera una vista previa de ForgotPasswordScreen
 * utilizando el tema claro de RIETI.
 *
 * Permite visualizar la cabecera, el formulario
 * de recuperación y los controles de navegación
 * desde Android Studio.
 *
 * Emplea callbacks vacíos para representar
 * las acciones de la pantalla durante la previsualización.
 */
@Preview(
    name = "Forgot Password - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ForgotPasswordScreenLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        ForgotPasswordScreen(
            onLoginClick = {},
            onRegisterClick = {},
            onSendCodeClick = {},
            darkTheme = false,
            onThemeChange = {}
        )
    }
}


// =============================================================
// PREVIEW DARK
// =============================================================

/**
 * Genera una vista previa de ForgotPasswordScreen
 * utilizando el tema oscuro de RIETI.
 *
 * Configura RIETITheme con darkTheme en true
 * y aplica Configuration.UI_MODE_NIGHT_YES
 * para representar la interfaz en modo nocturno.
 *
 * Las acciones de navegación, recuperación
 * y cambio de tema utilizan callbacks vacíos.
 */
@Preview(
    name = "Forgot Password - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ForgotPasswordScreenDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        ForgotPasswordScreen(
            onLoginClick = {},
            onRegisterClick = {},
            onSendCodeClick = {},
            darkTheme = true,
            onThemeChange = {}
        )
    }
}
