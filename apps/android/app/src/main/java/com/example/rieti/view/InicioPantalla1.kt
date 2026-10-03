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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme


@Composable
fun InicioPantalla1(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    darkTheme: Boolean,
    onThemeChange: () -> Unit
) {

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


    val registerBackground =
        if (darkTheme) {
            Color(0xFFFFEEF4)
        } else {
            Color(0xFFFFF3F7)
        }


    val loginContent =
        if (darkTheme) {
            Color(0xFFFFFFFF)
        } else {
            Color.White
        }


    val registerContent =
        if (darkTheme) {
            Color(0xFF160A10)
        } else {
            Color(0xFF131026)
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


            // =====================================================
            // CABECERA RIETI
            // =====================================================

            CabeceraRieti(
                darkTheme = darkTheme,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )


            // =====================================================
            // CONTENIDO PRINCIPAL
            // =====================================================

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-30).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                // =================================================
                // ESPACIO PARA LA CABECERA
                // =================================================

                Spacer(
                    modifier = Modifier.height(260.dp)
                )


                // =================================================
                // MENSAJE PRINCIPAL
                // =================================================

                Text(
                    text = buildAnnotatedString {

                        append("Reporta una posible\n")
                        append("situación de\n")

                        withStyle(
                            SpanStyle(
                                color = Color(0xFFE84485),
                                fontWeight = FontWeight.Bold
                            )
                        ) {

                            append("trabajo infantil.")
                        }
                    },

                    fontSize = 27.sp,
                    lineHeight = 29.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    color = colors.text
                )


                // =================================================
                // ESPACIO ANTES DE LA LÍNEA
                // =================================================

                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                // =================================================
                // LÍNEA DEBAJO DEL MENSAJE
                // =================================================

                Surface(
                    modifier = Modifier
                        .size(
                            width = 290.dp,
                            height = 1.dp
                        ),
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFFF0060)
                ) {}


                // =================================================
                // ESPACIO DESPUÉS DE LA LÍNEA
                // =================================================

                Spacer(
                    modifier = Modifier.height(17.dp)
                )


                // =================================================
                // SUBTÍTULO
                // =================================================

                Text(
                    text = "La información que compartas\ncontribuirá a su detención y atención.",
                    fontSize = 15.sp,
                    lineHeight = 17.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    color = colors.text.copy(
                        alpha = 0.78f
                    )
                )


                Spacer(
                    modifier = Modifier.height(40.dp)
                )


                // =================================================
                // INICIAR SESIÓN
                // =================================================

                BotonPrincipalInicio(
                    text = "INICIAR SESIÓN",
                    backgroundColor = loginBackground,
                    contentColor = loginContent,

                    icon = {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Outlined.Login,

                            contentDescription = null,

                            modifier =
                                Modifier.size(27.dp),

                            tint = loginContent
                        )
                    },

                    onClick = onLoginClick
                )


                Spacer(
                    modifier = Modifier.height(18.dp)
                )


                // =================================================
                // CREAR CUENTA
                // =================================================

                BotonPrincipalInicio(
                    text = "CREAR CUENTA",
                    backgroundColor = registerBackground,
                    contentColor = registerContent,

                    icon = {

                        Icon(
                            imageVector =
                                Icons.Outlined.PersonOutline,

                            contentDescription = null,

                            modifier =
                                Modifier.size(27.dp),

                            tint =
                                if (darkTheme) {
                                    Color(0xFFE4007C)
                                } else {
                                    Color(0xFFFF0060)
                                }
                        )
                    },

                    arrowColor =
                        if (darkTheme) {
                            registerContent
                        } else {
                            Color(0xFFFF0060)
                        },

                    dividerColor =
                        Color(0xFFFF0060),

                    onClick =
                        onRegisterClick
                )


                // =================================================
                // ESPACIO FLEXIBLE
                // =================================================

                Spacer(
                    modifier = Modifier.weight(1f)
                )


                // =================================================
                // CAMBIO DE TEMA
                // =================================================

                SelectorTema(
                    darkTheme = darkTheme,
                    onThemeChange = onThemeChange,
                    textColor = colors.text
                )


                Spacer(
                    modifier = Modifier.height(72.dp)
                )
            }
        }
    }
}


// =====================================================================
// CABECERA RIETI
// =====================================================================

@Composable
private fun CabeceraRieti(
    darkTheme: Boolean,
    modifier: Modifier = Modifier
) {

    val coloresCabecera =
        if (darkTheme) {

            // MODO OSCURO
            // Rosa intenso -> vino/frambuesa profundo
            listOf(
                Color(0xFFFF0060),
                Color(0xFFD71964),
                Color(0xFF85113F)
            )

        } else {

            // MODO CLARO
            // Rosa intenso -> rosa suave -> casi blanco
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


            // =====================================================
            // RIETI
            // =====================================================

            Text(
                text = "RIETI",
                fontSize = 55.sp,
                fontFamily = FontFamily.Serif,
                color = Color.White
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // =====================================================
            // LÍNEA DEBAJO DE RIETI
            // =====================================================

            Surface(
                modifier = Modifier
                    .size(
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
// BOTÓN PRINCIPAL
// =====================================================================

@Composable
private fun BotonPrincipalInicio(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    arrowColor: Color = contentColor,
    dividerColor: Color = contentColor,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(90.dp)
            .clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(15.dp),

        color =
            backgroundColor,

        shadowElevation =
            9.dp
    ) {


        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 17.dp,
                    end = 12.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            // =====================================================
            // ICONO
            // =====================================================

            Box(
                modifier =
                    Modifier.size(30.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                icon()
            }


            // =====================================================
            // DIVISOR
            // =====================================================

            Box(
                modifier = Modifier
                    .padding(
                        start = 11.dp,
                        end = 13.dp
                    )
                    .size(
                        width = 1.dp,
                        height = 34.dp
                    )
                    .background(
                        dividerColor.copy(
                            alpha = 0.35f
                        )
                    )
            )


            // =====================================================
            // TEXTO
            // =====================================================

            Text(
                text = text,

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
                    contentColor
            )


            // =====================================================
            // FLECHA
            // =====================================================

            Icon(
                imageVector =
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,

                contentDescription =
                    null,

                modifier =
                    Modifier.size(25.dp),

                tint =
                    arrowColor
            )
        }
    }
}


// =====================================================================
// SELECTOR DE TEMA
// =====================================================================

@Composable
private fun SelectorTema(
    darkTheme: Boolean,
    onThemeChange: () -> Unit,
    textColor: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 45.dp
            )
            .clickable {
                onThemeChange()
            },

        verticalAlignment =
            Alignment.CenterVertically,

        horizontalArrangement =
            Arrangement.Center
    ) {


        // =========================================================
        // LÍNEA IZQUIERDA
        // =========================================================

        HorizontalDivider(
            modifier =
                Modifier.weight(1f),

            thickness =
                1.dp,

            color =
                Color(0xFFE4007C)
                    .copy(
                        alpha = 0.35f
                    )
        )


        Spacer(
            modifier =
                Modifier.size(10.dp)
        )


        // =========================================================
        // ICONO
        // =========================================================

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


        // =========================================================
        // TEXTO
        // =========================================================

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


        // =========================================================
        // LÍNEA DERECHA
        // =========================================================

        HorizontalDivider(
            modifier =
                Modifier.weight(1f),

            thickness =
                1.dp,

            color =
                Color(0xFFE4007C)
                    .copy(
                        alpha = 0.35f
                    )
        )
    }
}


// =====================================================================
// PREVIEW LIGHT
// =====================================================================

@Preview(
    name = "RIETI - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun InicioPantalla1LightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        InicioPantalla1(
            onLoginClick = {},
            onRegisterClick = {},
            darkTheme = false,
            onThemeChange = {}
        )
    }
}


// =====================================================================
// PREVIEW DARK
// =====================================================================

@Preview(
    name = "RIETI - Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode =
        Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun InicioPantalla1DarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        InicioPantalla1(
            onLoginClick = {},
            onRegisterClick = {},
            darkTheme = true,
            onThemeChange = {}
        )
    }
}