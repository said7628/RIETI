package com.example.rieti.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Mapa utilizado para seleccionar la ubicación asociada a un reporte.
 *
 * El punto seleccionado representa la ubicación DEL REPORTE,
 * no necesariamente la ubicación actual del dispositivo.
 *
 * El usuario puede:
 * - Tocar cualquier punto del mapa para seleccionarlo.
 * - Arrastrar el marcador para ajustar la ubicación.
 *
 * TODO(BACKEND):
 * Las coordenadas seleccionadas se almacenarán en ReportUiState
 * y posteriormente se enviarán al backend junto con el reporte.
 */
@Composable
fun ReportLocationMap(
    selectedLocation: LatLng,
    onLocationSelected: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {

    /*
     * Posición inicial de la cámara.
     *
     * selectedLocation provendrá posteriormente del ReportViewModel.
     */
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            selectedLocation,
            15f
        )
    }

    /*
     * Estado del marcador.
     */
    val markerState = remember {
        MarkerState(position = selectedLocation)
    }

    /*
     * Si selectedLocation cambia desde fuera del mapa
     * (por ejemplo, al usar la ubicación actual),
     * actualizamos marcador y cámara.
     */
    LaunchedEffect(selectedLocation) {

        markerState.position = selectedLocation

        cameraPositionState.position =
            CameraPosition.fromLatLngZoom(
                selectedLocation,
                15f
            )
    }

    /*
     * Configuración visual del mapa.
     *
     * Por ahora NO mostramos el punto azul de ubicación del dispositivo.
     * Ese punto y la ubicación del reporte son conceptos diferentes.
     */
    val mapProperties = remember {
        MapProperties(
            isMyLocationEnabled = false
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = true
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(225.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {

        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,

            /*
             * El usuario toca cualquier punto del mapa.
             */
            onMapClick = { latLng ->

                markerState.position = latLng

                onLocationSelected(latLng)
            }
        ) {

            Marker(
                state = markerState,
                title = "Ubicación del reporte",
                snippet = "Puedes mover este marcador",
            )
        }
    }
}