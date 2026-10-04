package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.R
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.AppTheme.colors
import com.example.rieti.ui.theme.BrandPink
import com.example.rieti.ui.theme.BrandPinkMedium
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.TopBar


@Composable
fun HomeScreen(
    onAnonymousReportClick: () -> Unit,
    onReportClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onTrackingClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val colors = AppTheme.colors
    val isDarkTheme = AppTheme.isDarkTheme


    // =========================================================
    // IMAGEN DEL REPORTE ANÓNIMO SEGÚN EL TEMA
    // =========================================================

    val anonymousReportImage = if (isDarkTheme) {

        R.drawable.anonymus_report_dark

    } else {

        R.drawable.anonymus_report_light
    }


    Scaffold(

        containerColor = colors.surface,


        // =====================================================
        // TOP BAR
        // =====================================================

        topBar = {

            TopBar(
                onMenuClick = {
                    // Menú hamburguesa descartado por ahora
                }
            )
        },


        // =====================================================
        // BOTTOM BAR
        // =====================================================

        bottomBar = {

            BottomBar(

                // Ya estamos en Home
                onHomeClick = {},

                onReportClick = {
                    onReportClick()
                },

                onSettingsClick = {
                    onSettingsClick()
                }
            )
        }

    ) { innerPadding ->


        // =====================================================
        // CONTENIDO
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.surface)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {


            // =================================================
            // REPORTE ANÓNIMO
            // =================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()

                    /*
                     * Altura total de esta sección.
                     *
                     * Incluye:
                     * - parte rosa
                     * - parte negra
                     */
                    .height(330.dp)

                    .background(colors.surface)

                    .clickable {
                        onAnonymousReportClick()
                    }
            ) {


                // =============================================
                // FONDO ROSA DEGRADADO
                // =============================================

                Box(
                    modifier = Modifier
                        .fillMaxWidth()

                        /*
                         * Hasta aquí llega el rosa.
                         */
                        .height(220.dp)

                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    BrandPink,
                                    BrandPinkMedium
                                )
                            )
                        )
                )


                // =============================================
                // IMAGEN REPORTE ANÓNIMO
                // =============================================

                Image(
                    painter = painterResource(
                        id = anonymousReportImage
                    ),

                    contentDescription = "Reporte Anónimo",

                    contentScale = ContentScale.Fit,

                    modifier = Modifier
                        .fillMaxWidth()

                        /*
                         * Tamaño vertical de la imagen.
                         *
                         * Como está encima del Box completo,
                         * puede verse tanto sobre el rosa
                         * como sobre el negro.
                         */
                        .requiredHeight(310.dp)

                        /*
                         * Ajusta esto para subirla o bajarla.
                         *
                         * positivo = baja
                         * negativo = sube
                         */
                        .offset(y = 5.dp)

                        .align(Alignment.TopCenter)
                )
            }

            HomeBlackSeparator()

            // =================================================
            // REALIZAR REPORTE
            // =================================================

            HomeOptionButton(

                title = "Realizar Reporte",

                subtitle = "Reporta cualquier forma de trabajo infantil",

                onClick = {
                    onReportClick()
                }
            )


            // =================================================
            // SEPARADOR
            // =================================================

            HomeBlackSeparator()


            // =================================================
            // QUIÉNES SOMOS
            // =================================================

            HomeOptionButton(

                title = "Quienes somos",

                subtitle = "SIPINNA",

                onClick = {
                    onAboutUsClick()
                }
            )


            // =================================================
            // SEPARADOR
            // =================================================

            HomeBlackSeparator()


            // =================================================
            // SEGUIMIENTO
            // =================================================

            HomeOptionButton(

                title = "Seguimiento",

                subtitle = "Números de contacto 55 3622 2700 / 55 3622 2800",

                onClick = {
                    onTrackingClick()
                }
            )
        }
    }
}


// =================================================================
// BOTONES GRANDES DEL HOME
// =================================================================

@Composable
private fun HomeOptionButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    val colors = AppTheme.colors


    Column(
        modifier = Modifier

            // Ocupa TODO el ancho
            .fillMaxWidth()

            // Altura del botón
            .height(64.dp)

            // Color dependiendo del tema
            .background(
                colors.bluePrincipal
            )

            // Lo hacemos clickeable
            .clickable {
                onClick()
            },

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {


        // =====================================================
        // TÍTULO
        // =====================================================

        Text(
            text = title,

            color = colors.onPrimary,

            fontFamily = FontFamily.Serif,

            fontSize = 23.sp,

            textAlign = TextAlign.Center
        )


        Spacer(
            modifier = Modifier.height(2.dp)
        )


        // =====================================================
        // SUBTÍTULO
        // =====================================================

        Text(
            text = subtitle,

            color = colors.onPrimary,

            fontSize = 10.sp,

            textAlign = TextAlign.Center,

            modifier = Modifier.padding(
                horizontal = 4.dp
            )
        )
    }
}


// =================================================================
// SEPARADOR NEGRO ENTRE BOTONES
// =================================================================

@Composable
private fun HomeBlackSeparator() {

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
            .background(colors.surface)

    )
}


// =================================================================
// PREVIEW DARK
// =================================================================

@Preview(
    name = "Home - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HomeScreenDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        HomeScreen(

            onAnonymousReportClick = {},

            onReportClick = {},

            onAboutUsClick = {},

            onTrackingClick = {},

            onSettingsClick = {}
        )
    }
}


// =================================================================
// PREVIEW LIGHT
// =================================================================

@Preview(
    name = "Home - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun HomeScreenLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        HomeScreen(

            onAnonymousReportClick = {},

            onReportClick = {},

            onAboutUsClick = {},

            onTrackingClick = {},

            onSettingsClick = {}
        )
    }
}