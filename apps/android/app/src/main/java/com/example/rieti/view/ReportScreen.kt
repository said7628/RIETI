
package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rieti.ui.theme.AppTheme
import com.example.rieti.ui.theme.RIETITheme
import com.example.rieti.view.components.BottomBar
import com.example.rieti.view.components.ReportLocationMap
import com.example.rieti.viewmodel.ReportViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

// =================================================================
// MODALIDAD DEL REPORTE
// =================================================================

enum class ReportMode {
    TRACKING,
    ANONYMOUS
}

// =================================================================
// REPORTE CON SEGUIMIENTO
// =================================================================

@Composable
fun ReportScreen(
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCurrentLocationClick: () -> Unit,
    onSelectFilesClick: () -> Unit,
    onSendReportClick: () -> Unit
) {
    ReportFormScreen(
        mode = ReportMode.TRACKING,
        onHomeClick = onHomeClick,
        onAboutUsClick = onAboutUsClick,
        onSettingsClick = onSettingsClick,
        onCurrentLocationClick = onCurrentLocationClick,
        onSelectFilesClick = onSelectFilesClick,
        onSendReportClick = onSendReportClick
    )
}

// =================================================================
// FORMULARIO COMPARTIDO
// =================================================================

@Composable
fun ReportFormScreen(
    mode: ReportMode,
    onHomeClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCurrentLocationClick: () -> Unit,
    onSelectFilesClick: () -> Unit,
    onSendReportClick: () -> Unit,
    draftId: String? = null
) {

    val isPreview = LocalInspectionMode.current

    val reportViewModel: ReportViewModel = viewModel()

    val reportState by
    reportViewModel.uiState.collectAsState()

    val isDraftLoaded by
    reportViewModel.isDraftLoaded.collectAsState()

    val draftError by
    reportViewModel.draftError.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val colors = AppTheme.colors

    // =============================================================
    // APERTURA DEL BORRADOR
    // =============================================================

    LaunchedEffect(mode, draftId) {

        if (!isPreview) {

            reportViewModel.openDraft(
                mode = mode.name,
                draftId = draftId
            )
        }
    }

    // =============================================================
    // ESTADO DEL FORMULARIO
    // =============================================================

    val currentStep = reportState.currentStep

    val email = reportState.email
    val confirmEmail = reportState.confirmEmail

    val numberOfChildren = reportState.numberOfChildren
    val childrenAges = reportState.childrenAges

    val workType = reportState.workType
    val workSchedule = reportState.workSchedule
    val details = reportState.details

    val fullAddress = reportState.fullAddress
    val neighborhood = reportState.neighborhood
    val postalCode = reportState.postalCode
    val municipality = reportState.municipality

    val selectedLocation = reportState.selectedLocation

    // =============================================================
    // GUARDADO SEGURO ANTES DE NAVEGAR
    // =============================================================

    /**
     * Guarda el borrador antes de abandonar el formulario.
     *
     * Si ocurre un error:
     * - No ejecuta la navegación.
     * - Mantiene el formulario abierto.
     * - Permite mostrar el error del ViewModel.
     */
    val saveAndNavigate: (() -> Unit) -> Unit = { navigate ->

        if (isPreview) {

            navigate()

        } else if (isDraftLoaded) {

            coroutineScope.launch {

                try {

                    reportViewModel.saveDraftNow()

                    navigate()

                } catch (e: CancellationException) {

                    throw e

                } catch (e: Exception) {

                    // El ViewModel actualiza draftError.
                    // No salimos para evitar perder cambios.
                }
            }
        }
    }

    // =============================================================
    // PANTALLA PRINCIPAL
    // =============================================================

    Scaffold(
        containerColor = colors.surface,

        bottomBar = {

            BottomBar(

                onHomeClick = {
                    saveAndNavigate(onHomeClick)
                },

                onAboutUsClick = {
                    saveAndNavigate(onAboutUsClick)
                },

                onSettingsClick = {
                    saveAndNavigate(onSettingsClick)
                }
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.surface)
        ) {

            // =====================================================
            // ENCABEZADO
            // =====================================================

            ReportHeader(
                onBackClick = {

                    if (!isDraftLoaded && !isPreview) {

                        // Si la recuperación falló, regresar
                        // no debe intentar guardar nada.
                        if (draftError != null) {
                            onHomeClick()
                        }

                    } else if (currentStep > 1) {

                        reportViewModel.onStepChange(
                            currentStep - 1
                        )

                    } else {

                        saveAndNavigate(onHomeClick)
                    }
                }
            )

            // =====================================================
            // INDICADOR DE PROGRESO
            // =====================================================

            if (isDraftLoaded || isPreview) {

                ReportProgressIndicator(
                    currentStep = currentStep
                )
            }

            // =====================================================
            // CONTENIDO
            // =====================================================

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {

                // =================================================
                // BORRADOR NO DISPONIBLE
                // =================================================

                if (!isDraftLoaded && !isPreview) {

                    if (draftError != null) {

                        // =========================================
                        // ERROR DE RECUPERACIÓN
                        // =========================================

                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .padding(24.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally,

                            verticalArrangement =
                                Arrangement.spacedBy(16.dp)
                        ) {

                            Text(
                                text =
                                    "No fue posible recuperar el reporte",

                                color = colors.titles,

                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp,

                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = draftError
                                    ?: "Ocurrió un error al recuperar la información.",

                                color = colors.text,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,

                                textAlign = TextAlign.Center
                            )

                            Text(
                                text =
                                    "El borrador original no ha sido eliminado.",

                                color = colors.text.copy(
                                    alpha = 0.70f
                                ),

                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = onHomeClick,

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),

                                shape = RoundedCornerShape(10.dp),

                                colors = ButtonDefaults.buttonColors(
                                    containerColor =
                                        colors.pinkPrincipal,

                                    contentColor = Color.White
                                )
                            ) {

                                Text(
                                    text = "Volver al inicio",

                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                    } else {

                        // =========================================
                        // RECUPERACIÓN EN PROCESO
                        // =========================================

                        Text(
                            text = "Recuperando reporte...",

                            color = colors.text,

                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                    }

                } else {

                    // =================================================
                    // FORMULARIO DISPONIBLE
                    // =================================================

                    Column {

                        // =========================================
                        // MENSAJES DE GUARDADO Y UBICACIÓN
                        // =========================================

                        if (draftError != null) {

                            Text(
                                text = draftError ?: "",

                                color = colors.pinkPrincipal,

                                fontSize = 12.sp,

                                modifier = Modifier.padding(
                                    horizontal = 22.dp,
                                    vertical = 8.dp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {

                            when (currentStep) {

                                // =====================================
                                // PASO 1: DETALLES
                                // =====================================

                                1 -> {

                                    ReportDetailsStep(
                                        mode = mode,

                                        email = email,

                                        onEmailChange =
                                            reportViewModel::onEmailChange,

                                        confirmEmail = confirmEmail,

                                        onConfirmEmailChange =
                                            reportViewModel::onConfirmEmailChange,

                                        numberOfChildren =
                                            numberOfChildren,

                                        onNumberOfChildrenChange =
                                            reportViewModel::onNumberOfChildrenChange,

                                        childrenAges = childrenAges,

                                        onChildrenAgesChange =
                                            reportViewModel::onChildrenAgesChange,

                                        workType = workType,

                                        onWorkTypeChange =
                                            reportViewModel::onWorkTypeChange,

                                        workSchedule = workSchedule,

                                        onWorkScheduleChange =
                                            reportViewModel::onWorkScheduleChange,

                                        details = details,

                                        onDetailsChange =
                                            reportViewModel::onDetailsChange,

                                        onNextClick = {

                                            reportViewModel.onStepChange(2)
                                        }
                                    )
                                }

                                // =====================================
                                // PASO 2: UBICACIÓN
                                // =====================================

                                2 -> {

                                    ReportLocationStep(
                                        selectedLocation =
                                            selectedLocation,

                                        onLocationSelected = {

                                            reportViewModel
                                                .onMapLocationChanged(it)
                                        },

                                        fullAddress = fullAddress,

                                        onFullAddressChange =
                                            reportViewModel::onFullAddressChange,

                                        neighborhood = neighborhood,

                                        onNeighborhoodChange =
                                            reportViewModel::onNeighborhoodChange,

                                        postalCode = postalCode,

                                        onPostalCodeChange =
                                            reportViewModel::onPostalCodeChange,

                                        municipality = municipality,

                                        onMunicipalityChange =
                                            reportViewModel::onMunicipalityChange,

                                        onCurrentLocationClick =
                                            onCurrentLocationClick,

                                        onNextClick = {

                                            reportViewModel.onStepChange(3)
                                        }
                                    )
                                }

                                // =====================================
                                // PASO 3: EVIDENCIAS
                                // =====================================

                                3 -> {

                                    ReportEvidenceStep(
                                        onSelectFilesClick =
                                            onSelectFilesClick,

                                        onNextClick = {

                                            reportViewModel.onStepChange(4)
                                        }
                                    )
                                }

                                // =====================================
                                // PASO 4: REVISIÓN
                                // =====================================

                                4 -> {

                                    ReportReviewStep(
                                        mode = mode,
                                        email = email,

                                        numberOfChildren =
                                            numberOfChildren,

                                        childrenAges = childrenAges,
                                        workType = workType,
                                        workSchedule = workSchedule,
                                        details = details,

                                        fullAddress = fullAddress,
                                        neighborhood = neighborhood,
                                        postalCode = postalCode,
                                        municipality = municipality,

                                        onEditDetailsClick = {

                                            reportViewModel.onStepChange(1)
                                        },

                                        onEditLocationClick = {

                                            reportViewModel.onStepChange(2)
                                        },

                                        onEditEvidenceClick = {

                                            reportViewModel.onStepChange(3)
                                        },

                                        onSendReportClick = {

                                            /*
                                             * TODO(BACKEND):
                                             *
                                             * No mostrar éxito hasta que
                                             * el servidor confirme el
                                             * registro del reporte.
                                             *
                                             * onSendReportClick()
                                             */
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// =================================================================
// ENCABEZADO
// =================================================================

@Composable
private fun ReportHeader(
    onBackClick: () -> Unit
) {
    val colors = AppTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = colors.titles,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Realizar reporte",
            color = colors.text,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
    }
}

// =================================================================
// INDICADOR DE PROGRESO
// =================================================================

@Composable
private fun ReportProgressIndicator(
    currentStep: Int
) {
    val colors = AppTheme.colors
    val pink = colors.pinkPrincipal
    val inactiveColor = colors.text.copy(alpha = 0.30f)

    val labels = listOf(
        "Detalles",
        "Ubicación",
        "Evidencia",
        "Revisión"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.Top
    ) {
        labels.forEachIndexed { index, label ->

            val step = index + 1
            val isCompleted = step < currentStep
            val isCurrent = step == currentStep

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step != 1) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(
                                    if (step <= currentStep) pink
                                    else inactiveColor
                                )
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCompleted || isCurrent) {
                                    pink
                                } else {
                                    colors.surface
                                }
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (isCompleted || isCurrent) {
                                    pink
                                } else {
                                    inactiveColor
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = step.toString(),
                                color = if (isCurrent) {
                                    Color.White
                                } else {
                                    colors.text.copy(alpha = 0.65f)
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (step != 4) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(
                                    if (step < currentStep) pink
                                    else inactiveColor
                                )
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = label,
                    color = if (isCurrent) {
                        pink
                    } else {
                        colors.text.copy(alpha = 0.70f)
                    },
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// =================================================================
// PASO 1: DETALLES
// =================================================================

@Composable
private fun ReportDetailsStep(
    mode: ReportMode,

    email: String,
    onEmailChange: (String) -> Unit,

    confirmEmail: String,
    onConfirmEmailChange: (String) -> Unit,

    numberOfChildren: String,
    onNumberOfChildrenChange: (String) -> Unit,

    childrenAges: String,
    onChildrenAgesChange: (String) -> Unit,

    workType: String,
    onWorkTypeChange: (String) -> Unit,

    workSchedule: String,
    onWorkScheduleChange: (String) -> Unit,

    details: String,
    onDetailsChange: (String) -> Unit,

    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 22.dp,
                end = 22.dp,
                top = 16.dp,
                bottom = 24.dp
            )
    ) {

        // =========================================================
        // REPORTE ANÓNIMO
        // =========================================================

        if (mode == ReportMode.ANONYMOUS) {
            AnonymousModeCard(
                email = email,
                onEmailChange = onEmailChange,
                confirmEmail = confirmEmail,
                onConfirmEmailChange = onConfirmEmailChange
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // =========================================================
        // DATOS COMUNES
        // =========================================================

        StepTitle(
            title = "Detalles del reporte",
            description =
                "Proporciona la información disponible sobre la situación observada. No es necesario contar con todos los datos para continuar."
        )

        Spacer(modifier = Modifier.height(24.dp))

        ReportFieldWithLabel(
            label = "Número de niñas, niños",
            value = numberOfChildren,
            placeholder = "Ingresa una cantidad",
            onValueChange = onNumberOfChildrenChange
        )

        Spacer(modifier = Modifier.height(18.dp))

        ReportFieldWithLabel(
            label = "Rango de edad aproximado",
            value = childrenAges,
            placeholder = "Ej: 6 - 12 años",
            onValueChange = onChildrenAgesChange
        )

        Spacer(modifier = Modifier.height(18.dp))

        ReportFieldWithLabel(
            label = "Tipo de actividad observada",
            value = workType,
            placeholder = "Describe la actividad observada",
            onValueChange = onWorkTypeChange
        )

        Spacer(modifier = Modifier.height(18.dp))

        ReportFieldWithLabel(
            label = "Horario aproximado",
            value = workSchedule,
            placeholder = "Ej: 8:00 am - 6:00 pm",
            onValueChange = onWorkScheduleChange
        )

        Spacer(modifier = Modifier.height(18.dp))

        ReportFieldWithLabel(
            label = "Descripción de la situación",
            value = details,
            placeholder = "Describe brevemente los hechos observados...",
            onValueChange = {
                if (it.length <= 500) {
                    onDetailsChange(it)
                }
            },
            minHeight = 120.dp,
            singleLine = false
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "${details.length}/500",
            color = AppTheme.colors.text.copy(alpha = 0.65f),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(26.dp))

        NextButton(
            text = "Siguiente",
            onClick = onNextClick
        )
    }
}

// =================================================================
// BLOQUE EXCLUSIVO: REPORTE ANÓNIMO
// =================================================================

@Composable
private fun AnonymousModeCard(
    email: String,
    onEmailChange: (String) -> Unit,
    confirmEmail: String,
    onConfirmEmailChange: (String) -> Unit
) {
    val colors = AppTheme.colors

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.pinkPrincipal.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = colors.pinkPrincipal.copy(alpha = 0.22f)
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
        ) {
            Text(
                text = "Reporte anónimo",
                color = colors.pinkPrincipal,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text =
                    "El reporte se registrará de forma anónima. " +
                            "El correo electrónico se utilizará únicamente " +
                            "para los fines definidos para esta modalidad.",
                color = colors.text.copy(alpha = 0.78f),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            ReportFieldWithLabel(
                label = "Correo electrónico (*)",
                value = email,
                placeholder = "correo@ejemplo.com",
                onValueChange = onEmailChange
            )

            Spacer(modifier = Modifier.height(14.dp))

            ReportFieldWithLabel(
                label = "Confirmar correo electrónico",
                value = confirmEmail,
                placeholder = "Confirma tu correo electrónico",
                onValueChange = onConfirmEmailChange
            )

            if (
                confirmEmail.isNotEmpty() &&
                !email.equals(confirmEmail, ignoreCase = true)
            ) {
                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "Los correos electrónicos no coinciden.",
                    color = colors.pinkPrincipal,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// =================================================================
// PASO 2: UBICACIÓN
// =================================================================

@Composable
private fun ReportLocationStep(
    selectedLocation: LatLng,
    onLocationSelected: (LatLng) -> Unit,

    fullAddress: String,
    onFullAddressChange: (String) -> Unit,

    neighborhood: String,
    onNeighborhoodChange: (String) -> Unit,

    postalCode: String,
    onPostalCodeChange: (String) -> Unit,

    municipality: String,
    onMunicipalityChange: (String) -> Unit,

    onCurrentLocationClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 22.dp,
                end = 22.dp,
                top = 16.dp,
                bottom = 24.dp
            )
    ) {
        StepTitle(
            title = "Ubicación del reporte",
            description =
                "Indica el lugar donde ocurrió la situación. Puedes seleccionar un punto en el mapa o proporcionar la dirección de forma manual."
        )

        Spacer(modifier = Modifier.height(22.dp))

        // =========================================================
        // MAPA INTERACTIVO
        // =========================================================

        ReportLocationMap(
            selectedLocation = selectedLocation,
            onLocationSelected = onLocationSelected
        )

        Spacer(modifier = Modifier.height(20.dp))

        ReportFieldWithLabel(
            label = "Dirección completa",
            value = fullAddress,
            placeholder = "Ingresa la dirección o referencia",
            onValueChange = onFullAddressChange,
            minHeight = 72.dp,
            singleLine = false
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportFieldWithLabel(
                label = "Colonia",
                value = neighborhood,
                placeholder = "Ingresar",
                onValueChange = onNeighborhoodChange,
                modifier = Modifier.weight(1.2f)
            )

            ReportFieldWithLabel(
                label = "Código postal",
                value = postalCode,
                placeholder = "Ingresar",
                onValueChange = onPostalCodeChange,
                modifier = Modifier.weight(0.8f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        ReportFieldWithLabel(
            label = "Municipio",
            value = municipality,
            placeholder = "Ingresa el municipio",
            onValueChange = onMunicipalityChange
        )

        Spacer(modifier = Modifier.height(28.dp))

        NextButton(
            text = "Siguiente",
            onClick = onNextClick
        )
    }
}

// =================================================================
// PASO 3: EVIDENCIA
// =================================================================

@Composable
private fun ReportEvidenceStep(
    onSelectFilesClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 22.dp,
                end = 22.dp,
                top = 16.dp,
                bottom = 24.dp
            )
    ) {
        StepTitle(
            title = "Evidencia fotográfica",
            description =
                "Puedes agregar fotografías que ayuden a documentar la situación. Este campo es opcional."
        )

        Spacer(modifier = Modifier.height(28.dp))

        EvidenceSelector(
            onClick = onSelectFilesClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TODO(EVIDENCE):
        // Mostrar aquí las fotografías seleccionadas
        // cuando se integre el selector real.

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(4) { index ->
                EvidencePlaceholder(
                    showAdd = index == 0,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        EvidencePlaceholder(
            showAdd = false,
            modifier = Modifier
                .width(76.dp)
                .height(76.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        NextButton(
            text = "Siguiente",
            onClick = onNextClick
        )
    }
}

// =================================================================
// PASO 4: REVISIÓN
// =================================================================

@Composable
private fun ReportReviewStep(
    mode: ReportMode,
    email: String,

    numberOfChildren: String,
    childrenAges: String,
    workType: String,
    workSchedule: String,
    details: String,

    fullAddress: String,
    neighborhood: String,
    postalCode: String,
    municipality: String,

    onEditDetailsClick: () -> Unit,
    onEditLocationClick: () -> Unit,
    onEditEvidenceClick: () -> Unit,

    onSendReportClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 18.dp,
                end = 18.dp,
                top = 16.dp,
                bottom = 28.dp
            )
    ) {
        StepTitle(
            title = "Revisión del reporte",
            description =
                "Verifica que la información sea correcta antes de enviarlo. Puedes editar los datos si es necesario."
        )

        Spacer(modifier = Modifier.height(22.dp))

        // =========================================================
        // MODALIDAD
        // =========================================================

        ReviewCard(
            title = "Modalidad del reporte",
            onEditClick = onEditDetailsClick
        ) {
            ReviewRow(
                label = "Modalidad",
                value = if (mode == ReportMode.ANONYMOUS) {
                    "Reporte anónimo"
                } else {
                    "Reporte con seguimiento"
                }
            )

            if (mode == ReportMode.ANONYMOUS) {
                ReviewRow(
                    label = "Correo electrónico",
                    value = valueOrEmpty(email)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // =========================================================
        // DETALLES
        // =========================================================

        ReviewCard(
            title = "Detalles del reporte",
            onEditClick = onEditDetailsClick
        ) {
            ReviewRow(
                label = "Número de niñas, niños",
                value = valueOrEmpty(numberOfChildren)
            )

            ReviewRow(
                label = "Rango de edad aproximado",
                value = valueOrEmpty(childrenAges)
            )

            ReviewRow(
                label = "Tipo de actividad",
                value = valueOrEmpty(workType)
            )

            ReviewRow(
                label = "Horario aproximado",
                value = valueOrEmpty(workSchedule)
            )

            ReviewRow(
                label = "Descripción",
                value = valueOrEmpty(details)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // =========================================================
        // UBICACIÓN
        // =========================================================

        ReviewCard(
            title = "Ubicación del reporte",
            onEditClick = onEditLocationClick
        ) {
            ReviewRow(
                label = "Dirección",
                value = valueOrEmpty(fullAddress)
            )

            ReviewRow(
                label = "Colonia",
                value = valueOrEmpty(neighborhood)
            )

            ReviewRow(
                label = "Código postal",
                value = valueOrEmpty(postalCode)
            )

            ReviewRow(
                label = "Municipio",
                value = valueOrEmpty(municipality)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // =========================================================
        // EVIDENCIAS
        // =========================================================

        ReviewCard(
            title = "Evidencias",
            onEditClick = onEditEvidenceClick
        ) {
            Text(
                text =
                    "Las evidencias seleccionadas aparecerán aquí cuando se conecte el selector de archivos.",
                color = AppTheme.colors.text.copy(alpha = 0.70f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // =========================================================
        // ENVÍO
        // =========================================================

        Button(
            onClick = onSendReportClick,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.colors.pinkPrincipal,
                contentColor = Color.White,
                disabledContainerColor =
                    AppTheme.colors.pinkPrincipal.copy(alpha = 0.45f),
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = "Enviar reporte",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "El envío estará disponible cuando se conecte el servicio de registro.",
            color = AppTheme.colors.text.copy(alpha = 0.65f),
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text =
                "La información proporcionada será utilizada únicamente para atender el reporte.",
            color = AppTheme.colors.text.copy(alpha = 0.65f),
            fontSize = 12.sp,
            lineHeight = 17.sp
        )
    }
}


// =================================================================
// TÍTULO DE CADA PASO
// =================================================================

@Composable
private fun StepTitle(
    title: String,
    description: String
) {
    Text(
        text = title,
        color = AppTheme.colors.titles,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp
    )

    Spacer(modifier = Modifier.height(7.dp))

    Text(
        text = description,
        color = AppTheme.colors.text.copy(alpha = 0.78f),
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
}

// =================================================================
// CAMPO CON LABEL
// =================================================================

@Composable
private fun ReportFieldWithLabel(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 52.dp,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {

        Text(
            text = label,
            color = AppTheme.colors.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(7.dp))

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

// =================================================================
// TEXT FIELD RIETI
// =================================================================

@Composable
private fun RietiTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 52.dp,
    singleLine: Boolean = true
) {
    val colors = AppTheme.colors

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,

        textStyle = TextStyle(
            color = colors.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        ),

        cursorBrush = SolidColor(colors.pinkPrincipal),

        modifier = modifier
            .heightIn(min = minHeight)
            .clip(RoundedCornerShape(9.dp))
            .background(colors.surface)
            .border(
                width = 1.dp,
                color = colors.text.copy(alpha = 0.18f),
                shape = RoundedCornerShape(9.dp)
            ),

        decorationBox = { innerTextField ->

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 14.dp
                    ),

                contentAlignment = if (singleLine) {
                    Alignment.CenterStart
                } else {
                    Alignment.TopStart
                }
            ) {

                if (value.isEmpty()) {

                    Text(
                        text = placeholder,
                        color = colors.text.copy(alpha = 0.45f),
                        fontSize = 14.sp
                    )
                }

                innerTextField()
            }
        }
    )
}

// =================================================================
// MAPA TEMPORAL
// =================================================================

@Composable
private fun MapPlaceholder(
    onCurrentLocationClick: () -> Unit
) {
    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(225.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                colors.blueDetails.copy(alpha = 0.10f)
            )
            .border(
                width = 1.dp,
                color = colors.blueDetails.copy(alpha = 0.35f),
                shape = RoundedCornerShape(10.dp)
            )
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = colors.bluePrincipal,
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Mapa",
                color = colors.text,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                text = "Se conectará posteriormente",
                color = colors.text.copy(alpha = 0.55f),
                fontSize = 11.sp
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .clickable {
                    onCurrentLocationClick()
                },

            color = colors.surface,
            shape = RoundedCornerShape(8.dp),

            border = BorderStroke(
                width = 1.dp,
                color = colors.bluePrincipal.copy(alpha = 0.25f)
            )
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = colors.bluePrincipal,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "Usar ubicación actual",
                    color = colors.bluePrincipal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// =================================================================
// SELECTOR DE EVIDENCIAS
// =================================================================

@Composable
private fun EvidenceSelector(
    onClick: () -> Unit
) {
    val colors = AppTheme.colors

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .clickable(onClick = onClick),

        color = colors.surface,
        shape = RoundedCornerShape(10.dp),

        border = BorderStroke(
            width = 1.dp,
            color = colors.pinkPrincipal.copy(alpha = 0.55f)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.AddPhotoAlternate,
                contentDescription = null,
                tint = colors.pinkPrincipal,
                modifier = Modifier.size(38.dp)
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column {

                Text(
                    text = "Agregar fotografías",
                    color = colors.text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Puedes subir hasta 5 imágenes.",
                    color = colors.text.copy(alpha = 0.65f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// =================================================================
// PLACEHOLDER DE EVIDENCIA
// =================================================================

@Composable
private fun EvidencePlaceholder(
    showAdd: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Box(
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.text.copy(alpha = 0.06f)),

        contentAlignment = Alignment.Center
    ) {

        if (showAdd) {

            Text(
                text = "+",
                color = colors.pinkPrincipal,
                fontSize = 34.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

// =================================================================
// BOTÓN SIGUIENTE
// =================================================================

@Composable
private fun NextButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),

        shape = RoundedCornerShape(10.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.colors.pinkPrincipal,
            contentColor = Color.White
        )
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = text,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center)
            )

            Icon(
                imageVector =
                    Icons.AutoMirrored.Filled.ArrowForward,

                contentDescription = null,

                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(22.dp)
            )
        }
    }
}

// =================================================================
// TARJETA DE REVISIÓN
// =================================================================

@Composable
private fun ReviewCard(
    title: String,
    onEditClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = AppTheme.colors

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.surface,
        shape = RoundedCornerShape(10.dp),

        border = BorderStroke(
            width = 1.dp,
            color = colors.text.copy(alpha = 0.14f)
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = colors.titles,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(9.dp))

                Text(
                    text = title,
                    color = colors.text,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Editar",
                    color = colors.pinkPrincipal,
                    fontSize = 13.sp,

                    textDecoration =
                        androidx.compose.ui.text.style
                            .TextDecoration.Underline,

                    modifier = Modifier.clickable {
                        onEditClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

// =================================================================
// FILA DE REVISIÓN
// =================================================================

@Composable
private fun ReviewRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        Text(
            text = label,
            color = AppTheme.colors.text.copy(alpha = 0.75f),
            fontSize = 12.sp,
            modifier = Modifier.weight(1.25f)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = value,
            color = AppTheme.colors.text,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

// =================================================================
// TEXTO VACÍO EN REVISIÓN
// =================================================================

private fun valueOrEmpty(
    value: String
): String {
    return value.ifBlank {
        "No especificado"
    }
}

// =================================================================
// PREVIEW LIGHT: REPORTE CON SEGUIMIENTO
// =================================================================

@Preview(
    name = "Reporte - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ReportScreenLightPreview() {

    RIETITheme(darkTheme = false) {

        ReportScreen(
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
// PREVIEW DARK: REPORTE CON SEGUIMIENTO
// =================================================================

@Preview(
    name = "Reporte - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ReportScreenDarkPreview() {

    RIETITheme(darkTheme = true) {

        ReportScreen(
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
// PREVIEW LIGHT: REPORTE ANÓNIMO
// =================================================================

@Preview(
    name = "Reporte anónimo - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AnonymousReportFormLightPreview() {

    RIETITheme(darkTheme = false) {

        ReportFormScreen(
            mode = ReportMode.ANONYMOUS,
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
// PREVIEW DARK: REPORTE ANÓNIMO
// =================================================================

@Preview(
    name = "Reporte anónimo - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AnonymousReportFormDarkPreview() {

    RIETITheme(darkTheme = true) {

        ReportFormScreen(
            mode = ReportMode.ANONYMOUS,
            onHomeClick = {},
            onAboutUsClick = {},
            onSettingsClick = {},
            onCurrentLocationClick = {},
            onSelectFilesClick = {},
            onSendReportClick = {}
        )
    }
}
