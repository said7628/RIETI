
package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.rieti.ui.theme.RIETITheme


// =================================================================
// REPORTE ANÓNIMO
// =================================================================

/**
 * Presenta la pantalla de reporte anónimo de RIETI.
 *
 * Reutiliza el componente ReportFormScreen para mostrar
 * el formulario de registro de posibles situaciones
 * de trabajo infantil.
 *
 * Establece la modalidad ReportMode.ANONYMOUS
 * para identificar el reporte como anónimo.
 *
 * Delega al formulario general las funciones relacionadas
 * con la navegación, la consulta de ubicación actual,
 * la selección de archivos y el envío del reporte.
 *
 * Esta función no implementa directamente la lógica
 * de captura o envío de información, sino que proporciona
 * la configuración correspondiente a la modalidad anónima.
 *
 * @param onHomeClick Función ejecutada cuando el usuario
 * selecciona la opción de regresar al inicio.
 * @param onAboutUsClick Función ejecutada cuando el usuario
 * selecciona la sección "Quiénes somos".
 * @param onSettingsClick Función ejecutada cuando el usuario
 * selecciona la sección de configuración.
 * @param onCurrentLocationClick Función ejecutada cuando
 * se solicita utilizar la ubicación actual del dispositivo.
 * @param onSelectFilesClick Función ejecutada cuando
 * se solicita seleccionar archivos como evidencias.
 * @param onSendReportClick Función que recibe una cadena
 * de texto al ejecutarse la acción de envío del reporte.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
@Composable
fun AnonymousReportScreen(
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCurrentLocationClick: () -> Unit,
    onSelectFilesClick: () -> Unit,
    onSendReportClick: (String) -> Unit
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

/**
 * Genera una vista previa de AnonymousReportScreen
 * utilizando el tema claro de RIETI.
 *
 * Configura RIETITheme con darkTheme en false
 * para visualizar la presentación del formulario
 * de reporte anónimo en modo claro desde Android Studio.
 *
 * Las funciones de navegación, ubicación, selección
 * de archivos y envío se representan mediante
 * callbacks vacíos para fines de previsualización.
 */
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

/**
 * Genera una vista previa de AnonymousReportScreen
 * utilizando el tema oscuro de RIETI.
 *
 * Configura RIETITheme con darkTheme en true
 * y establece el modo nocturno del sistema mediante
 * Configuration.UI_MODE_NIGHT_YES.
 *
 * Permite revisar la apariencia del formulario
 * de reporte anónimo en modo oscuro desde Android Studio.
 *
 * Las funciones recibidas como parámetros utilizan
 * callbacks vacíos exclusivamente para la vista previa.
 */
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
