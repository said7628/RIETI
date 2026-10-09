package com.example.rieti.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
// REPORTE REGISTRADO CORRECTAMENTE
// =================================================================

@Composable
fun ReportSuccessScreen(
    folio: String,
    isAnonymous: Boolean = false,
    onTrackingClick: () -> Unit,
    onMainMenuClick: () -> Unit,
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val colors = AppTheme.colors


    Scaffold(
        containerColor = colors.surface,

        // =========================================================
        // TOP BAR
        // =========================================================

        topBar = {
            TopBar()
        },

        // =========================================================
        // BOTTOM BAR
        // Quiénes somos | Inicio | Configuración
        // =========================================================

        bottomBar = {

            BottomBar(
                onHomeClick = {
                    onHomeClick()
                },

                onAboutUsClick = {
                    onAboutUsClick()
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
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 14.dp,
                    bottom = 18.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // =====================================================
            // CONFIRMACIÓN
            // =====================================================

            SuccessCheckIcon()


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =====================================================
            // TÍTULO
            // =====================================================

            Text(
                text = "Reporte registrado",
                color = colors.titles,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 31.sp,
                textAlign = TextAlign.Center
            )


            Text(
                text = "correctamente",
                color = colors.pinkPrincipal,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 31.sp,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            // =====================================================
            // LÍNEA DECORATIVA
            // =====================================================

            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(4.dp)
                    .background(
                        color = colors.pinkPrincipal,
                        shape = RoundedCornerShape(20.dp)
                    )
            )


            Spacer(
                modifier = Modifier.height(17.dp)
            )


            // =====================================================
            // EXPLICACIÓN
            // =====================================================

            Text(
                text = if (isAnonymous) "Tu reporte anonimo fue recibido correctamente." else
                    "Conserva el siguiente folio para consultar posteriormente su estado.",
                color = colors.text.copy(alpha = 0.78f),
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // =====================================================
            // FOLIO
            // =====================================================

            if (!isAnonymous) FolioCard(folio = folio)


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =====================================================
            // AVISO
            // =====================================================

            if (!isAnonymous) FolioInformationCard()


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // =====================================================
            // CONSULTAR SEGUIMIENTO
            // =====================================================

            if (!isAnonymous) TrackingButton(onClick = { onTrackingClick() })


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            // =====================================================
            // VOLVER AL INICIO
            // =====================================================

            HomeButton(
                onClick = {
                    onMainMenuClick()
                }
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}


// =================================================================
// ÍCONO DE CONFIRMACIÓN
// =================================================================

@Composable
private fun SuccessCheckIcon() {

    val successGreen = Color(0xFF27A869)


    Box(
        modifier = Modifier
            .size(86.dp)
            .background(
                color = successGreen.copy(
                    alpha = 0.50f
                ),
                shape = CircleShape
            ),

        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Reporte registrado correctamente",
            tint = Color.White,
            modifier = Modifier.size(50.dp)
        )
    }
}


// =================================================================
// TARJETA DEL FOLIO
// =================================================================

@Composable
private fun FolioCard(
    folio: String
) {

    val colors = AppTheme.colors
    val context = LocalContext.current


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = 94.dp
            ),

        color = colors.pinkPrincipal.copy(
            alpha = 0.055f
        ),

        shape = RoundedCornerShape(12.dp),

        border = BorderStroke(
            width = 1.dp,
            color = colors.pinkPrincipal.copy(
                alpha = 0.28f
            )
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 18.dp,
                    end = 10.dp,
                    top = 14.dp,
                    bottom = 14.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {


            Column(
                modifier = Modifier.weight(1f)
            ) {

                // =================================================
                // FOLIO DE SEGUIMIENTO
                //
                // IMPORTANTE:
                // Antes estaba forzado a Color.White.
                //
                // Eso provocaba que el texto prácticamente
                // desapareciera sobre el fondo rosa en modo claro.
                //
                // Ahora utiliza colors.text para adaptarse
                // automáticamente al tema claro u oscuro.
                // =================================================

                Text(
                    text = "Folio de seguimiento",
                    color = colors.text,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )


                Spacer(
                    modifier = Modifier.height(4.dp)
                )


                Text(
                    text = folio,
                    color = colors.titles,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            }


            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(54.dp)
                    .background(
                        colors.text.copy(
                            alpha = 0.12f
                        )
                    )
            )


            Column(
                modifier = Modifier
                    .width(68.dp)
                    .clickable {

                        copyFolioToClipboard(
                            context = context,
                            folio = folio
                        )
                    },

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copiar folio",
                    tint = colors.titles,
                    modifier = Modifier.size(24.dp)
                )


                Spacer(
                    modifier = Modifier.height(3.dp)
                )


                Text(
                    text = "Copiar\nfolio",
                    color = colors.titles,
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    lineHeight = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


// =================================================================
// INFORMACIÓN SOBRE EL FOLIO
// =================================================================

@Composable
private fun FolioInformationCard() {

    val colors = AppTheme.colors


    Surface(
        modifier = Modifier.fillMaxWidth(),

        color = colors.bluePrincipal.copy(
            alpha = 0.08f
        ),

        shape = RoundedCornerShape(12.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 13.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {


            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = colors.bluePrincipal,
                modifier = Modifier.size(32.dp)
            )


            Spacer(
                modifier = Modifier.width(13.dp)
            )


            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(44.dp)
                    .background(
                        colors.bluePrincipal.copy(
                            alpha = 0.20f
                        )
                    )
            )


            Spacer(
                modifier = Modifier.width(13.dp)
            )


            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Guarda este folio en un lugar seguro.",
                    color = colors.titles,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )


                Spacer(
                    modifier = Modifier.height(2.dp)
                )


                Text(
                    text =
                        "Lo necesitarás para consultar posteriormente " +
                                "el estado de tu reporte.",

                    color = colors.text.copy(
                        alpha = 0.72f
                    ),

                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}


// =================================================================
// BOTÓN CONSULTAR SEGUIMIENTO
// =================================================================

@Composable
private fun TrackingButton(
    onClick: () -> Unit
) {

    val strongPink = Color(0xFFD10A5A)


    Button(
        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),

        shape = RoundedCornerShape(10.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = strongPink,
            contentColor = Color.White
        )
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Consultar seguimiento",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )


            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Consultar seguimiento",

                modifier = Modifier
                    .align(
                        Alignment.CenterEnd
                    )
                    .size(21.dp)
            )
        }
    }
}


// =================================================================
// BOTÓN VOLVER AL INICIO
// =================================================================

@Composable
private fun HomeButton(
    onClick: () -> Unit
) {

    OutlinedButton(
        onClick = {
            onClick()
        },

        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),

        shape = RoundedCornerShape(10.dp),

        border = BorderStroke(
            width = 1.dp,
            color = AppTheme.colors.pinkPrincipal.copy(
                alpha = 0.70f
            )
        ),

        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = AppTheme.colors.pinkPrincipal
        )
    ) {

        Text(
            text = "Volver al inicio",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}


// =================================================================
// COPIAR FOLIO
// =================================================================

private fun copyFolioToClipboard(
    context: Context,
    folio: String
) {

    val clipboard =
        context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager


    val clip =
        ClipData.newPlainText(
            "Folio RIETI",
            folio
        )


    clipboard.setPrimaryClip(clip)


    Toast.makeText(
        context,
        "Folio copiado",
        Toast.LENGTH_SHORT
    ).show()
}


// =================================================================
// PREVIEW LIGHT
// =================================================================

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
            onTrackingClick = {},
            onMainMenuClick = {},
            onHomeClick = {},
            onAboutUsClick = {},
            onSettingsClick = {}
        )
    }
}


// =================================================================
// PREVIEW DARK
// =================================================================

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
            onTrackingClick = {},
            onMainMenuClick = {},
            onHomeClick = {},
            onAboutUsClick = {},
            onSettingsClick = {}
        )
    }
}