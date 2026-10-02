package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme

/**
 * Pantalla inicial de la aplicación RIETI.
 *
 * Presenta las opciones principales para iniciar sesión,
 * crear una cuenta y cambiar entre el modo claro y oscuro.
 *
 * @param onLoginClick Acción que se ejecuta al seleccionar
 * el botón "INICIAR SESIÓN".
 *
 * @param onRegisterClick Acción que se ejecuta al seleccionar
 * el botón "CREAR CUENTA".
 *
 * @param darkThemeInicial Determina el tema inicial de la pantalla.
 * false = modo claro.
 * true = modo oscuro.
 */
@Composable
fun InicioPantalla1(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    darkThemeInicial: Boolean = false
) {

    // =========================================================
    // ESTADO LIGHT / DARK
    // false = Light Mode
    // true  = Dark Mode
    // =========================================================

    var darkTheme by remember {
        mutableStateOf(darkThemeInicial)
    }

    // =========================================================
    // TEMA DE RIETI
    // =========================================================

    RIETITheme(
        darkTheme = darkTheme
    ) {

        val colors = AppTheme.colors

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->

            // =================================================
            // SUPERFICIE PRINCIPAL
            // =================================================

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = colors.surface
            ) {

                /*
                 * Este Box permite colocar los círculos
                 * decorativos detrás del contenido.
                 */
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                    // =================================================
                    // CÍRCULOS DECORATIVOS - DERECHA
                    // Detrás del botón INICIAR SESIÓN.
                    // =================================================

                    CirculosDecorativos(
                        modifier = Modifier
                            .size(165.dp)
                            .align(Alignment.CenterEnd)
                            .offset(
                                x = 90.dp,
                                y = (40).dp
                            )
                    )

                    // =================================================
                    // CÍRCULOS DECORATIVOS - IZQUIERDA
                    // Detrás del botón CREAR CUENTA.
                    // =================================================

                    CirculosDecorativos(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.CenterStart)
                            .offset(
                                x = (-90).dp,
                                y = 200.dp
                            )
                    )

                    // =================================================
                    // CONTENIDO PRINCIPAL
                    // =================================================

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // =================================================
                        // CABECERA
                        // Degradado vertical del color principal.
                        // =================================================

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(182.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            colors.titles,
                                            colors.titles.copy(alpha = 0.50f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            // =============================================
                            // TÍTULO RIETI
                            // =============================================

                            val titulo = "RIETI"

                            /*
                             * Desplazamiento vertical de cada letra
                             * para generar la curvatura.
                             */
                            val desplazamientos = listOf(
                                8.dp,
                                3.dp,
                                0.dp,
                                3.dp,
                                8.dp
                            )

                            /*
                             * Rotación individual de cada letra
                             * para acompañar el arco.
                             */
                            val rotaciones = listOf(
                                -10f,
                                -5f,
                                0f,
                                5f,
                                10f
                            )

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                titulo.forEachIndexed { index, letra ->

                                    Text(
                                        text = letra.toString(),
                                        fontSize = 50.sp,
                                        fontFamily = FontFamily.Serif,
                                        color = colors.text,
                                        modifier = Modifier
                                            .padding(horizontal = 1.dp)
                                            .offset(
                                                y = desplazamientos[index]
                                            )
                                            .graphicsLayer {
                                                rotationZ = rotaciones[index]
                                            }
                                    )
                                }
                            }
                        }

                        // =================================================
                        // ESPACIO ENTRE CABECERA Y MENSAJE
                        // =================================================

                        Spacer(
                            modifier = Modifier.height(25.dp)
                        )

                        // =================================================
                        // MENSAJE PRINCIPAL
                        // =================================================

                        Text(
                            text = "Reporta cualquier forma\nde trabajo infantil",
                            fontSize = 30.sp,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            color = colors.text
                        )

                        Spacer(
                            modifier = Modifier.height(25.dp)
                        )

                        // =================================================
                        // DIVISOR
                        // =================================================

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            thickness = 2.dp,
                            color = colors.pinkPrincipal
                        )

                        // =================================================
                        // MENSAJE SECUNDARIO
                        // =================================================

                        Text(
                            text = "Empieza ahora, \n tu reporte hace el cambio",
                            modifier = Modifier.padding(
                                vertical = 27.dp
                            ),
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            color = colors.text
                        )

                        Spacer(
                            modifier = Modifier.height(35.dp)
                        )

                        // =================================================
                        // BOTÓN INICIAR SESIÓN
                        // =================================================

                        Button(
                            onClick = onLoginClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .height(80.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.blueBoxes,
                                contentColor = colors.textTyping
                            )
                        ) {

                            Text(
                                text = "INICIAR SESIÓN",
                                fontSize = 30.sp,
                                fontFamily = FontFamily.Serif
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(50.dp)
                        )

                        // =================================================
                        // BOTÓN CREAR CUENTA
                        // =================================================

                        Button(
                            onClick = onRegisterClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .height(80.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.blueDetails,
                                contentColor = colors.textTyping
                            )
                        ) {

                            Text(
                                text = "CREAR CUENTA",
                                fontSize = 30.sp,
                                fontFamily = FontFamily.Serif
                            )
                        }

                        /*
                         * Ocupa el espacio restante para mantener
                         * el selector de tema en la parte inferior.
                         */
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )

                        // =================================================
                        // DIVISOR INFERIOR
                        // =================================================

                        HorizontalDivider(
                            thickness = 4.dp,
                            color = colors.pinkPrincipal
                        )

                        // =================================================
                        // SELECTOR LIGHT / DARK
                        // =================================================

                        Text(
                            text = if (darkTheme) {
                                "Modo claro"
                            } else {
                                "Modo oscuro"
                            },
                            modifier = Modifier
                                .clickable {
                                    darkTheme = !darkTheme
                                }
                                .padding(vertical = 18.dp),
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Serif,
                            color = colors.text
                        )
                    }
                }
            }
        }
    }
}


// =============================================================
// CÍRCULOS DECORATIVOS
// =============================================================

@Composable
fun CirculosDecorativos(
    modifier: Modifier = Modifier
){

    Canvas(
        modifier = modifier
    ) {

        val centro = Offset(
            x = size.width / 2,
            y = size.height / 2
        )

        // Círculo exterior
        drawCircle(
            color = Color(0xFFFF1493),
            radius = 72.dp.toPx(),
            center = centro,
            style = Stroke(
                width = 4.dp.toPx()
            )
        )

        // Círculo intermedio
        drawCircle(
            color = Color(0xFFE91E83),
            radius = 57.dp.toPx(),
            center = centro,
            style = Stroke(
                width = 4.dp.toPx()
            )
        )

        // Círculo interior
        drawCircle(
            color = Color(0xFFB5165B),
            radius = 42.dp.toPx(),
            center = centro,
            style = Stroke(
                width = 4.dp.toPx()
            )
        )
    }
}


// =============================================================
// PREVIEW - LIGHT MODE
// =============================================================

@Preview(
    name = "RIETI - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun InicioPantalla1LightPreview() {

    InicioPantalla1(
        onLoginClick = {},
        onRegisterClick = {},
        darkThemeInicial = false
    )
}


// =============================================================
// PREVIEW - DARK MODE
// =============================================================

@Preview(
    name = "RIETI - Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun InicioPantalla1DarkPreview() {

    InicioPantalla1(
        onLoginClick = {},
        onRegisterClick = {},
        darkThemeInicial = true
    )
}