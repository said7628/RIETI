package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
// HOME
// =================================================================

@OptIn(ExperimentalMaterial3Api::class)
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

    var showReportTypeSheet by remember {
        mutableStateOf(false)
    }

    val reportTypeSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )


    // =============================================================
    // COLORES
    // =============================================================

    val mainPink =
        if (isDarkTheme) {
            Color(0xFFD71964)
        } else {
            Color(0xFFD9165D)
        }


    val secondaryCard =
        if (isDarkTheme) {
            Color(0xFF211D20)
        } else {
            Color(0xFFF9F9F9)
        }


    val secondaryBorder =
        if (isDarkTheme) {
            Color(0xFF4B4448)
        } else {
            Color(0xFFE1E1E1)
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


    val sheetBackground =
        if (isDarkTheme) {
            Color(0xFF171416)
        } else {
            Color(0xFFFFFBFC)
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

            TopBar( )
        },


        // =========================================================
        // BOTTOM BAR
        // =========================================================

        bottomBar = {

            BottomBar(

                // Ya estamos en Inicio
                onHomeClick = {},

                onAboutUsClick = {
                    onAboutUsClick()
                },

                onSettingsClick = {
                    onSettingsClick()
                }
            )
        }

    ) { innerPadding ->


        // =========================================================
        // CONTENIDO
        // =========================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.surface)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {


            // =====================================================
            // PRESENTACIÓN
            // =====================================================

            HomeHeroSection(
                isDarkTheme = isDarkTheme,
                primaryText = primaryText,
                secondaryText = secondaryText
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // =====================================================
            // REALIZAR REPORTE
            // =====================================================

            HomeActionCard(

                icon = Icons.Default.Description,

                title = "Realizar reporte",

                subtitle =
                    "Proporciona información sobre una posible situación de trabajo infantil.",

                backgroundColor = mainPink,

                borderColor = mainPink,

                iconColor = Color.White,

                titleColor = Color.White,

                subtitleColor = Color.White.copy(
                    alpha = 0.92f
                ),

                arrowColor = Color.White,

                onClick = {
                    showReportTypeSheet = true
                }
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // =====================================================
            // SEGUIMIENTO
            // =====================================================

            HomeActionCard(

                icon = Icons.Default.AccessTime,

                title = "Seguimiento",

                subtitle =
                    "Consulta el estado general de tu reporte.",

                backgroundColor = secondaryCard,

                borderColor = secondaryBorder,

                iconColor = primaryText,

                titleColor = primaryText,

                subtitleColor = secondaryText,

                arrowColor = primaryText,

                onClick = {
                    onTrackingClick()
                }
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // =====================================================
            // ORIENTACIÓN
            // =====================================================

            HomeActionCard(

                icon = Icons.AutoMirrored.Filled.MenuBook,

                title = "Orientación",

                subtitle =
                    "Conoce información útil sobre reportes y trabajo infantil.",

                backgroundColor = secondaryCard,

                borderColor = secondaryBorder,

                iconColor = primaryText,

                titleColor = primaryText,

                subtitleColor = secondaryText,

                arrowColor = primaryText,

                onClick = {

                    /*
                     * TEMPORAL:
                     *
                     * Actualmente reutilizamos onAboutUsClick()
                     * mientras se define una pantalla específica
                     * para Orientación.
                     */

                    onAboutUsClick()
                }
            )


            Spacer(
                modifier = Modifier.height(28.dp)
            )
        }
    }


    // =============================================================
    // MODAL PARA ELEGIR TIPO DE REPORTE
    // =============================================================

    if (showReportTypeSheet) {

        ModalBottomSheet(

            onDismissRequest = {
                showReportTypeSheet = false
            },

            sheetState = reportTypeSheetState,

            containerColor = sheetBackground,

            dragHandle = {

                Box(
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            bottom = 4.dp
                        )
                        .width(34.dp)
                        .height(4.dp)
                        .clip(
                            RoundedCornerShape(50)
                        )
                        .background(
                            if (isDarkTheme) {
                                Color(0xFF8A8185)
                            } else {
                                Color(0xFFB7BEC4)
                            }
                        )
                )
            }
        ) {

            ReportTypeContent(

                isDarkTheme = isDarkTheme,

                onClose = {
                    showReportTypeSheet = false
                },


                // =================================================
                // REPORTE ANÓNIMO
                // =================================================

                onAnonymousReportClick = {

                    showReportTypeSheet = false

                    /*
                     * TODO(BACKEND):
                     *
                     * Al enviar el reporte, deberá indicarse
                     * que la modalidad seleccionada corresponde
                     * a un reporte ANÓNIMO.
                     */

                    onAnonymousReportClick()
                },


                // =================================================
                // REPORTE CON SEGUIMIENTO
                // =================================================

                onTrackingReportClick = {

                    showReportTypeSheet = false

                    /*
                     * TODO(BACKEND):
                     *
                     * Al enviar el reporte, deberá indicarse
                     * que la modalidad seleccionada corresponde
                     * a un reporte CON SEGUIMIENTO.
                     */

                    onReportClick()
                }
            )
        }
    }
}


// =================================================================
// PRESENTACIÓN SUPERIOR
// =================================================================

@Composable
private fun HomeHeroSection(
    isDarkTheme: Boolean,
    primaryText: Color,
    secondaryText: Color
) {

    val pink =
        if (isDarkTheme) {
            Color(0xFFFF4A88)
        } else {
            Color(0xFFD9165D)
        }

    val teal = Color(0xFF16BFAF)


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(235.dp)
    ) {


        // =========================================================
        // CUADRADOS DECORATIVOS TRANSPARENTES
        // =========================================================


        // Rosa superior

        Box(
            modifier = Modifier
                .size(155.dp)
                .align(Alignment.TopEnd)
                .offset(
                    x = 30.dp,
                    y = (-30).dp
                )
                .background(
                    color = pink.copy(
                        alpha =
                            if (isDarkTheme) {
                                0.10f
                            } else {
                                0.07f
                            }
                    )
                )
        )


        // Rosa secundario

        Box(
            modifier = Modifier
                .size(102.dp)
                .align(Alignment.CenterEnd)
                .offset(
                    x = 20.dp,
                    y = (-14).dp
                )
                .background(
                    color = pink.copy(
                        alpha =
                            if (isDarkTheme) {
                                0.07f
                            } else {
                                0.045f
                            }
                    )
                )
        )


        // Turquesa inferior

        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomEnd)
                .offset(
                    x = 32.dp,
                    y = 15.dp
                )
                .background(
                    color = teal.copy(
                        alpha =
                            if (isDarkTheme) {
                                0.10f
                            } else {
                                0.07f
                            }
                    )
                )
        )


        // Turquesa pequeño superpuesto

        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomEnd)
                .offset(
                    x = (-35).dp,
                    y = 28.dp
                )
                .background(
                    color = teal.copy(
                        alpha =
                            if (isDarkTheme) {
                                0.06f
                            } else {
                                0.04f
                            }
                    )
                )
        )


        // =========================================================
        // CONTENIDO
        // =========================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 24.dp
                )
        ) {


            Text(
                text = "Por entornos",
                color = primaryText,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 37.sp
            )


            Text(
                text = "seguros",
                color = pink,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 37.sp,
                lineHeight = 40.sp
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(3.dp)
                    .background(pink)
            )


            Spacer(
                modifier = Modifier.height(15.dp)
            )


            Text(
                text =
                    "Informa sobre una posible situación de trabajo infantil de forma rápida y segura.",

                color = secondaryText,

                fontFamily = FontFamily.Serif,

                fontSize = 17.sp,

                lineHeight = 21.sp,

                modifier = Modifier.width(300.dp)
            )
        }
    }
}


// =================================================================
// TARJETAS DEL HOME
// =================================================================

@Composable
private fun HomeActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    borderColor: Color,
    iconColor: Color,
    titleColor: Color,
    subtitleColor: Color,
    arrowColor: Color,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp
            )
            .height(102.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(14.dp),

        color = backgroundColor,

        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 18.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {


            // =====================================================
            // ICONO
            // =====================================================

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(40.dp)
            )


            Spacer(
                modifier = Modifier.width(18.dp)
            )


            // =====================================================
            // TEXTO
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
                    fontSize = 21.sp
                )


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                Text(
                    text = subtitle,
                    color = subtitleColor,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }


            Spacer(
                modifier = Modifier.width(10.dp)
            )


            // =====================================================
            // FLECHA
            // =====================================================

            Text(
                text = "›",
                color = arrowColor,
                fontFamily = FontFamily.Serif,
                fontSize = 36.sp
            )
        }
    }
}

// =================================================================
// CONTENIDO DEL MODAL
// =================================================================

@Composable
private fun ReportTypeContent(
    isDarkTheme: Boolean,
    onClose: () -> Unit,
    onAnonymousReportClick: () -> Unit,
    onTrackingReportClick: () -> Unit
) {

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
            Color(0xFFFFFDFD)
        }


    val cardBorder =
        if (isDarkTheme) {
            Color(0xFF4B4448)
        } else {
            Color(0xFFE3E3E3)
        }


    val pink = Color(0xFFFF0060)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 22.dp
            ),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // =========================================================
        // CERRAR
        // =========================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            Text(
                text = "×",
                color = secondaryText,
                fontSize = 28.sp,

                modifier = Modifier
                    .clickable {
                        onClose()
                    }
                    .padding(
                        horizontal = 8.dp,
                        vertical = 2.dp
                    )
            )
        }


        // =========================================================
        // INDICADOR SUPERIOR
        // =========================================================

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    pink.copy(
                        alpha = 0.09f
                    )
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "▤",
                color = pink,
                fontSize = 25.sp
            )
        }


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // =========================================================
        // TÍTULO
        // =========================================================

        Text(
            text = "¿Qué tipo de reporte\ndeseas realizar?",
            color = primaryText,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            lineHeight = 23.sp,
            textAlign = TextAlign.Center
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text =
                "Selecciona la opción que mejor se adapte a tu situación. Puedes consultar los detalles de cada modalidad.",

            color = secondaryText,

            fontFamily = FontFamily.Serif,

            fontSize = 12.sp,

            lineHeight = 16.sp,

            textAlign = TextAlign.Center,

            modifier = Modifier.padding(
                horizontal = 22.dp
            )
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        // =========================================================
        // REPORTE ANÓNIMO
        // =========================================================

        ReportTypeOption(

            title = "Reporte anónimo",

            subtitle =
                "Tu identidad se mantendrá confidencial.",

            backgroundColor = cardBackground,

            borderColor = cardBorder,

            titleColor = primaryText,

            subtitleColor = secondaryText,

            onClick = onAnonymousReportClick
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // =========================================================
        // REPORTE CON SEGUIMIENTO
        // =========================================================

        ReportTypeOption(

            title = "Reporte con seguimiento",

            subtitle =
                "Podrás dar seguimiento al estado de tu reporte con un folio.",

            backgroundColor = cardBackground,

            borderColor = cardBorder,

            titleColor = primaryText,

            subtitleColor = secondaryText,

            onClick = onTrackingReportClick
        )


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // =========================================================
        // CANCELAR
        // =========================================================

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable {
                    onClose()
                },

            color = Color.Transparent,

            shape = RoundedCornerShape(8.dp),

            border = BorderStroke(
                width = 1.dp,
                color = pink
            )
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Cancelar",
                    color = pink,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}


// =================================================================
// OPCIÓN DEL MODAL
// =================================================================

@Composable
private fun ReportTypeOption(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    borderColor: Color,
    titleColor: Color,
    subtitleColor: Color,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .clickable {
                onClick()
            },

        color = backgroundColor,

        shape = RoundedCornerShape(12.dp),

        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = title,
                    color = titleColor,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                Text(
                    text = subtitle,
                    color = subtitleColor,
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }


            Spacer(
                modifier = Modifier.width(16.dp)
            )


            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFFB5C7D1),
                        shape = CircleShape
                    )
            )
        }
    }
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