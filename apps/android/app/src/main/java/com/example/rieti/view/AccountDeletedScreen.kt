package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.rieti.ui.theme.BrandPink
import com.example.rieti.ui.theme.BrandPinkMedium
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.TopBar


@Composable
fun AccountDeletedScreen(
    onHomeClick: () -> Unit,
    onReportClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val colors = AppTheme.colors


    Scaffold(

        containerColor = colors.surface,


        // ============================================
        // TOP BAR


        topBar = {

            Column {

                TopBar(
                    onMenuClick = {}
                )


                // Franja rosa debajo del TopBar

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
            }
        },


        // =========================================
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


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.surface)
        ) {


            // ======================================
            // MENSAJE CENTRAL

            DeletedAccountMessage(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 62.dp)
            )
        }
    }
}


// =================================================================
// CUADRO "CUENTA BORRADA CON ÉXITO"

@Composable
private fun DeletedAccountMessage(
    modifier: Modifier = Modifier
) {

    val colors = AppTheme.colors


    Box(
        modifier = modifier
            .size(
                width = 340.dp,
                height = 250.dp
            ),

        contentAlignment = Alignment.Center
    ) {


        // =====================================================
        // BORDE PUNTEADO

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            drawRoundRect(

                color = colors.titles,

                cornerRadius = CornerRadius(
                    x = 12.dp.toPx(),
                    y = 12.dp.toPx()
                ),

                style = Stroke(

                    width = 1.4.dp.toPx(),

                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(
                            3.dp.toPx(),
                            3.dp.toPx()
                        )
                    )
                )
            )
        }


        // ==================================================
        // TEXTO
        // =========================================

        Text(
            text = "Cuenta\nborrada con\néxito",

            color = colors.titles,

            fontFamily = FontFamily.Serif,

            fontSize = 40.sp,

            lineHeight = 60.sp,

            textAlign = TextAlign.Center
        )
    }
}


// =================================================================
//BORRAR ASDADAS

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
            onHomeClick = {},
            onReportClick = {},
            onSettingsClick = {}
        )
    }
}


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
            onHomeClick = {},
            onReportClick = {},
            onSettingsClick = {}
        )
    }
}