
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.TopBar


// =====================================================================
// CUENTA BORRADA
// =====================================================================

/**
 * Presenta la pantalla de confirmación de eliminación de cuenta
 * de la aplicación RIETI.
 *
 * Muestra un indicador visual de confirmación, un mensaje
 * informativo sobre la eliminación de la cuenta y un botón
 * que permite regresar a la pantalla de inicio.
 *
 * Utiliza AppTheme para adaptar los colores de la interfaz
 * al modo claro u oscuro seleccionado por el usuario.
 *
 * Integra el componente TopBar para mantener la identidad
 * visual de la aplicación.
 *
 * La pantalla no ejecuta directamente la eliminación
 * de la cuenta, sino que presenta la confirmación
 * correspondiente a dicho proceso.
 *
 * La navegación de regreso al inicio se delega
 * a la función recibida como parámetro.
 *
 * @param onBackToStartClick Función que se ejecuta
 * cuando el usuario selecciona "Volver al inicio".
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
@Composable
fun AccountDeletedScreen(
    onBackToStartClick: () -> Unit
) {

    val colors = AppTheme.colors
    val isDarkTheme = AppTheme.isDarkTheme


    // =================================================================
    // COLORES
    // =================================================================

    val navy =
        if (isDarkTheme) {
            colors.text
        } else {
            Color(0xFF123A68)
        }


    val background =
        if (isDarkTheme) {
            colors.surface
        } else {
            Color(0xFFFCFBF9)
        }


    val secondaryText =
        if (isDarkTheme) {
            colors.text.copy(alpha = 0.72f)
        } else {
            Color(0xFF627894)
        }


    // =================================================================
    // PANTALLA
    // =================================================================

    Scaffold(
        containerColor = background,

        // =============================================================
        // TOP BAR
        // =============================================================

        topBar = {
            TopBar()
        }

    ) { innerPadding ->


        // =============================================================
        // FONDO
        // =============================================================

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(background)
        ) {


            // =========================================================
            // CONTENIDO CENTRAL
            // =========================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 10.dp
                    ),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                // =====================================================
                // CHECK
                // =====================================================

                Box(
                    modifier = Modifier
                        .size(94.dp)
                        .background(
                            color = colors.pinkPrincipal.copy(
                                alpha = 0.12f
                            ),
                            shape = CircleShape
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Check,

                        contentDescription =
                            "Cuenta borrada correctamente",

                        tint = colors.pinkPrincipal,

                        modifier = Modifier.size(58.dp)
                    )
                }


                Spacer(
                    modifier = Modifier.height(24.dp)
                )


                // =====================================================
                // TÍTULO
                // =====================================================

                Text(
                    text = "Cuenta",
                    color = navy,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    lineHeight = 37.sp,
                    textAlign = TextAlign.Center
                )


                Text(
                    text = "borrada con éxito",
                    color = colors.pinkPrincipal,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    lineHeight = 37.sp,
                    textAlign = TextAlign.Center
                )


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                // =====================================================
                // LÍNEA ROSA
                // =====================================================

                Box(
                    modifier = Modifier
                        .width(58.dp)
                        .height(3.dp)
                        .background(
                            colors.pinkPrincipal
                        )
                )


                Spacer(
                    modifier = Modifier.height(30.dp)
                )


                // =====================================================
                // MENSAJE
                // =====================================================

                Text(
                    text =
                        "Tu cuenta ha sido eliminada correctamente.\n" +
                                "Gracias por haber sido parte de RIETI.",

                    color = secondaryText,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    lineHeight = 23.sp,
                    textAlign = TextAlign.Center
                )


                Spacer(
                    modifier = Modifier.height(46.dp)
                )


                // =====================================================
                // BOTÓN VOLVER AL INICIO
                // =====================================================

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .clip(
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            colors.pinkPrincipal
                        )
                        .clickable {

                            // Regresa a WelcomeScreen.
                            // Navigation limpiará el historial anterior.
                            onBackToStartClick()
                        },

                    contentAlignment = Alignment.Center
                ) {


                    // =================================================
                    // TEXTO DEL BOTÓN
                    // =================================================

                    Text(
                        text = "Volver al inicio",
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp
                    )


                    // =================================================
                    // FLECHA
                    // =================================================

                    Icon(
                        imageVector =
                            Icons.Default.ArrowForwardIos,

                        contentDescription = null,

                        tint = Color.White,

                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.CenterEnd)
                            .offset(
                                x = (-20).dp
                            )
                    )
                }
            }
        }
    }
}


// =====================================================================
// PREVIEW LIGHT
// =====================================================================

/**
 * Genera una vista previa de AccountDeletedScreen
 * utilizando el tema claro de RIETI.
 *
 * Permite visualizar la distribución de los elementos,
 * los colores y la presentación del mensaje de
 * confirmación desde Android Studio.
 *
 * Utiliza una acción de navegación vacía para
 * representar la pantalla sin ejecutar navegación.
 */
@Preview(
    name = "Account Deleted - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AccountDeletedLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        AccountDeletedScreen(
            onBackToStartClick = {}
        )
    }
}


// =====================================================================
// PREVIEW DARK
// =====================================================================

/**
 * Genera una vista previa de AccountDeletedScreen
 * utilizando el tema oscuro de RIETI.
 *
 * Permite verificar la apariencia de la pantalla
 * cuando se encuentran activos los colores
 * correspondientes al modo oscuro.
 *
 * Configura RIETITheme con darkTheme en true
 * y utiliza una acción de navegación vacía
 * únicamente para fines de previsualización.
 */
@Preview(
    name = "Account Deleted - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AccountDeletedDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        AccountDeletedScreen(
            onBackToStartClick = {}
        )
    }
}
