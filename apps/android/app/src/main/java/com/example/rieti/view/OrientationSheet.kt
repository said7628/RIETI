package com.example.rieti.view

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.TopBar


// =====================================================================
// ORIENTACIÓN
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrientationScreen(
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val colors = AppTheme.colors
    val isDarkTheme = AppTheme.isDarkTheme

    var selectedOption by remember {
        mutableStateOf<OrientationOption?>(null)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )


    // =================================================================
    // COLORES
    // =================================================================

    val navy =
        if (isDarkTheme) {
            colors.text
        } else {
            Color(0xFF123A68)
        }

    val secondaryText =
        if (isDarkTheme) {
            colors.text.copy(alpha = 0.68f)
        } else {
            Color(0xFF627894)
        }

    val cardBackground =
        if (isDarkTheme) {
            colors.navVar
        } else {
            Color(0xFFFFFFFF)
        }

    val cardBorder =
        if (isDarkTheme) {
            colors.text.copy(alpha = 0.12f)
        } else {
            Color(0xFFE3E8ED)
        }

    val iconBackground =
        if (isDarkTheme) {
            colors.pinkPrincipal.copy(alpha = 0.15f)
        } else {
            Color(0xFFFCEAF1)
        }

    val sheetBackground =
        if (isDarkTheme) {
            colors.surface
        } else {
            Color(0xFFFFFBFC)
        }


    // =================================================================
    // PANTALLA
    // =================================================================

    Scaffold(

        containerColor = colors.surface,

        topBar = {
            TopBar()
        },

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
                    start = 12.dp,
                    end = 12.dp,
                    top = 18.dp,
                    bottom = 24.dp
                )
        ) {


            // =========================================================
            // TÍTULO
            // =========================================================

            Text(
                text = "Orientación",
                color = navy,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 38.sp
            )


            Spacer(
                modifier = Modifier.height(7.dp)
            )


            // =========================================================
            // LÍNEA ROSA
            // =========================================================

            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(3.dp)
                    .background(
                        colors.pinkPrincipal
                    )
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            // =========================================================
            // DESCRIPCIÓN
            // =========================================================

            Text(
                text =
                    "Encuentra información y recursos sobre " +
                            "el trabajo infantil y dónde puedes pedir apoyo.",

                color = secondaryText,
                fontFamily = FontFamily.Serif,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            // =========================================================
            // ¿QUÉ ES EL TRABAJO INFANTIL?
            // =========================================================

            OrientationCard(
                title = "¿Qué es el trabajo infantil?",
                description =
                    "Conoce qué es, sus formas y por qué es importante reportarlo.",
                icon = Icons.Default.MenuBook,
                navy = navy,
                secondaryText = secondaryText,
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                iconBackground = iconBackground,
                pink = colors.pinkPrincipal,

                onClick = {
                    selectedOption =
                        OrientationOption.WHAT_IS_CHILD_LABOR
                }
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            // =========================================================
            // SEÑALES DE ALERTA
            // =========================================================

            OrientationCard(
                title = "Señales de alerta",
                description =
                    "Identifica situaciones que podrían indicar trabajo infantil.",
                icon = Icons.Default.Groups,
                navy = navy,
                secondaryText = secondaryText,
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                iconBackground = iconBackground,
                pink = colors.pinkPrincipal,

                onClick = {
                    selectedOption =
                        OrientationOption.WARNING_SIGNS
                }
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            // =========================================================
            // ¿QUÉ PUEDO HACER?
            // =========================================================

            OrientationCard(
                title = "¿Qué puedo hacer?",
                description =
                    "Pasos que puedes seguir si identificas un caso.",
                icon = Icons.Default.Shield,
                navy = navy,
                secondaryText = secondaryText,
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                iconBackground = iconBackground,
                pink = colors.pinkPrincipal,

                onClick = {
                    selectedOption =
                        OrientationOption.WHAT_CAN_I_DO
                }
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            // =========================================================
            // INSTITUCIONES DE APOYO
            // =========================================================

            OrientationCard(
                title = "Instituciones de apoyo",
                description =
                    "Directorio de organizaciones y autoridades que pueden brindar atención.",
                icon = Icons.Default.Phone,
                navy = navy,
                secondaryText = secondaryText,
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                iconBackground = iconBackground,
                pink = colors.pinkPrincipal,

                onClick = {
                    selectedOption =
                        OrientationOption.SUPPORT_INSTITUTIONS
                }
            )


            Spacer(
                modifier = Modifier.height(9.dp)
            )


            // =========================================================
            // MATERIAL DE APOYO
            // =========================================================

            OrientationCard(
                title = "Material de apoyo",
                description =
                    "Guías, infografías y recursos adicionales.",
                icon = Icons.Default.Description,
                navy = navy,
                secondaryText = secondaryText,
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                iconBackground = iconBackground,
                pink = colors.pinkPrincipal,

                onClick = {
                    selectedOption =
                        OrientationOption.SUPPORT_MATERIAL
                }
            )
        }
    }


    // =================================================================
    // PESTAÑITA DE INFORMACIÓN
    // =================================================================

    selectedOption?.let { option ->

        ModalBottomSheet(

            onDismissRequest = {
                selectedOption = null
            },

            sheetState = sheetState,

            containerColor = sheetBackground,

            shape = RoundedCornerShape(
                topStart = 22.dp,
                topEnd = 22.dp
            ),

            dragHandle = {

                Box(
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            bottom = 5.dp
                        )
                        .width(38.dp)
                        .height(4.dp)
                        .background(
                            color =
                                secondaryText.copy(
                                    alpha = 0.28f
                                ),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        ) {


            OrientationInformationSheet(
                option = option,
                navy = navy,
                secondaryText = secondaryText,
                pink = colors.pinkPrincipal,

                onCloseClick = {
                    selectedOption = null
                }
            )
        }
    }
}


// =====================================================================
// OPCIONES DE ORIENTACIÓN
// =====================================================================

private enum class OrientationOption {

    WHAT_IS_CHILD_LABOR,

    WARNING_SIGNS,

    WHAT_CAN_I_DO,

    SUPPORT_INSTITUTIONS,

    SUPPORT_MATERIAL
}


// =====================================================================
// TARJETA DE ORIENTACIÓN
// =====================================================================

@Composable
private fun OrientationCard(
    title: String,
    description: String,
    icon: ImageVector,
    navy: Color,
    secondaryText: Color,
    cardBackground: Color,
    cardBorder: Color,
    iconBackground: Color,
    pink: Color,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        color = cardBackground,

        shape = RoundedCornerShape(8.dp),

        border = BorderStroke(
            width = 1.dp,
            color = cardBorder
        )
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {


            // =========================================================
            // ICONO
            // =========================================================

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = iconBackground,
                        shape = CircleShape
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = navy,
                    modifier = Modifier.size(26.dp)
                )
            }


            Spacer(
                modifier = Modifier.width(14.dp)
            )


            // =========================================================
            // TEXTO
            // =========================================================

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = navy,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 18.sp
                )


                Spacer(
                    modifier = Modifier.height(2.dp)
                )


                Text(
                    text = description,
                    color = secondaryText,
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    lineHeight = 15.sp
                )
            }


            Spacer(
                modifier = Modifier.width(8.dp)
            )


            // =========================================================
            // FLECHA
            // =========================================================

            Icon(
                imageVector =
                    Icons.AutoMirrored.Filled.ArrowForwardIos,

                contentDescription = null,

                tint = pink,

                modifier = Modifier.size(18.dp)
            )
        }
    }
}


// =====================================================================
// CONTENIDO DE LA PESTAÑITA
// =====================================================================

@Composable
private fun OrientationInformationSheet(
    option: OrientationOption,
    navy: Color,
    secondaryText: Color,
    pink: Color,
    onCloseClick: () -> Unit
) {

    val title =
        when (option) {

            OrientationOption.WHAT_IS_CHILD_LABOR ->
                "¿Qué es el trabajo infantil?"

            OrientationOption.WARNING_SIGNS ->
                "Señales de alerta"

            OrientationOption.WHAT_CAN_I_DO ->
                "¿Qué puedo hacer?"

            OrientationOption.SUPPORT_INSTITUTIONS ->
                "Instituciones de apoyo"

            OrientationOption.SUPPORT_MATERIAL ->
                "Material de apoyo"
        }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 22.dp,
                end = 22.dp,
                bottom = 34.dp
            )
    ) {


        // =============================================================
        // ENCABEZADO
        // =============================================================

        Row(
            modifier = Modifier.fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {


            Text(
                text = title,
                color = navy,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                modifier = Modifier.weight(1f)
            )


            IconButton(
                onClick = {
                    onCloseClick()
                }
            ) {

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = navy,
                    modifier = Modifier.size(22.dp)
                )
            }
        }


        Spacer(
            modifier = Modifier.height(5.dp)
        )


        Box(
            modifier = Modifier
                .width(45.dp)
                .height(2.dp)
                .background(pink)
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // =============================================================
        // CONTENIDO TEMPORAL
        // =============================================================

        Text(
            text = "Nope data",
            color = navy,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )


        Spacer(
            modifier = Modifier.height(6.dp)
        )


        Text(
            text =
                "La información correspondiente a esta sección " +
                        "se agregará próximamente.",

            color = secondaryText,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )
    }
}


// =====================================================================
// PREVIEW LIGHT
// =====================================================================

@Preview(
    name = "Orientation - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun OrientationLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        OrientationScreen(
            onHomeClick = {},
            onAboutUsClick = {},
            onSettingsClick = {}
        )
    }
}


// =====================================================================
// PREVIEW DARK
// =====================================================================

@Preview(
    name = "Orientation - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun OrientationDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        OrientationScreen(
            onHomeClick = {},
            onAboutUsClick = {},
            onSettingsClick = {}
        )
    }
}