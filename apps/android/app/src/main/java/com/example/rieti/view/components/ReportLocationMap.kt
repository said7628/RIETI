package com.example.rieti.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
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
 * Muestra una vista previa del lugar. Al tocarla abre un mapa a pantalla completa.
 * Solo actualiza el reporte cuando la persona confirma el punto elegido.
 * Autor: Said Valseca Figueroa.
 */
@Composable
fun ReportLocationMap(
    selectedLocation: LatLng,
    onLocationSelected: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    var editing by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val previewCamera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 16f)
    }

    LaunchedEffect(selectedLocation) {
        previewCamera.position = CameraPosition.fromLatLngZoom(selectedLocation, 16f)
    }

    // La vista previa no se mueve ni cambia coordenadas. Toda la superficie abre el mapa grande.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = previewCamera,
            uiSettings = MapUiSettings(
                scrollGesturesEnabled = false,
                zoomGesturesEnabled = false,
                zoomControlsEnabled = false,
                rotationGesturesEnabled = false,
                tiltGesturesEnabled = false
            )
        ) {
            Marker(
                state = MarkerState(position = selectedLocation),
                title = "Lugar del reporte"
            )
        }

        // La capa transparente captura el toque incluso sobre los controles del mapa.
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    role = Role.Button,
                    onClickLabel = "Abrir el mapa a pantalla completa"
                ) {
                    focusManager.clearFocus()
                    editing = true
                }
        ) {
            Text(
                text = "Toca el mapa para elegir el lugar",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD0003554))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }

    if (editing) {
        Dialog(
            onDismissRequest = { editing = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val marker = remember(selectedLocation) { MarkerState(position = selectedLocation) }
            val camera = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(selectedLocation, 16f)
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = AppTheme.colors.surface
            ) {
                Column {
                    Text(
                        text = "Selecciona el lugar exacto",
                        color = AppTheme.colors.pinkPrincipal,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
                    )
                    Text(
                        text = "Mueve el mapa y toca el sitio donde ocurrió. También puedes arrastrar el marcador.",
                        color = AppTheme.colors.text,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )

                    GoogleMap(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        cameraPositionState = camera,
                        uiSettings = MapUiSettings(
                            scrollGesturesEnabled = true,
                            zoomGesturesEnabled = true,
                            zoomControlsEnabled = true,
                            compassEnabled = true
                        ),
                        onMapClick = { point -> marker.position = point }
                    ) {
                        Marker(
                            state = marker,
                            draggable = true,
                            title = "Ubicación elegida"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { editing = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) { Text("Cancelar") }
                        Button(
                            onClick = {
                                onLocationSelected(marker.position)
                                editing = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.pinkPrincipal)
                        ) { Text("Confirmar punto") }
                    }
                }
            }
        }
    }
}
