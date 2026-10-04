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
// ESTADO REAL DEL REPORTE
// =================================================================

private enum class ReportStatus {
    REGISTRADO,
    EN_REVISION,
    EN_SEGUIMIENTO,
    CANALIZADO,
    CONCLUIDO,
    ARCHIVADO,
    CANCELADO,
    REINCIDENTE
}


// =================================================================
// ESTADO VISUAL DE CADA PUNTO DEL TIMELINE
// =================================================================

private enum class TrackingStatus {
    COMPLETED,
    CURRENT,
    PENDING
}


// =================================================================
// MODELO DE UI
//
// TODO(BACKEND):
// Posteriormente estos datos deberán construirse a partir de la
// respuesta del endpoint de seguimiento.
// =================================================================

private data class TrackingReportUi(
    val folio: String,
    val status: ReportStatus,
    val lastUpdate: String,
    val registrationDate: String,
    val municipality: String,
    val modality: String,
    val closingDate: String? = null,
    val resolution: String? = null
)


// =================================================================
// PANTALLA PRINCIPAL
// =================================================================

@Composable
fun TrackingScreen(
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val colors = AppTheme.colors

    var reportFolio by rememberSaveable {
        mutableStateOf("")
    }

    var selectedReportStatus by rememberSaveable {
        mutableStateOf<ReportStatus?>(null)
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

            if (selectedReportStatus == null) {

                TrackingSearchContent(
                    folio = reportFolio,

                    onFolioChange = {
                        reportFolio = it
                    },

                    onSearchClick = {

                        /*
                         * TODO(BACKEND):
                         * DATOS SIMULADOS ÚNICAMENTE PARA PRUEBAS DE UI.
                         *
                         * Cuando se conecte el backend:
                         *
                         * 1. Eliminar la selección mediante folios
                         *    especiales de prueba.
                         *
                         * 2. Consultar el reporte real mediante el
                         *    folio ingresado.
                         *
                         * 3. Convertir la respuesta del API a un
                         *    TrackingReportUi.
                         *
                         * 4. Mostrar un estado de carga mientras se
                         *    realiza la consulta.
                         *
                         * 5. Mostrar un error si el folio no existe.
                         *
                         * 6. Manejar errores de conexión.
                         *
                         * 7. Utilizar el estatus recibido por backend
                         *    para construir el timeline.
                         *
                         * 8. Mostrar información de cierre únicamente
                         *    cuando corresponda.
                         */

                        selectedReportStatus =
                            getSimulatedStatus(
                                reportFolio
                            )
                    }
                )

            } else {

                val report =
                    createSimulatedReport(
                        folio = reportFolio,
                        status = selectedReportStatus!!
                    )

                TrackingResultContent(
                    report = report,

                    onEditFolioClick = {
                        selectedReportStatus = null
                    }
                )
            }
        }
    }
}


// =================================================================
// DATOS TEMPORALES PARA PROBAR TODOS LOS ESTADOS
// =================================================================

private fun getSimulatedStatus(
    folio: String
): ReportStatus {

    val normalizedFolio =
        folio
            .trim()
            .uppercase()

    return when (normalizedFolio) {

        "RIETI-REGISTRADO" ->
            ReportStatus.REGISTRADO

        "RIETI-REVISION" ->
            ReportStatus.EN_REVISION

        "RIETI-SEGUIMIENTO" ->
            ReportStatus.EN_SEGUIMIENTO

        "RIETI-CANALIZADO" ->
            ReportStatus.CANALIZADO

        "RIETI-CONCLUIDO" ->
            ReportStatus.CONCLUIDO

        "RIETI-ARCHIVADO" ->
            ReportStatus.ARCHIVADO

        "RIETI-CANCELADO" ->
            ReportStatus.CANCELADO

        "RIETI-REINCIDENTE" ->
            ReportStatus.REINCIDENTE

        else ->
            ReportStatus.EN_SEGUIMIENTO
    }
}


// =================================================================
// CREACIÓN TEMPORAL DEL REPORTE
//
// TODO(BACKEND):
// Eliminar esta función cuando TrackingReportUi se construya con
// los datos reales recibidos desde el API.
// =================================================================

private fun createSimulatedReport(
    folio: String,
    status: ReportStatus
): TrackingReportUi {

    val lastUpdate =
        when (status) {

            ReportStatus.REGISTRADO ->
                "28 de septiembre de 2026, 10:12 h"

            ReportStatus.EN_REVISION ->
                "29 de septiembre de 2026, 16:40 h"

            ReportStatus.EN_SEGUIMIENTO ->
                "03 de octubre de 2026, 14:25 h"

            ReportStatus.CANALIZADO ->
                "08 de octubre de 2026, 09:30 h"

            ReportStatus.CONCLUIDO ->
                "12 de octubre de 2026, 12:15 h"

            ReportStatus.ARCHIVADO ->
                "12 de octubre de 2026, 12:15 h"

            ReportStatus.CANCELADO ->
                "06 de octubre de 2026, 11:20 h"

            ReportStatus.REINCIDENTE ->
                "10 de octubre de 2026, 15:45 h"
        }


    return TrackingReportUi(
        folio = folio,

        status = status,

        lastUpdate = lastUpdate,

        registrationDate =
            "28 de septiembre de 2026",

        municipality =
            "Atizapán de Zaragoza",

        modality =
            "Con seguimiento",

        closingDate =
            if (
                status == ReportStatus.CONCLUIDO ||
                status == ReportStatus.ARCHIVADO
            ) {
                "12 de octubre de 2026"
            } else {
                null
            },

        resolution =
            if (status == ReportStatus.CONCLUIDO) {
                "La atención del reporte ha finalizado conforme al proceso correspondiente."
            } else {
                null
            }
    )
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


        TrackingInformationNotice(
            text =
                "Por seguridad, la consulta muestra únicamente información relacionada con el avance del reporte."
        )
    }
}


// =================================================================
// VISTA 2 - RESULTADO
// =================================================================

@Composable
private fun TrackingResultContent(
    report: TrackingReportUi,
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
                if (report.status == ReportStatus.CONCLUIDO) {
                    "Consulta el resultado general de tu reporte."
                } else {
                    "Consulta el estado general de tu reporte."
                }
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        TrackingCurrentFolio(
            folio = report.folio,

            onEditClick = onEditFolioClick
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


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
            text = report.folio,

            color = colors.pinkPrincipal,

            fontFamily = FontFamily.Serif,

            fontWeight = FontWeight.Bold,

            fontSize = 18.sp
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // =========================================================
        // TARJETA PRINCIPAL SEGÚN ESTADO
        // =========================================================

        when (report.status) {

            ReportStatus.CONCLUIDO -> {
                ConcludedStatusCard(
                    report = report
                )
            }

            ReportStatus.ARCHIVADO,
            ReportStatus.CANCELADO,
            ReportStatus.REINCIDENTE -> {
                SpecialStatusCard(
                    report = report
                )
            }

            else -> {
                CurrentStatusCard(
                    report = report
                )
            }
        }


        Spacer(
            modifier = Modifier.height(22.dp)
        )


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


        TrackingTimeline(
            report = report
        )


        // =========================================================
        // RESULTADO DE SEGUIMIENTO
        // Solo se muestra cuando el reporte está concluido.
        // =========================================================

        if (
            report.status == ReportStatus.CONCLUIDO &&
            report.resolution != null
        ) {

            Spacer(
                modifier = Modifier.height(24.dp)
            )


            TrackingResolution(
                resolution = report.resolution
            )
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


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


        ReportGeneralInformation(
            report = report
        )


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

                textStyle = TextStyle(
                    color = colors.titles,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),

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
// TARJETA DE ESTADO ACTIVO
// =================================================================

@Composable
private fun CurrentStatusCard(
    report: TrackingReportUi
) {

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
                    text =
                        getStatusLabel(
                            report.status
                        ),

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
                        "Última actualización\n${report.lastUpdate}",

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
// TARJETA DE REPORTE CONCLUIDO
// =================================================================

@Composable
private fun ConcludedStatusCard(
    report: TrackingReportUi
) {

    val colors = AppTheme.colors
    val teal = Color(0xFF20AFAE)

    Surface(
        modifier = Modifier.fillMaxWidth(),

        color =
            teal.copy(
                alpha = 0.075f
            ),

        shape = RoundedCornerShape(9.dp),

        border = BorderStroke(
            width = 1.dp,
            color = teal.copy(alpha = 0.22f)
        )
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 16.dp
            ),

            verticalAlignment =
                Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(teal),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Check,

                    contentDescription = null,

                    tint = Color.White,

                    modifier = Modifier.size(26.dp)
                )
            }


            Spacer(
                modifier = Modifier.width(14.dp)
            )


            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Reporte concluido",

                    color = colors.titles,

                    fontFamily = FontFamily.Serif,

                    fontWeight = FontWeight.Bold,

                    fontSize = 18.sp
                )


                Spacer(
                    modifier = Modifier.height(4.dp)
                )


                Text(
                    text =
                        "La atención correspondiente a este reporte ha finalizado.",

                    color =
                        colors.text.copy(
                            alpha = 0.72f
                        ),

                    fontSize = 11.sp,

                    lineHeight = 15.sp
                )


                if (report.closingDate != null) {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    Text(
                        text = "Fecha de conclusión",

                        color =
                            colors.text.copy(
                                alpha = 0.58f
                            ),

                        fontSize = 10.sp
                    )


                    Text(
                        text = report.closingDate,

                        color = colors.titles,

                        fontWeight = FontWeight.Bold,

                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}


// =================================================================
// TARJETA DE ESTADOS ESPECIALES
// =================================================================

@Composable
private fun SpecialStatusCard(
    report: TrackingReportUi
) {

    val colors = AppTheme.colors

    val message =
        when (report.status) {

            ReportStatus.ARCHIVADO ->
                "El seguimiento de este reporte ha sido archivado."

            ReportStatus.CANCELADO ->
                "El seguimiento de este reporte ha sido cancelado."

            ReportStatus.REINCIDENTE ->
                "El reporte ha sido identificado como reincidente."

            else ->
                ""
        }


    Surface(
        modifier = Modifier.fillMaxWidth(),

        color =
            colors.pinkPrincipal.copy(
                alpha = 0.055f
            ),

        shape = RoundedCornerShape(9.dp),

        border = BorderStroke(
            width = 1.dp,

            color =
                colors.pinkPrincipal.copy(
                    alpha = 0.18f
                )
        )
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 15.dp
            ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .border(
                        width = 1.5.dp,
                        color = colors.pinkPrincipal,
                        shape = CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Info,

                    contentDescription = null,

                    tint = colors.pinkPrincipal,

                    modifier = Modifier.size(22.dp)
                )
            }


            Spacer(
                modifier = Modifier.width(13.dp)
            )


            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text =
                        getStatusLabel(
                            report.status
                        ),

                    color = colors.titles,

                    fontFamily = FontFamily.Serif,

                    fontWeight = FontWeight.Bold,

                    fontSize = 17.sp
                )


                Spacer(
                    modifier = Modifier.height(3.dp)
                )


                Text(
                    text = message,

                    color =
                        colors.text.copy(
                            alpha = 0.72f
                        ),

                    fontSize = 11.sp,

                    lineHeight = 15.sp
                )


                Spacer(
                    modifier = Modifier.height(5.dp)
                )


                Text(
                    text =
                        "Última actualización: ${report.lastUpdate}",

                    color =
                        colors.text.copy(
                            alpha = 0.58f
                        ),

                    fontSize = 10.sp
                )
            }
        }
    }
}


// =================================================================
// TEXTO DEL ESTADO
// =================================================================

private fun getStatusLabel(
    status: ReportStatus
): String {

    return when (status) {

        ReportStatus.REGISTRADO ->
            "Registrado"

        ReportStatus.EN_REVISION ->
            "En revisión"

        ReportStatus.EN_SEGUIMIENTO ->
            "En seguimiento"

        ReportStatus.CANALIZADO ->
            "Canalizado"

        ReportStatus.CONCLUIDO ->
            "Concluido"

        ReportStatus.ARCHIVADO ->
            "Archivado"

        ReportStatus.CANCELADO ->
            "Cancelado"

        ReportStatus.REINCIDENTE ->
            "Reincidente"
    }
}


// =================================================================
// LÍNEA DE TIEMPO
// =================================================================

@Composable
private fun TrackingTimeline(
    report: TrackingReportUi
) {

    /*
     * ARCHIVADO, CANCELADO y REINCIDENTE no se agregan como pasos
     * adicionales del timeline.
     *
     * Son desenlaces/estados especiales y se representan mediante
     * la tarjeta principal.
     */

    TrackingTimelineItem(
        title = "Registrado",

        date =
            getTimelineDate(
                ReportStatus.REGISTRADO,
                report
            ),

        description =
            "Tu reporte ha sido recibido.",

        status =
            getTimelineStatus(
                step = ReportStatus.REGISTRADO,
                current = report.status
            ),

        showLine = true
    )


    TrackingTimelineItem(
        title = "En revisión",

        date =
            getTimelineDate(
                ReportStatus.EN_REVISION,
                report
            ),

        description =
            "El reporte está siendo revisado por el equipo correspondiente.",

        status =
            getTimelineStatus(
                step = ReportStatus.EN_REVISION,
                current = report.status
            ),

        showLine = true
    )


    TrackingTimelineItem(
        title = "En seguimiento",

        date =
            getTimelineDate(
                ReportStatus.EN_SEGUIMIENTO,
                report
            ),

        description =
            "El reporte se encuentra en seguimiento con la instancia responsable.",

        status =
            getTimelineStatus(
                step = ReportStatus.EN_SEGUIMIENTO,
                current = report.status
            ),

        showLine = true
    )


    TrackingTimelineItem(
        title = "Canalizado",

        date =
            getTimelineDate(
                ReportStatus.CANALIZADO,
                report
            ),

        description = "",

        status =
            getTimelineStatus(
                step = ReportStatus.CANALIZADO,
                current = report.status
            ),

        showLine = true
    )


    TrackingTimelineItem(
        title = "Concluido",

        date =
            getTimelineDate(
                ReportStatus.CONCLUIDO,
                report
            ),

        description = "",

        status =
            getTimelineStatus(
                step = ReportStatus.CONCLUIDO,
                current = report.status
            ),

        showLine = false
    )
}


// =================================================================
// CALCULAR ESTADO VISUAL DEL TIMELINE
// =================================================================

private fun getTimelineStatus(
    step: ReportStatus,
    current: ReportStatus
): TrackingStatus {

    val orderedStatuses =
        listOf(
            ReportStatus.REGISTRADO,
            ReportStatus.EN_REVISION,
            ReportStatus.EN_SEGUIMIENTO,
            ReportStatus.CANALIZADO,
            ReportStatus.CONCLUIDO
        )


    // =============================================================
    // ESTADOS ESPECIALES
    //
    // Para la simulación visual mantenemos únicamente como
    // completados los pasos que razonablemente ya ocurrieron.
    //
    // TODO(BACKEND):
    // El historial real deberá venir del servidor. No se deberá
    // inferir únicamente a partir del estado final.
    // =============================================================

    if (
        current == ReportStatus.ARCHIVADO ||
        current == ReportStatus.CANCELADO ||
        current == ReportStatus.REINCIDENTE
    ) {

        return when (step) {

            ReportStatus.REGISTRADO ->
                TrackingStatus.COMPLETED

            else ->
                TrackingStatus.PENDING
        }
    }


    val currentIndex =
        orderedStatuses.indexOf(current)

    val stepIndex =
        orderedStatuses.indexOf(step)


    return when {

        stepIndex < currentIndex ->
            TrackingStatus.COMPLETED

        stepIndex == currentIndex -> {

            if (current == ReportStatus.CONCLUIDO) {
                TrackingStatus.COMPLETED
            } else {
                TrackingStatus.CURRENT
            }
        }

        else ->
            TrackingStatus.PENDING
    }
}


// =================================================================
// FECHAS TEMPORALES DEL TIMELINE
// =================================================================

private fun getTimelineDate(
    step: ReportStatus,
    report: TrackingReportUi
): String {

    val visualStatus =
        getTimelineStatus(
            step = step,
            current = report.status
        )


    if (visualStatus == TrackingStatus.PENDING) {
        return "Pendiente"
    }


    return when (step) {

        ReportStatus.REGISTRADO ->
            "28 de septiembre de 2026, 10:12 h"

        ReportStatus.EN_REVISION ->
            "29 de septiembre de 2026, 16:40 h"

        ReportStatus.EN_SEGUIMIENTO ->
            "03 de octubre de 2026, 14:25 h"

        ReportStatus.CANALIZADO ->
            "08 de octubre de 2026, 09:30 h"

        ReportStatus.CONCLUIDO ->
            "12 de octubre de 2026, 12:15 h"

        else ->
            report.lastUpdate
    }
}


// =================================================================
// ELEMENTO DEL TIMELINE
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


            if (
                description.isNotBlank() &&
                status != TrackingStatus.PENDING
            ) {

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
// RESULTADO DEL SEGUIMIENTO
// =================================================================

@Composable
private fun TrackingResolution(
    resolution: String
) {

    val colors = AppTheme.colors
    val teal = Color(0xFF20AFAE)

    Text(
        text = "Resultado del seguimiento",

        color = colors.titles,

        fontFamily = FontFamily.Serif,

        fontWeight = FontWeight.Bold,

        fontSize = 18.sp
    )


    Spacer(
        modifier = Modifier.height(10.dp)
    )


    Surface(
        modifier = Modifier.fillMaxWidth(),

        color =
            teal.copy(
                alpha = 0.065f
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

            Icon(
                imageVector = Icons.Default.Check,

                contentDescription = null,

                tint = teal,

                modifier = Modifier.size(19.dp)
            )


            Spacer(
                modifier = Modifier.width(10.dp)
            )


            Text(
                text = resolution,

                color =
                    colors.text.copy(
                        alpha = 0.78f
                    ),

                fontFamily = FontFamily.Serif,

                fontSize = 11.sp,

                lineHeight = 16.sp,

                modifier = Modifier.weight(1f)
            )
        }
    }
}


// =================================================================
// INFORMACIÓN GENERAL
// =================================================================

@Composable
private fun ReportGeneralInformation(
    report: TrackingReportUi
) {

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
                value = report.modality
            )


            Spacer(
                modifier = Modifier.height(13.dp)
            )


            GeneralInformationRow(
                icon = Icons.Default.CalendarMonth,
                label = "Fecha de registro",
                value = report.registrationDate
            )


            Spacer(
                modifier = Modifier.height(13.dp)
            )


            GeneralInformationRow(
                icon = Icons.Default.LocationOn,
                label = "Municipio",
                value = report.municipality
            )


            if (report.closingDate != null) {

                Spacer(
                    modifier = Modifier.height(13.dp)
                )


                GeneralInformationRow(
                    icon = Icons.Default.Check,
                    label = "Fecha de cierre",
                    value = report.closingDate
                )
            }
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