package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.TopBar


@Composable
fun ReportScreen(
    onHomeClick: () -> Unit,
    onReportClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMenuClick: () -> Unit,
    onCurrentLocationClick: () -> Unit,
    onSelectFilesClick: () -> Unit,
    onSendReportClick: () -> Unit
) {

    // ****************************************
    // ESTADOS TEMPORALES
    //
    // Por ahora estan aquí porque solo estamos haciendo front.
    // Después probablemente pasarán a un ReportViewModel.

    var numberOfChildren by rememberSaveable {
        mutableStateOf("")
    }

    var childrenAges by rememberSaveable {
        mutableStateOf("")
    }

    var workType by rememberSaveable {
        mutableStateOf("")
    }

    var workSchedule by rememberSaveable {
        mutableStateOf("")
    }

    var details by rememberSaveable {
        mutableStateOf("")
    }

    var fullAddress by rememberSaveable {
        mutableStateOf("")
    }

    var neighborhood by rememberSaveable {
        mutableStateOf("")
    }

    var postalCode by rememberSaveable {
        mutableStateOf("")
    }

    var municipality by rememberSaveable {
        mutableStateOf("")
    }


    val colors = AppTheme.colors


    Scaffold(

        // ********************************************
        // BARRA SUPERIOR REUTILIZABLE

        topBar = {

            TopBar(
                onMenuClick = onMenuClick
            )
        },


        //************************************
        // BARRA INFERIOR REUTILIZABLE


        bottomBar = {

            BottomBar(
                onHomeClick = onHomeClick,
                onReportClick = onReportClick,
                onSettingsClick = onSettingsClick
            )
        },


        containerColor = colors.surface

    ) { innerPadding ->


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            // ********************************
            // TÍTULO PRINCIPAL
            

            item {

                ReportMainTitle()
            }


            // =================================================
            // DETALLES DEL CASO

            item {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                ) {

                    SectionTitle(
                        title = "Detalles del caso"
                    )


                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )


                    // Primera fila

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


                    // Segunda fila

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


                    // Espacio grande de detalles

                    ReportFieldWithLabel(
                        label = "Detalles:",
                        value = details,
                        placeholder = "Describa detalladamente lo que observó ...",

                        onValueChange = {
                            details = it
                        },

                        minHeight = 125.dp,
                        singleLine = false,

                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }


            // =================================================
            // UBICACIÓN DEL CASO


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
                        title = "Ubicación del caso"
                    )


                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )


                    // Botón ubicación actual

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {

                        Button(

                            onClick = {
                                onCurrentLocationClick()
                            },

                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.surface,
                                contentColor = colors.bluePrincipal
                            ),

                            border = BorderStroke(
                                width = 1.dp,
                                color = colors.bluePrincipal
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
                        modifier = Modifier.height(5.dp)
                    )


                    // Por ahora solamente placeholder

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
                            label = "Código Postal",
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

                        modifier = Modifier.fillMaxWidth(0.58f)
                    )
                }
            }


            // =================================================
            // EVIDENCIA FOTOGRÁFICA


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
                        title = "Evidencia"
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
                        modifier = Modifier.height(30.dp)
                    )
                }
            }
        }
    }
}


// =================================================================
// TÍTULO "REALIZA TU REPORTE"


@Composable
private fun ReportMainTitle() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(
                AppTheme.colors.pinkPrincipal
            ),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "Realiza tu reporte",
            color = AppTheme.colors.text,
            fontSize = 30.sp,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center
        )
    }
}


// ======================================
// TÍTULOS DE SECCIÓN


@Composable
private fun SectionTitle(
    title: String
) {

    Column(
        modifier = Modifier.width(
            IntrinsicSize.Max
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

        onValueChange = {
            onValueChange(it)
        },

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
            .heightIn(
                min = minHeight
            )
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
                tint = colors.bluePrincipal
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            Text(
                text = "Mapa",
                color = colors.text,
                fontSize = 18.sp
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


// =================================================================
// SELECTOR DE ARCHIVOS


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


        // **********************
        // Elegir archivos


        Box(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxSize(),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Elegir archivos",
                color = colors.bluePrincipal,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp
            )
        }


        //*************************
        // Línea divisoria


        Box(
            modifier = Modifier
                .width(1.dp)
                .height(34.dp)
                .background(
                    colors.bluePrincipal
                )
        )


        //***********************
        // Nombre de archivo


        Box(
            modifier = Modifier
                .weight(1.8f)
                .fillMaxSize(),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Ningún archivo seleccionado...",
                color = colors.bluePrincipal,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp
            )
        }
    }
}


// *********************************
// BOTÓN ENVIAR REPORTE


@Composable
private fun SendReportButton(
    onClick: () -> Unit
) {

    Button(

        onClick = {
            onClick()
        },

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



@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ReportScreenLightPreview() {

    RIETITheme(
        darkTheme = false
    ) {

        ReportScreen(

            onHomeClick = {},

            onReportClick = {},

            onSettingsClick = {},

            onMenuClick = {},

            onCurrentLocationClick = {},

            onSelectFilesClick = {},

            onSendReportClick = {}
        )
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ReportScreenDarkPreview() {

    RIETITheme(
        darkTheme = true
    ) {

        ReportScreen(

            onHomeClick = {},

            onReportClick = {},

            onSettingsClick = {},

            onMenuClick = {},

            onCurrentLocationClick = {},

            onSelectFilesClick = {},

            onSendReportClick = {}
        )
    }
}

//Borrar lo que sigue después de implementar esas pantallas :DDDD
@Composable
fun HomeScreen(
    onReportClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    Text(
        text = "Home"
    )
}

@Composable
fun SettingsScreen(
    onHomeClick: () -> Unit,
    onReportClick: () -> Unit
) {

    Text(
        text = "Settings"
    )
}

