package com.example.rieti.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.rieti.ui.theme.AppTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Permite elegir un punto sin modificar la ubicacion hasta pulsar Confirmar.
 * Said Valseca Figueroa
 */
@Composable
fun ReportLocationMap(
    selectedLocation: LatLng,
    onLocationSelected: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    var editing by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth().height(225.dp)
        .clip(RoundedCornerShape(18.dp)).clickable { editing = true }) {
        val previewCamera = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(selectedLocation, 15f)
        }
        LaunchedEffect(selectedLocation) {
            previewCamera.position = CameraPosition.fromLatLngZoom(selectedLocation, 15f)
        }
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = previewCamera,
            uiSettings = MapUiSettings(scrollGesturesEnabled = false, zoomGesturesEnabled = false,
                rotationGesturesEnabled = false, tiltGesturesEnabled = false, zoomControlsEnabled = false)
        ) {
            Marker(state = MarkerState(position = selectedLocation), title = "Punto del reporte")
        }
        Text("Modificar ubicación en mapa", color = Color.White,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .background(Color(0xCCB90066)).clickable { editing = true }
                .padding(12.dp))
    }
    if (editing) {
        Dialog(
            onDismissRequest = { editing = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            var candidate by remember(selectedLocation) { mutableStateOf(selectedLocation) }
            val camera = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(selectedLocation, 16f)
            }
            val marker = remember { MarkerState(position = selectedLocation) }
            LaunchedEffect(candidate) { marker.position = candidate }
            Surface(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                color = AppTheme.colors.surface
            ) {
                Column {
                    Text("Selecciona el lugar exacto", modifier = Modifier.padding(20.dp),
                        color = AppTheme.colors.pinkPrincipal)
                    Text("Desliza el mapa, toca el lugar o mueve el marcador. Confirma para guardar.",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        color = AppTheme.colors.text)
                    GoogleMap(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        cameraPositionState = camera,
                        uiSettings = MapUiSettings(scrollGesturesEnabled = true,
                            zoomGesturesEnabled = true, zoomControlsEnabled = true,
                            compassEnabled = true),
                        onMapClick = { candidate = it }
                    ) {
                        Marker(state = marker, draggable = true,
                            onClick = { false },
                            title = "Ubicación elegida")
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { editing = false }, modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                            Text("Cancelar")
                        }
                        Button(onClick = {
                            onLocationSelected(marker.position)
                            editing = false
                        }, modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.pinkPrincipal)) {
                            Text("Confirmar punto")
                        }
                    }
                }
            }
        }
    }
}
