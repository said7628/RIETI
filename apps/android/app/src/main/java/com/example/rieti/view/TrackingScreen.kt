package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
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
// ESTADOS DEL SEGUIMIENTO
// =================================================================

private enum class TrackingStatus {
    COMPLETED,
    CURRENT,
    PENDING
}


// =================================================================
// PANTALLA DE SEGUIMIENTO
// =================================================================

@Composable
fun TrackingScreen(
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val colors = AppTheme.colors

    // =============================================================
    // DATOS TEMPORALES
    //
    // TODO(BACKEND):
    // El folio se utilizará posteriormente para consultar el
    // reporte real mediante el endpoint correspondiente.
    // =============================================================

    var reportFolio by rememberSaveable {
        mutableStateOf("")
    }

    var reportFound by rememberSaveable {
        mutableStateOf(false)
    }


    Scaffold(
        containerColor = colors.surface,

        topBar = {
            TopBar()
        },

        bottomBar = {

            BottomBar(
                onHomeClick = onHomeClick,
                onAboutUsClick = onAboutUsClick,
                onSettingsClick = onSettingsClick
            )
        }

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.surface)
        ) {

            if (!reportFound) {

                TrackingSearchContent(
                    folio = reportFolio,

                    onFolioChange = {
                        reportFolio = it
                    },

                    onSearchClick = {

                        /*
                         * TODO(BACKEND):
                         *
                         * Sustituir este cambio de estado por
                         * la consulta real del reporte mediante
                         * el folio ingresado.
                         *
                         * Si el reporte existe:
                         * mostrar los datos recibidos.
                         *
                         * Si no existe:
                         * mostrar el mensaje de error correspondiente.
                         */

                        if (reportFolio.isNotBlank()) {
                            reportFound = true
                        }
                    }
                )

            } else {

                TrackingResultContent(
                    folio = reportFolio,

                    onEditFolioClick = {
                        reportFound = false
                    }
                )
            }
        }
    }
}


// =================================================================
// VISTA 1 - CONSULTAR FOLIO
// =================================================================

@Composable
private fun TrackingSearchContent(
    folio: String,
    onFolioChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {

    val colors = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                start = 22.dp,
                end = 22.dp,
                top = 20.dp,
                bottom = 30.dp
            )
    ) {

        TrackingTitle(
            title = "Seguimiento",

            description =
                "Consulta el estado general de un reporte mediante el folio asignado al momento de su registro."
        )


        Spacer(
            modifier = Modifier.height(28.dp)
        )


        // =========================================================
        // FOLIO
        // =========================================================

        Text(
            text = "Folio del reporte",

            color = colors.titles,

            fontFamily = FontFamily.Serif,

            fontWeight = FontWeight.Bold,

            fontSize = 15.sp
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        TrackingSearchField(
            value = folio,

            placeholder =
                "Ej. RIETI-ATZ-2026-000123",

            onValueChange = onFolioChange
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // =========================================================
        // CONSULTAR
        // =========================================================

        Button(
            onClick = onSearchClick,

            enabled = folio.isNotBlank(),

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),

            shape = RoundedCornerShape(8.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor =
                    colors.pinkPrincipal,

                contentColor = Color.White,

                disabledContainerColor =
                    colors.pinkPrincipal.copy(
                        alpha = 0.35f
                    ),

                disabledContentColor =
                    Color.White.copy(
                        alpha = 0.75f
                    )
            )
        ) {

            Text(
                text = "Consultar reporte",

                fontFamily = FontFamily.Serif,

                fontWeight = FontWeight.Bold,

                fontSize = 16.sp
            )


            Spacer(
                modifier = Modifier.width(10.dp)
            )


            Text(
                text = "›",

                fontFamily = FontFamily.Serif,

                fontSize = 27.sp
            )
        }


        Spacer(
            modifier = Modifier.height(22.dp)
        )


        // =========================================================
        // AVISO DE SEGURIDAD
        // =========================================================

        TrackingInformationNotice(
            text =
                "Por seguridad, la consulta muestra únicamente información relacionada con el avance del reporte."
        )
    }
}


// =================================================================
// VISTA 2 - RESULTADO DEL SEGUIMIENTO
// =================================================================

@Composable
private fun TrackingResultContent(
    folio: String,
    onEditFolioClick: () -> Unit
) {

    val colors = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                start = 18.dp,
                end = 18.dp,
                top = 14.dp,
                bottom = 30.dp
            )
    ) {

        TrackingTitle(
            title = "Seguimiento",

            description =
                "Consulta el estado general de tu reporte."
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // =========================================================
        // FOLIO CONSULTADO
        // =========================================================

        TrackingCurrentFolio(
            folio = folio,

            onEditClick = onEditFolioClick
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        // =========================================================
        // REPORTE
        // =========================================================

        Text(
            text = "Reporte",

            color = colors.titles,

            fontFamily = FontFamily.Serif,

            fontWeight = FontWeight.Bold,

            fontSize = 21.sp
        )


        Spacer(
            modifier = Modifier.height(2.dp)
        )


        Text(
            text = folio,

            color = colors.pinkPrincipal,

            fontFamily = FontFamily.Serif,

            fontWeight = FontWeight.Bold,

            fontSize = 18.sp
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // =========================================================
        // ESTADO ACTUAL
        // =========================================================

        CurrentStatusCard()


        Spacer(
            modifier = Modifier.height(22.dp)
        )


        // =========================================================
        // AVANCE
        // =========================================================

        Text(
            text = "Avance del reporte",

            color = colors.titles,

            fontFamily = FontFamily.Serif,

            fontWeight = FontWeight.Bold,

            fontSize = 18.sp
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        TrackingTimeline()


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // =========================================================
        // INFORMACIÓN GENERAL
        // =========================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    colors.text.copy(
                        alpha = 0.13f
                    )
                )
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        Text(
            text = "Información del reporte",

            color = colors.titles,

            fontFamily = FontFamily.Serif,

            fontWeight = FontWeight.Bold,

            fontSize = 18.sp
        )


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        ReportGeneralInformation()


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        TrackingInformationNotice(
            text =
                "Esta consulta muestra únicamente información general sobre el avance del reporte."
        )
    }
}


// =================================================================
// TÍTULO
// =================================================================

@Composable
private fun TrackingTitle(
    title: String,
    description: String
) {

    val colors = AppTheme.colors

    Text(
        text = title,

        color = colors.titles,

        fontFamily = FontFamily.Serif,

        fontWeight = FontWeight.Bold,

        fontSize = 30.sp
    )


    Spacer(
        modifier = Modifier.height(5.dp)
    )


    Box(
        modifier = Modifier
            .width(48.dp)
            .height(3.dp)
            .background(
                colors.pinkPrincipal
            )
    )


    Spacer(
        modifier = Modifier.height(9.dp)
    )


    Text(
        text = description,

        color =
            colors.text.copy(
                alpha = 0.72f
            ),

        fontFamily = FontFamily.Serif,

        fontSize = 14.sp,

        lineHeight = 19.sp
    )
}


// =================================================================
// CAMPO DE BÚSQUEDA
// =================================================================

@Composable
private fun TrackingSearchField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {

    val colors = AppTheme.colors

    /*
     * IMPORTANTE:
     *
     * Antes utilizábamos colors.textTyping para el texto escrito.
     * Para asegurar contraste tanto en Light como en Dark,
     * utilizamos colors.titles, que ya funciona correctamente
     * en ambos temas dentro de esta misma pantalla.
     */

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),

        color =
            colors.text.copy(
                alpha = 0.055f
            ),

        shape = RoundedCornerShape(8.dp),

        border = BorderStroke(
            width = 1.dp,

            color =
                colors.text.copy(
                    alpha = 0.18f
                )
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 14.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Search,

                contentDescription = null,

                tint = colors.titles,

                modifier = Modifier.size(22.dp)
            )


            Spacer(
                modifier = Modifier.width(12.dp)
            )


            BasicTextField(
                value = value,

                onValueChange = onValueChange,

                singleLine = true,

                // =================================================
                // TEXTO ESCRITO POR EL USUARIO
                // =================================================

                textStyle = TextStyle(
                    color = colors.titles,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),

                // =================================================
                // CURSOR
                // =================================================

                cursorBrush =
                    SolidColor(
                        colors.pinkPrincipal
                    ),

                modifier = Modifier.weight(1f),

                decorationBox = { innerTextField ->

                    Box(
                        modifier = Modifier.fillMaxWidth(),

                        contentAlignment =
                            Alignment.CenterStart
                    ) {

                        // =========================================
                        // PLACEHOLDER
                        // =========================================

                        if (value.isEmpty()) {

                            Text(
                                text = placeholder,

                                color =
                                    colors.text.copy(
                                        alpha = 0.55f
                                    ),

                                fontSize = 12.sp
                            )
                        }


                        // =========================================
                        // CAMPO REAL
                        // =========================================

                        innerTextField()
                    }
                }
            )
        }
    }
}


// =================================================================
// FOLIO ACTUAL
// =================================================================

@Composable
private fun TrackingCurrentFolio(
    folio: String,
    onEditClick: () -> Unit
) {

    val colors = AppTheme.colors

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),

        color =
            colors.text.copy(
                alpha = 0.035f
            ),

        shape = RoundedCornerShape(7.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 12.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Search,

                contentDescription = null,

                tint = colors.titles,

                modifier = Modifier.size(19.dp)
            )


            Spacer(
                modifier = Modifier.width(10.dp)
            )


            Text(
                text = folio,

                color = colors.titles,

                fontSize = 12.sp,

                fontWeight = FontWeight.Bold,

                modifier = Modifier.weight(1f)
            )


            Icon(
                imageVector = Icons.Default.Edit,

                contentDescription =
                    "Consultar otro folio",

                tint = colors.pinkPrincipal,

                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onEditClick()
                    }
            )
        }
    }
}


// =================================================================
// ESTADO ACTUAL
// =================================================================

@Composable
private fun CurrentStatusCard() {

    val colors = AppTheme.colors

    Surface(
        modifier = Modifier.fillMaxWidth(),

        color =
            colors.pinkPrincipal.copy(
                alpha = 0.065f
            ),

        shape = RoundedCornerShape(9.dp)
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 15.dp
            ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            // =====================================================
            // ICONO
            // =====================================================

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFFFB400),
                        shape = CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MoreHoriz,

                    contentDescription = null,

                    tint = Color(0xFFFFB400),

                    modifier = Modifier.size(27.dp)
                )
            }


            Spacer(
                modifier = Modifier.width(14.dp)
            )


            Column {

                Text(
                    text = "Estado actual",

                    color =
                        colors.text.copy(
                            alpha = 0.62f
                        ),

                    fontSize = 11.sp
                )


                Text(
                    text = "En seguimiento",

                    color = colors.titles,

                    fontFamily = FontFamily.Serif,

                    fontWeight = FontWeight.Bold,

                    fontSize = 18.sp
                )


                Spacer(
                    modifier = Modifier.height(3.dp)
                )


                Text(
                    text =
                        "Última actualización\n03 de octubre de 2026, 14:25 h",

                    color =
                        colors.text.copy(
                            alpha = 0.65f
                        ),

                    fontSize = 10.sp,

                    lineHeight = 13.sp
                )
            }
        }
    }
}


// =================================================================
// LÍNEA DE TIEMPO
// =================================================================

@Composable
private fun TrackingTimeline() {

    Column {

        TrackingTimelineItem(
            title = "Registrado",

            date =
                "28 de septiembre de 2026, 10:12 h",

            description =
                "Tu reporte ha sido recibido.",

            status = TrackingStatus.COMPLETED,

            showLine = true
        )


        TrackingTimelineItem(
            title = "En revisión",

            date =
                "29 de septiembre de 2026, 16:40 h",

            description =
                "El reporte está siendo revisado por el equipo correspondiente.",

            status = TrackingStatus.COMPLETED,

            showLine = true
        )


        TrackingTimelineItem(
            title = "En seguimiento",

            date =
                "03 de octubre de 2026, 14:25 h",

            description =
                "El reporte se encuentra en seguimiento con la instancia responsable.",

            status = TrackingStatus.CURRENT,

            showLine = true
        )


        TrackingTimelineItem(
            title = "Canalizado",

            date = "Pendiente",

            description = "",

            status = TrackingStatus.PENDING,

            showLine = true
        )


        TrackingTimelineItem(
            title = "Concluido",

            date = "Pendiente",

            description = "",

            status = TrackingStatus.PENDING,

            showLine = false
        )
    }
}


// =================================================================
// ELEMENTO DE LÍNEA DE TIEMPO
// =================================================================

@Composable
private fun TrackingTimelineItem(
    title: String,
    date: String,
    description: String,
    status: TrackingStatus,
    showLine: Boolean
) {

    val colors = AppTheme.colors

    val teal = Color(0xFF20AFAE)

    val circleColor =
        when (status) {

            TrackingStatus.COMPLETED ->
                teal

            TrackingStatus.CURRENT ->
                colors.pinkPrincipal

            TrackingStatus.PENDING ->
                colors.text.copy(
                    alpha = 0.35f
                )
        }


    Row(
        modifier = Modifier.fillMaxWidth()
    ) {


        // =========================================================
        // INDICADOR
        // =========================================================

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(23.dp)
                    .then(

                        if (
                            status ==
                            TrackingStatus.PENDING
                        ) {

                            Modifier.border(
                                width = 1.5.dp,
                                color = circleColor,
                                shape = CircleShape
                            )

                        } else {

                            Modifier
                                .clip(CircleShape)
                                .background(circleColor)
                        }
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                if (
                    status ==
                    TrackingStatus.COMPLETED
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Check,

                        contentDescription = null,

                        tint = Color.White,

                        modifier = Modifier.size(15.dp)
                    )

                } else if (
                    status ==
                    TrackingStatus.CURRENT
                ) {

                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }


            if (showLine) {

                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(
                            if (description.isBlank()) {
                                35.dp
                            } else {
                                62.dp
                            }
                        )
                        .background(
                            colors.text.copy(
                                alpha = 0.25f
                            )
                        )
                )
            }
        }


        Spacer(
            modifier = Modifier.width(12.dp)
        )


        // =========================================================
        // INFORMACIÓN
        // =========================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    bottom =
                        if (description.isBlank()) {
                            14.dp
                        } else {
                            18.dp
                        }
                )
        ) {

            Text(
                text = title,

                color =
                    if (
                        status ==
                        TrackingStatus.CURRENT
                    ) {
                        colors.pinkPrincipal
                    } else {
                        colors.titles
                    },

                fontWeight = FontWeight.Bold,

                fontSize = 13.sp
            )


            Spacer(
                modifier = Modifier.height(2.dp)
            )


            Text(
                text = date,

                color =
                    colors.text.copy(
                        alpha = 0.62f
                    ),

                fontSize = 10.sp
            )


            if (description.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(3.dp)
                )


                Text(
                    text = description,

                    color =
                        colors.text.copy(
                            alpha = 0.72f
                        ),

                    fontSize = 10.sp,

                    lineHeight = 13.sp
                )
            }
        }
    }
}


// =================================================================
// INFORMACIÓN GENERAL
// =================================================================

@Composable
private fun ReportGeneralInformation() {

    val colors = AppTheme.colors

    Surface(
        modifier = Modifier.fillMaxWidth(),

        color =
            colors.text.copy(
                alpha = 0.035f
            ),

        shape = RoundedCornerShape(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 13.dp,
                vertical = 12.dp
            )
        ) {

            GeneralInformationRow(
                icon = Icons.Default.Description,
                label = "Modalidad",
                value = "Con seguimiento"
            )


            Spacer(
                modifier = Modifier.height(13.dp)
            )


            GeneralInformationRow(
                icon = Icons.Default.CalendarMonth,
                label = "Fecha de registro",
                value = "28 de septiembre de 2026"
            )


            Spacer(
                modifier = Modifier.height(13.dp)
            )


            GeneralInformationRow(
                icon = Icons.Default.LocationOn,
                label = "Municipio",
                value = "Atizapán de Zaragoza"
            )
        }
    }
}


// =================================================================
// FILA DE INFORMACIÓN
// =================================================================

@Composable
private fun GeneralInformationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {

    val colors = AppTheme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,

            contentDescription = null,

            tint = colors.titles,

            modifier = Modifier.size(18.dp)
        )


        Spacer(
            modifier = Modifier.width(9.dp)
        )


        Text(
            text = label,

            color = colors.titles,

            fontSize = 11.sp,

            fontWeight = FontWeight.Bold,

            modifier = Modifier.weight(1f)
        )


        Text(
            text = value,

            color =
                colors.text.copy(
                    alpha = 0.70f
                ),

            fontSize = 10.sp,

            textAlign = TextAlign.End,

            modifier = Modifier.weight(1.15f)
        )
    }
}


// =================================================================
// AVISO DE INFORMACIÓN
// =================================================================

@Composable
private fun TrackingInformationNotice(
    text: String
) {

    val colors = AppTheme.colors

    Surface(
        modifier = Modifier.fillMaxWidth(),

        color =
            colors.pinkPrincipal.copy(
                alpha = 0.055f
            ),

        shape = RoundedCornerShape(8.dp)
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 13.dp,
                vertical = 13.dp
            ),

            verticalAlignment =
                Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(
                        width = 1.5.dp,

                        color =
                            colors.pinkPrincipal,

                        shape = CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Info,

                    contentDescription = null,

                    tint = colors.pinkPrincipal,

                    modifier = Modifier.size(17.dp)
                )
            }


            Spacer(
                modifier = Modifier.width(11.dp)
            )


            Text(
                text = text,

                color =
                    colors.text.copy(
                        alpha = 0.72f
                    ),

                fontFamily = FontFamily.Serif,

                fontSize = 11.sp,

                lineHeight = 15.sp,

                modifier = Modifier.weight(1f)
            )
        }
    }
}


// =================================================================
// PREVIEW LIGHT
// =================================================================

@Preview(
    name = "Seguimiento - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun TrackingScreenLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        TrackingScreen(
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
    name = "Seguimiento - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun TrackingScreenDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        TrackingScreen(
            onHomeClick = {},
            onAboutUsClick = {},
            onSettingsClick = {}
        )
    }
}