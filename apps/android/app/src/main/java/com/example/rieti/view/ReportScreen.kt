package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme


@Composable
fun ReportScreen(
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMenuClick: () -> Unit,
    onCurrentLocationClick: () -> Unit,
    onSelectFilesClick: () -> Unit,
    onSendReportClick: () -> Unit
) {

    /*
     * Por ahora guardamos el texto aquí solamente
     * para poder probar visualmente los campos.
     *
     * Después estos estados pasarían al ViewModel.
     */

    var numberOfChildren by rememberSaveable { mutableStateOf("") }
    var childrenAges by rememberSaveable { mutableStateOf("") }
    var workType by rememberSaveable { mutableStateOf("") }
    var workSchedule by rememberSaveable { mutableStateOf("") }
    var details by rememberSaveable { mutableStateOf("") }

    var fullAddress by rememberSaveable { mutableStateOf("") }
    var neighborhood by rememberSaveable { mutableStateOf("") }
    var postalCode by rememberSaveable { mutableStateOf("") }
    var municipality by rememberSaveable { mutableStateOf("") }

    val colors = AppTheme.colors

    Scaffold(
        containerColor = colors.surface,

        topBar = {
            ReportTopBar(
                onMenuClick = onMenuClick
            )
        },

        bottomBar = {
            RietiBottomBar(
                onHomeClick = onHomeClick,
                onSettingsClick = onSettingsClick
            )
        }

    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /*
             * DETALLES DEL CASO
             */

            item {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                ) {

                    SectionTitle(
                        title = "Detalles del Caso"
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(28.dp)
                    ) {

                        ReportFieldWithLabel(
                            label = "Número de niños",
                            value = numberOfChildren,
                            placeholder = "1",
                            onValueChange = {
                                numberOfChildren = it
                            },
                            modifier = Modifier.weight(1f)
                        )

                        ReportFieldWithLabel(
                            label = "Edades de los niños",
                            value = childrenAges,
                            placeholder = "Ej: 6, 12, 16 años",
                            onValueChange = {
                                childrenAges = it
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(28.dp)
                    ) {

                        ReportFieldWithLabel(
                            label = "Tipo de Trabajo",
                            value = workType,
                            placeholder = "1",
                            onValueChange = {
                                workType = it
                            },
                            modifier = Modifier.weight(1f)
                        )

                        ReportFieldWithLabel(
                            label = "Horario de Trabajo",
                            value = workSchedule,
                            placeholder = "Ej: 8:00 am - 6:00 pm",
                            onValueChange = {
                                workSchedule = it
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    ReportFieldWithLabel(
                        label = "Detalles:",
                        value = details,
                        placeholder = "Describa detalladamente lo que observo ...",
                        onValueChange = {
                            details = it
                        },
                        minHeight = 125.dp,
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }


            /*
             * UBICACIÓN DEL CASO
             */

            item {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                ) {

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    SectionTitle(
                        title = "Ubicación del Caso"
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {

                        Button(
                            onClick = onCurrentLocationClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.surface,
                                contentColor = colors.bluePrincipal
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                colors.bluePrincipal
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {

                            Text(
                                text = "Ubicación actual",
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    MapPlaceholder()

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    ReportFieldWithLabel(
                        label = "Dirección completa",
                        value = fullAddress,
                        placeholder = "",
                        onValueChange = {
                            fullAddress = it
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(28.dp)
                    ) {

                        ReportFieldWithLabel(
                            label = "Colonia",
                            value = neighborhood,
                            placeholder = "",
                            onValueChange = {
                                neighborhood = it
                            },
                            modifier = Modifier.weight(1.7f)
                        )

                        ReportFieldWithLabel(
                            label = "Codigo Postal",
                            value = postalCode,
                            placeholder = "",
                            onValueChange = {
                                postalCode = it
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    ReportFieldWithLabel(
                        label = "Municipio",
                        value = municipality,
                        placeholder = "",
                        onValueChange = {
                            municipality = it
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.58f)
                    )
                }
            }


            /*
             * EVIDENCIA FOTOGRÁFICA
             */

            item {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                ) {

                    Spacer(
                        modifier = Modifier.height(25.dp)
                    )

                    SectionTitle(
                        title = "Evidencia Fotografica"
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    EvidenceSelector(
                        onClick = onSelectFilesClick
                    )

                    Spacer(
                        modifier = Modifier.height(45.dp)
                    )

                    SendReportButton(
                        onClick = onSendReportClick
                    )

                    Spacer(
                        modifier = Modifier.height(40.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun ReportFieldWithLabel(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 42.dp,
    singleLine: Boolean = true
) {

    Column(
        modifier = modifier
    ) {

        Text(
            text = label,
            color = AppTheme.colors.text,
            fontSize = 16.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        RietiTextField(
            value = value,
            placeholder = placeholder,
            onValueChange = onValueChange,
            minHeight = minHeight,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SectionTitle(
    title: String
) {

    Column(
        modifier = Modifier.width(
            IntrinsicSize.Min
        )
    ) {

        Text(
            text = title,
            color = AppTheme.colors.titles,
            fontFamily = FontFamily.Serif,
            fontSize = 26.sp,
            maxLines = 1
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = AppTheme.colors.titles
        )
    }
}

@Composable
private fun MapPlaceholder() {

    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .border(
                width = 1.dp,
                color = colors.blueDetails
            )
            .background(
                colors.blueDetails.copy(
                    alpha = 0.12f
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = colors.bluePrincipal,
                modifier = Modifier.size(40.dp)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Mapa",
                color = colors.text
            )

            Text(
                text = "Se conectará posteriormente",
                color = colors.text.copy(
                    alpha = 0.6f
                ),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun EvidenceSelector(
    onClick: () -> Unit
) {

    val colors = AppTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .border(
                width = 1.dp,
                color = colors.bluePrincipal,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Elegir archivos",
                color = colors.bluePrincipal,
                fontFamily = FontFamily.Serif
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(34.dp)
                .background(
                    colors.bluePrincipal
                )
        )

        Box(
            modifier = Modifier
                .weight(1.8f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Ningun archivo seleccionado...",
                color = colors.bluePrincipal,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun SendReportButton(
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp),

        shape = RoundedCornerShape(10.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.colors.blueBoxes,
            contentColor = AppTheme.colors.textTyping
        )
    ) {

        Text(
            text = "Enviar Reporte",
            fontFamily = FontFamily.Serif,
            fontSize = 36.sp,
            textAlign = TextAlign.Center
        )
    }
}

//Esto borrarlo, es la parte superior de la pantalla, lo tenemos que cambiar por la imagen

@Composable
private fun ReportTopBar(
    onMenuClick: () -> Unit
) {

    val colors = AppTheme.colors

    Column {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(105.dp)
        ) {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {

                // Fondo
                drawRect(
                    color = colors.surface
                )

                // Zona rosa derecha
                drawRect(
                    color = colors.pinkPrincipal,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        size.width * 0.70f,
                        0f
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        size.width * 0.30f,
                        size.height
                    )
                )

                // Franja diagonal clara
                val path = Path().apply {

                    moveTo(
                        size.width * 0.56f,
                        0f
                    )

                    lineTo(
                        size.width * 0.64f,
                        0f
                    )

                    lineTo(
                        size.width * 0.73f,
                        size.height
                    )

                    lineTo(
                        size.width * 0.64f,
                        size.height
                    )

                    close()
                }

                drawPath(
                    path = path,
                    color = colors.subTitles
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 18.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                /*
                 * Cuando tengan el logo real,
                 * sustituimos este Text por Image(...)
                 */

                Text(
                    text = "RIETI",
                    color = colors.text,
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onMenuClick
                ) {

                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = colors.text,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .background(
                    colors.pinkPrincipal
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Realiza tu reporte",
                color = colors.text,
                fontFamily = FontFamily.Serif,
                fontSize = 31.sp
            )
        }
    }
}



@Composable
private fun RietiBottomBar(
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {

        /*
         * Barra rosa
         */

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(70.dp)
                .background(
                    colors.pinkPrincipal
                )
        )

        /*
         * Inicio
         */

        IconButton(
            onClick = onHomeClick,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 34.dp,
                    bottom = 10.dp
                )
                .size(55.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Inicio",
                tint = colors.onPrimary,
                modifier = Modifier.size(48.dp)
            )
        }

        /*
         * Reporte seleccionado
         */

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(82.dp)
                .clip(CircleShape)
                .background(
                    colors.subTitles
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Reporte",
                tint = colors.titles,
                modifier = Modifier.size(60.dp)
            )
        }

        /*
         * Ajustes
         */

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 34.dp,
                    bottom = 10.dp
                )
                .size(55.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Ajustes",
                tint = colors.onPrimary,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

//eliminar TODO

@Composable
fun HomeScreen(
    onReportClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Text("Home")
}

@Composable
fun SettingsScreen(
    onHomeClick: () -> Unit,
    onReportClick: () -> Unit
) {
    Text("Settings")
}

@Composable
private fun RietiTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 42.dp,
    singleLine: Boolean = true
) {
    val colors = AppTheme.colors

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,

        textStyle = TextStyle(
            color = colors.textTyping,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        ),

        cursorBrush = SolidColor(
            colors.textTyping
        ),

        modifier = modifier
            .heightIn(min = minHeight)
            .clip(
                RoundedCornerShape(10.dp)
            )
            .background(
                colors.blueBoxes
            ),

        decorationBox = { innerTextField ->

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 12.dp
                    ),
                contentAlignment =
                    if (singleLine) {
                        Alignment.CenterStart
                    } else {
                        Alignment.TopStart
                    }
            ) {

                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = colors.textTyping,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                innerTextField()
            }
        }
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ReportScreenDarkPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        ReportScreen(
            onHomeClick = {},
            onSettingsClick = {},
            onMenuClick = {},
            onCurrentLocationClick = {},
            onSelectFilesClick = {},
            onSendReportClick = {}
        )
    }
}