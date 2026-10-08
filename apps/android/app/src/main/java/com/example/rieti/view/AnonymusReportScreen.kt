package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.rieti.ui.theme.RIETITheme


// =================================================================
// REPORTE ANÓNIMO
// =================================================================

@Composable
fun AnonymousReportScreen(
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCurrentLocationClick: () -> Unit,
    onSelectFilesClick: () -> Unit,
    onSendReportClick: () -> Unit
) {

    /*
     * El reporte anónimo utiliza el mismo formulario
     * y flujo del reporte con seguimiento.
     *
     * La diferencia se controla mediante ReportMode.ANONYMOUS.
     *
     * TODO(BACKEND):
     *
     * Cuando se conecte con el backend, la modalidad deberá
     * enviarse junto con los datos del reporte para que el
     * servicio aplique las reglas correspondientes.
     */

    ReportFormScreen(

        mode = ReportMode.ANONYMOUS,

        onHomeClick = onHomeClick,

        onAboutUsClick = onAboutUsClick,

        onSettingsClick = onSettingsClick,

        onCurrentLocationClick =
            onCurrentLocationClick,

        onSelectFilesClick =
            onSelectFilesClick,

        onSendReportClick =
            onSendReportClick
    )
}


// =================================================================
// PREVIEW LIGHT
// =================================================================

@Preview(
    name = "Reporte anónimo - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AnonymousReportScreenLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        AnonymousReportScreen(

            onHomeClick = {},

            onAboutUsClick = {},

            onSettingsClick = {},

            onCurrentLocationClick = {},

            onSelectFilesClick = {},

            onSendReportClick = {}
        )
    }
}


// =================================================================
// PREVIEW DARK
// =================================================================

@Preview(
    name = "Reporte anónimo - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AnonymousReportScreenDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        AnonymousReportScreen(

            onHomeClick = {},

            onAboutUsClick = {},

            onSettingsClick = {},

            onCurrentLocationClick = {},

            onSelectFilesClick = {},

            onSendReportClick = {}
        )
    }
}