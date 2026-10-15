
package com.example.rieti.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Componente de mapa interactivo utilizado en RIETI
 * para visualizar y seleccionar una ubicación geográfica.
 *
 * Integra Google Maps mediante Jetpack Compose
 * y permite al usuario desplazar el mapa para
 * seleccionar una posición.
 *
 * La cámara se inicializa utilizando las coordenadas
 * recibidas en currentLatLng y un nivel de zoom de 15.
 *
 * Cuando cambia la ubicación proporcionada,
 * la cámara se desplaza mediante una animación
 * con duración de 800 milisegundos.
 *
 * Si el usuario mueve el mapa mediante un gesto,
 * al finalizar el movimiento se comunica
 * la posición central mediante onPositionSelected.
 *
 * Presenta un ícono de ubicación rojo y fijo
 * en el centro del mapa como referencia visual
 * para la selección de coordenadas.
 *
 * Los controles de zoom integrados de Google Maps
 * permanecen deshabilitados.
 *
 * @param currentLatLng Coordenadas geográficas
 * utilizadas para establecer y actualizar
 * la posición central de la cámara.
 * @param onPositionSelected Función que recibe
 * las coordenadas seleccionadas cuando finaliza
 * un movimiento de cámara iniciado por un gesto.
 * @param modifier Modificador opcional utilizado
 * para personalizar el contenedor del mapa.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
@Composable
fun MapScreen(
    currentLatLng: LatLng,
    onPositionSelected: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
    }

    LaunchedEffect(currentLatLng) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLng(currentLatLng),
            durationMs = 800
        )
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving &&
            cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            onPositionSelected(cameraPositionState.position.target)
        }
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        )

        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Centro del mapa",
            tint = Color.Red,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Center)
        )
    }
}
