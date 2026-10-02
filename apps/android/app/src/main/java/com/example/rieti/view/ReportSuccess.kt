package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.AppTheme.colors
import com.example.rieti.ui.theme.BrandPink
import com.example.rieti.ui.theme.BrandPinkMedium
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.TopBar


@Composable
fun ReportSuccessScreen(
    folio: String,
    onNewReportClick: () -> Unit,
    onMainMenuClick: () -> Unit,
    onHomeClick: () -> Unit,
    onReportClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val colors = AppTheme.colors
    val isDarkTheme = AppTheme.isDarkTheme


    Scaffold(

        containerColor = colors.surface,

        // ==========================================
        // TOP BAR

        topBar = {

            TopBar(
                onMenuClick = {}
            )
        },


        // =======================================
        // BOTTOM BAR

        bottomBar = {

            BottomBar(

                onHomeClick = {
                    onHomeClick()
                },

                onReportClick = {
                    onReportClick()
                },

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
        ) {


            // ========================================
            // RAYA ROSA DEGRADADA (no sé si se escriba así xd)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                BrandPink,
                                BrandPinkMedium
                            )
                        )
                    )
            )


            Spacer(
                modifier = Modifier.height(34.dp)
            )


            // ====================================
            // REPORTE CREADO CON ÉXITO


            SuccessTitleBox(
                text = "Reporte creado\ncon éxito"
            )


            Spacer(
                modifier = Modifier.height(40.dp)
            )


            // =========================================
            // FOLIO

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 21.dp)
                    .height(76.dp)
                    .background(
                        color = colors.navVar,
                        shape = RoundedCornerShape(7.dp)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Folio:",
                        color = colors.text,
                        fontFamily = FontFamily.Serif,
                        fontSize = 30.sp
                    )

                    Text(
                        text = folio,
                        color = colors.text,
                        fontFamily = FontFamily.Serif,
                        fontSize = 30.sp
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(45.dp)
            )


            // *****************************
            // NUEVO REPORTE

            ReportSuccessButton(

                title = "Nuevo Reporte",

                subtitle = "Reporta cualquier forma de trabajo infantil",

                onClick = {
                    onNewReportClick()
                }
            )


            Separator()


            // **************************************
            // MENÚ PRINCIPAL

            ReportSuccessButton(

                title = "Menú Principal",

                subtitle = "",

                onClick = {
                    onMainMenuClick()
                }
            )


          //esto ocupa el espacio que resta
            Spacer(
                modifier = Modifier.weight(1f)
            )
        }
    }
}


// =================================================
// CUADRO CON BORDE

@Composable
private fun SuccessTitleBox(
    text: String
) {

    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 7.dp)
            .height(130.dp),

        contentAlignment = Alignment.Center
    ) {


        // Borde punteado
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {

            drawRoundRect(

                color = colors.titles,

                cornerRadius = CornerRadius(
                    x = 15.dp.toPx(),
                    y = 15.dp.toPx()
                ),

                style = Stroke(
                    width = 1.5.dp.toPx(),

                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(
                            3.dp.toPx(),
                            3.dp.toPx()
                        )
                    )
                )
            )
        }


        Text(
            text = text,

            color = colors.titles,

            fontFamily = FontFamily.Serif,

            fontSize = 32.sp,

            lineHeight = 43.sp,

            textAlign = TextAlign.Center
        )
    }
}


// ==========================================================
// BOTONES


@Composable
private fun ReportSuccessButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    val colors = AppTheme.colors


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(colors.bluePrincipal)
            .clickable {
                onClick()
            },

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {


        Text(
            text = title,

            color = colors.onPrimary,

            fontFamily = FontFamily.Serif,

            fontSize = 24.sp,

            textAlign = TextAlign.Center
        )


        if (subtitle.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,

                color = colors.onPrimary,

                fontSize = 13.sp,

                textAlign = TextAlign.Center
            )
        }
    }
}


// ===================================================
// SEPARADOR NEGRO


@Composable
private fun Separator() {

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(colors.surface)
    )
}


//======================================================
// PRUEBAS PARA BORRAR DESPUES DE IMPLEMENTAR NAVEGACION


@Preview(
    name = "Report Success - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ReportSuccessLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        ReportSuccessScreen(

            folio = "261908-1017S",

            onNewReportClick = {},

            onMainMenuClick = {},

            onHomeClick = {},

            onReportClick = {},

            onSettingsClick = {}
        )
    }
}



@Preview(
    name = "Report Success - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ReportSuccessDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        ReportSuccessScreen(

            folio = "261908-1017S",

            onNewReportClick = {},

            onMainMenuClick = {},

            onHomeClick = {},

            onReportClick = {},

            onSettingsClick = {}
        )
    }
}



