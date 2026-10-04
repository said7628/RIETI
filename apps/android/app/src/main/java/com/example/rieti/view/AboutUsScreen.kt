package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.TopBar


// =================================================================
// ABOUT US
// =================================================================

@Composable
fun AboutUsScreen(
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val colors = AppTheme.colors
    val isDarkTheme = AppTheme.isDarkTheme


    // =============================================================
    // COLORES
    // =============================================================

    val pink =
        if (isDarkTheme) {
            Color(0xFFFF4A88)
        } else {
            Color(0xFFD9165D)
        }

    val teal =
        if (isDarkTheme) {
            Color(0xFF49C9BD)
        } else {
            Color(0xFF16AFA4)
        }

    val primaryText =
        if (isDarkTheme) {
            Color(0xFFF8F5F6)
        } else {
            Color(0xFF123E5B)
        }

    val secondaryText =
        if (isDarkTheme) {
            Color(0xFFC8BEC3)
        } else {
            Color(0xFF587081)
        }

    val cardBackground =
        if (isDarkTheme) {
            Color(0xFF211D20)
        } else {
            Color(0xFFF9F9F9)
        }

    val cardBorder =
        if (isDarkTheme) {
            Color(0xFF4B4448)
        } else {
            Color(0xFFE1E1E1)
        }


    // =============================================================
    // PANTALLA
    // =============================================================

    Scaffold(

        containerColor = colors.surface,


        // =========================================================
        // TOP BAR
        // =========================================================

        topBar = {

            TopBar(

            )
        },


        // =========================================================
        // BOTTOM BAR
        // =========================================================

        bottomBar = {

            BottomBar(

                onHomeClick = {
                    onHomeClick()
                },

                // Ya estamos en Quiénes somos
                onAboutUsClick = {},

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
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 20.dp
                )
        ) {


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // =====================================================
            // ENCABEZADO
            // =====================================================

            Text(
                text = "Conoce",
                color = primaryText,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                lineHeight = 32.sp
            )

            Text(
                text = "RIETI",
                color = pink,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 34.sp
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            Box(
                modifier = Modifier
                    .width(34.dp)
                    .height(2.dp)
                    .background(pink)
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            Text(
                text = "Una herramienta para informar y dar seguimiento a posibles situaciones de trabajo infantil.",
                color = secondaryText,
                fontFamily = FontFamily.Serif,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )


            Spacer(
                modifier = Modifier.height(26.dp)
            )


            // =====================================================
            // ¿QUÉ ES RIETI?
            // =====================================================

            AboutUsCard(
                title = "¿Qué es RIETI?",
                text = "RIETI es una herramienta tecnológica orientada al registro, canalización y seguimiento de reportes relacionados con posibles situaciones de trabajo infantil.",
                accentColor = pink,
                backgroundColor = cardBackground,
                borderColor = cardBorder,
                titleColor = primaryText,
                textColor = secondaryText
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // =====================================================
            // NUESTRO PROPÓSITO
            // =====================================================

            AboutUsCard(
                title = "Nuestro propósito",
                text = "Facilitar un medio accesible para proporcionar información sobre una posible situación de trabajo infantil y apoyar su adecuada canalización y seguimiento.",
                accentColor = teal,
                backgroundColor = cardBackground,
                borderColor = cardBorder,
                titleColor = primaryText,
                textColor = secondaryText
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // =====================================================
            // PRIVACIDAD
            // =====================================================

            AboutUsCard(
                title = "Tu privacidad importa",
                text = "RIETI contempla diferentes modalidades de reporte para que cada persona pueda elegir la alternativa adecuada según su situación.",
                accentColor = pink,
                backgroundColor = cardBackground,
                borderColor = cardBorder,
                titleColor = primaryText,
                textColor = secondaryText
            )


            Spacer(
                modifier = Modifier.height(28.dp)
            )


            // =====================================================
            // MENSAJE FINAL
            // =====================================================

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = pink.copy(
                    alpha = if (isDarkTheme) 0.12f else 0.07f
                ),
                shape = RoundedCornerShape(14.dp)
            ) {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Por entornos",
                        color = primaryText,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "seguros",
                        color = pink,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Informar también es una forma de contribuir a la protección de niñas, niños y adolescentes.",
                        color = secondaryText,
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(28.dp)
            )
        }
    }
}


// =================================================================
// TARJETA INFORMATIVA
// =================================================================

@Composable
private fun AboutUsCard(
    title: String,
    text: String,
    accentColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    titleColor: Color,
    textColor: Color
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),

        color = backgroundColor,

        shape = RoundedCornerShape(14.dp),

        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 18.dp
            )
        ) {


            // =====================================================
            // ACENTO LATERAL
            // =====================================================

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(58.dp)
                    .background(
                        color = accentColor,
                        shape = RoundedCornerShape(50)
                    )
            )


            Spacer(
                modifier = Modifier.width(14.dp)
            )


            // =====================================================
            // INFORMACIÓN
            // =====================================================

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = title,
                    color = titleColor,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )


                Spacer(
                    modifier = Modifier.height(7.dp)
                )


                Text(
                    text = text,
                    color = textColor,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}


// =================================================================
// PREVIEW LIGHT
// =================================================================

@Preview(
    name = "About Us - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AboutUsScreenLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        AboutUsScreen(
            onHomeClick = {},
            onSettingsClick = {}
        )
    }
}


// =================================================================
// PREVIEW DARK
// =================================================================

@Preview(
    name = "About Us - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AboutUsScreenDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        AboutUsScreen(
            onHomeClick = {},
            onSettingsClick = {}
        )
    }
}