
package com.example.rieti.view

import android.content.res.Configuration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.util.Calendar
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
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.draw.clip
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
import com.example.rieti.view.components.rietiScrollbar
import com.example.rieti.viewmodel.ReportViewModel
import com.example.rieti.model.MunicipalityBoundaries
import com.example.rieti.model.AgeBreakdown
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue

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
    onSendReportClick: (String) -> Unit
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
    onSendReportClick: (String) -> Unit,
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
    val context = LocalContext.current
    val sending by reportViewModel.sending.collectAsState()
    val sendError by reportViewModel.sendError.collectAsState()
    val reportSnackbar = remember { SnackbarHostState() }
    LaunchedEffect(sendError) {
        sendError?.takeIf { it.isNotBlank() }?.let {
            reportSnackbar.showSnackbar(it, duration = SnackbarDuration.Short)
        }
    }
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val selected = uris.take(5)
        selected.forEach { uri ->
            runCatching { context.contentResolver.takePersistableUriPermission(
                uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            ) }
        }
        reportViewModel.onEvidenceReferencesChange(
            (reportState.evidenceReferences + selected.map { it.toString() }).distinct().take(5)
        )
    }


    val showLocationQuestion = remember { mutableStateOf(false) }
    val locationConsentGiven = remember { mutableStateOf(false) }
    val locationConfirmed = remember { mutableStateOf(false) }
    val locationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) {
            reportViewModel.fetchCurrentLocation(context) {
                locationConfirmed.value = true
            }
        }
    }
    val useDeviceLocation: () -> Unit = {
        val granted = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
        if (granted) {
            reportViewModel.fetchCurrentLocation(context) {
                locationConfirmed.value = true
            }
        }
        else locationPermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    LaunchedEffect(reportState.currentStep, isDraftLoaded) {
        if (reportState.currentStep == 2 && isDraftLoaded && !locationConsentGiven.value) {
            showLocationQuestion.value = true
        }
    }
    if (showLocationQuestion.value) AlertDialog(
        onDismissRequest = { showLocationQuestion.value = false },
        title = { Text("¿Estás en el lugar donde ocurrió?") },
        text = { Text("Si estás ahí, tomaremos tu ubicación. Si no, podrás elegir el punto en el mapa.") },
        confirmButton = { TextButton(onClick = {
            showLocationQuestion.value = false
            locationConsentGiven.value = true
            useDeviceLocation()
        }) { Text("Sí, estoy aquí") } },
        dismissButton = { TextButton(onClick = {
            showLocationQuestion.value = false
            locationConsentGiven.value = true
            locationConfirmed.value = false
        }) { Text("No, elegir en mapa") } }
    )
    // El municipio se obtiene de limites geoestadisticos oficiales de INEGI.
    // Geocoder se usa solamente para direccion, colonia, codigo postal y entidad.
    // No se permite copiar locality/subAdminArea al campo Municipio.
    val municipalityResolver = remember(context) { MunicipalityBoundaries(context.applicationContext) }
    val municipalityChecking = remember { mutableStateOf(false) }
    val municipalityMessage = remember { mutableStateOf("") }
    val municipalityOptions = remember { mutableStateOf<List<String>>(emptyList()) }
    val verifiedMunicipality = remember { mutableStateOf("") }
    val retryMunicipality = remember { mutableStateOf(0) }
    LaunchedEffect(reportState.selectedLocation, reportState.currentStep, locationConsentGiven.value, retryMunicipality.value) {
        if (reportState.currentStep == 2 && locationConsentGiven.value) {
            val point = reportState.selectedLocation
            municipalityChecking.value = true
            municipalityMessage.value = ""
            verifiedMunicipality.value = ""
            // Un municipio de una posicion anterior nunca debe seguir visible tras mover el mapa.
            reportViewModel.onMunicipalityChange("")
            reportViewModel.onFullAddressChange("")
            reportViewModel.onNeighborhoodChange("")
            reportViewModel.onPostalCodeChange("")
            try {
                val address = withContext(Dispatchers.IO) {
                    runCatching {
                        @Suppress("DEPRECATION")
                        Geocoder(context, java.util.Locale("es", "MX"))
                            .getFromLocation(point.latitude, point.longitude, 1)?.firstOrNull()
                    }.getOrNull()
                }
                address?.let {
                    reportViewModel.onFullAddressChange(it.getAddressLine(0).orEmpty())
                    reportViewModel.onNeighborhoodChange(it.subLocality ?: it.locality.orEmpty())
                    reportViewModel.onPostalCodeChange(it.postalCode.orEmpty())
                }
                val match = municipalityResolver.resolve(point.latitude, point.longitude, address?.adminArea)
                municipalityOptions.value = municipalityResolver.municipalities(address?.adminArea, match?.geoCode).map { it.name }
                if (match != null) {
                    verifiedMunicipality.value = match.name
                    reportViewModel.onMunicipalityChange(match.name)
                    municipalityMessage.value = ""
                } else {
                    municipalityMessage.value = "No se pudo identificar el municipio. Ajusta el punto e intenta nuevamente."
                }
            } catch (_: Exception) {
                municipalityMessage.value = "No se pudo comprobar el municipio. Revisa tu conexion e intenta otra vez."
            } finally {
                municipalityChecking.value = false
            }
        }
    }
    val selectReportPoint: (LatLng) -> Unit = { point ->
        locationConsentGiven.value = true
        locationConfirmed.value = true
        reportViewModel.onMapLocationChanged(point)
    }


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
        snackbarHost = {
            SnackbarHost(hostState = reportSnackbar) { data ->
                Snackbar(
                    data,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    containerColor = colors.pinkPrincipal,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },

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

                                        onLocationSelected = selectReportPoint,
                                        hasConfirmedLocation = locationConfirmed.value,

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

                                        onMunicipalityChange = { selected ->
                                            if (selected == verifiedMunicipality.value) {
                                                reportViewModel.onMunicipalityChange(selected)
                                            } else {
                                                Toast.makeText(context,
                                                    "Ese municipio no coincide con el punto. Ajusta la ubicacion en el mapa.",
                                                    Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        municipalityChecking = municipalityChecking.value,
                                        municipalityMessage = municipalityMessage.value,
                                        municipalityOptions = municipalityOptions.value,
                                        onRetryMunicipality = { retryMunicipality.value += 1 },

                                        onCurrentLocationClick = {
                                            locationConsentGiven.value = true
                                            useDeviceLocation()
                                        },
                                        onSearchAddressClick = {
                                            coroutineScope.launch {
                                                val query = listOf(reportState.fullAddress, reportState.neighborhood,
                                                    reportState.municipality, reportState.postalCode)
                                                    .filter { it.isNotBlank() }.joinToString(", ")
                                                if (query.isNotBlank()) {
                                                    val point = withContext(Dispatchers.IO) {
                                                        runCatching { @Suppress("DEPRECATION")
                                                            Geocoder(context, java.util.Locale("es", "MX"))
                                                                .getFromLocationName(query, 1)?.firstOrNull()
                                                        }.getOrNull()
                                                    }
                                                    if (point != null) {
                                                        val street = reportState.fullAddress.trim().substringBefore(",").lowercase(java.util.Locale.ROOT)
                                                        val actualStreet = (point.thoroughfare ?: "").lowercase(java.util.Locale.ROOT)
                                                        // No aceptar un resultado aproximado cuando el usuario escribió una calle distinta.
                                                        if (street.length >= 5 && actualStreet.isNotBlank() &&
                                                            !street.contains(actualStreet) && !actualStreet.contains(street)) {
                                                            Toast.makeText(context, "La calle encontrada no coincide. Selecciona el punto en el mapa.", Toast.LENGTH_LONG).show()
                                                        } else selectReportPoint(LatLng(point.latitude, point.longitude))
                                                    } else Toast.makeText(context, "No se encontró una dirección exacta. Usa el mapa.", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        },

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
                                        onRemoveFile = { uri ->
                                            reportViewModel.onEvidenceReferencesChange(
                                                reportState.evidenceReferences.filterNot { it == uri }
                                            )
                                        },
                                        onSelectFilesClick = {
                                            if (!sending) picker.launch("image/*")
                                        },
                                        selectedCount = reportState.evidenceReferences.size,
                                        evidenceReferences = reportState.evidenceReferences,

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

                                        evidenceReferences = reportState.evidenceReferences,
                                        onEditEvidenceClick = {

                                            reportViewModel.onStepChange(3)
                                        },

                                        onSendReportClick = {
                                            if (!sending) coroutineScope.launch {
                                                val folio = reportViewModel.sendReport()
                                                if (folio != null) onSendReportClick(folio)
                                            }
                                        }
                                    )
                                    if (sending) {
                                        Dialog(onDismissRequest = {}) {
                                            Surface(shape = RoundedCornerShape(20.dp), color = AppTheme.colors.surface) {
                                                Column(
                                                    Modifier.fillMaxWidth().padding(28.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    CircularProgressIndicator(color = AppTheme.colors.pinkPrincipal)
                                                    Spacer(Modifier.height(18.dp))
                                                    Text("Enviando tu reporte", color = AppTheme.colors.text,
                                                        fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                                    Spacer(Modifier.height(8.dp))
                                                    Text("Guardando los datos y las fotografías...",
                                                        color = AppTheme.colors.text.copy(alpha = 0.7f),
                                                        fontSize = 13.sp, textAlign = TextAlign.Center)
                                                }
                                            }
                                        }
                                    }

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
    val animatedProgress by animateFloatAsState(targetValue = currentStep.toFloat(), animationSpec = tween(450), label = "progreso reporte")

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
                                    if (animatedProgress >= step) pink
                                    else pink.copy(alpha = (0.25f + (animatedProgress - (step - 1)).coerceIn(0f, 1f) * 0.75f))
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
                                    if (animatedProgress >= step + 1) pink
                                    else pink.copy(alpha = (0.25f + (animatedProgress - step).coerceIn(0f, 1f) * 0.75f))
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
private fun ReportCatalogSelector(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val optionsScroll = rememberScrollState()
    Column {
        Text(label, color = AppTheme.colors.text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(7.dp))
        Surface(
            modifier = Modifier.fillMaxWidth().clickable { expanded.value = true },
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, AppTheme.colors.pinkPrincipal.copy(alpha = 0.5f)),
            color = AppTheme.colors.surface
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value.ifBlank { "Pulsa para seleccionar" },
                    modifier = Modifier.weight(1f),
                    color = AppTheme.colors.text,
                    fontSize = 14.sp
                )
                Text("▾", color = AppTheme.colors.pinkPrincipal)
            }
        }
        if (expanded.value) {
            Dialog(onDismissRequest = { expanded.value = false }) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = AppTheme.colors.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(label, color = AppTheme.colors.pinkPrincipal,
                            fontFamily = FontFamily.Serif, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Column(modifier = Modifier.heightIn(max = 360.dp).verticalScroll(optionsScroll).rietiScrollbar(optionsScroll)) {
                            options.forEach { option ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        onSelect(option)
                                        expanded.value = false
                                    }.padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = value == option,
                                        onClick = { onSelect(option); expanded.value = false },
                                        colors = RadioButtonDefaults.colors(selectedColor = AppTheme.colors.pinkPrincipal)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(option, color = AppTheme.colors.text, fontSize = 14.sp,
                                        modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { expanded.value = false }, modifier = Modifier.align(Alignment.End)) {
                            Text("Cancelar", color = AppTheme.colors.pinkPrincipal)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    val reportScroll = rememberScrollState()
    val context = LocalContext.current
    val validationError = remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(reportScroll).rietiScrollbar(reportScroll)
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
                "Completa los datos obligatorios. La descripción es opcional."
        )

        Spacer(modifier = Modifier.height(24.dp))

        ReportFieldWithLabel(
            label = "Número de niñas, niños y adolescentes",
            value = numberOfChildren,
            placeholder = "Ingresa una cantidad",
            onValueChange = { onNumberOfChildrenChange(it.filter(Char::isDigit).take(3)) },
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Un reporte puede involucrar menores en varios rangos de edad.
        // Guardamos el desglose en el campo existente del borrador.
        Text("¿Cuántos menores hay en cada rango?", color = AppTheme.colors.text,
            fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        Text("Reparte el total que anotaste arriba entre estas edades. Puedes poner 0 donde no haya menores.",
            color = AppTheme.colors.text.copy(alpha = 0.7f), fontSize = 12.sp)
        val counts = AgeBreakdown.read(childrenAges, numberOfChildren.toIntOrNull() ?: 0)
        AgeBreakdown.ranges.forEach { age ->
            Spacer(Modifier.height(8.dp))
            ReportFieldWithLabel(
                label = "$age años",
                value = counts[age]?.toString().orEmpty(),
                placeholder = "0",
                onValueChange = { raw ->
                    val next = counts.toMutableMap()
                    val n = raw.filter(Char::isDigit).take(3).toIntOrNull() ?: 0
                    if (n == 0) next.remove(age) else next[age] = n
                    onChildrenAgesChange(AgeBreakdown.encode(next))
                },
                keyboardType = KeyboardType.Number
            )
        }
        Spacer(Modifier.height(6.dp))
        Text("Total por edades: ${counts.values.sum()} de ${numberOfChildren.toIntOrNull() ?: 0}",
            color = if (AgeBreakdown.valid(childrenAges, numberOfChildren.toIntOrNull() ?: 0))
                AppTheme.colors.text else AppTheme.colors.pinkPrincipal, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(18.dp))

        ReportCatalogSelector(
            label = "Tipo de actividad observada",
            value = if (workType.startsWith("Otro: ")) "Otro" else workType,
            options = listOf(
                "Venta en semáforos", "Repartiendo volantes", "Venta en mercado",
                "Trabajador de tortillería", "Trabajo en construcción", "Lavado de autos",
                "Despachador de gasolina", "Cerillo en supermercado", "Limpiaparabrisas",
                "Mendicidad forzada o inducida", "Explotación sexual comercial de NNA", "Otro"
            ),
            onSelect = { option -> onWorkTypeChange(if (option == "Otro") "Otro: " else option) }
        )
        if (workType.startsWith("Otro:")) {
            Spacer(Modifier.height(10.dp))
            ReportFieldWithLabel(
                label = "Especifica la actividad observada",
                value = workType.removePrefix("Otro:").trimStart(),
                placeholder = "Describe qué trabajo realiza",
                onValueChange = { onWorkTypeChange("Otro: " + it.take(120)) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Un horario puede ser una hora puntual o un rango de horas.
        val scheduleMode = remember { mutableStateOf(if (workSchedule.contains(" - ")) "Rango" else "Hora exacta") }
        val startHour = remember { mutableStateOf(workSchedule.substringBefore(" - ").takeIf { it.matches(Regex("[0-9]{2}:[0-9]{2}")) }.orEmpty()) }
        val endHour = remember { mutableStateOf(workSchedule.substringAfter(" - ", "").takeIf { it.matches(Regex("[0-9]{2}:[0-9]{2}")) }.orEmpty()) }
        val editingTime = remember { mutableStateOf<String?>(null) }
        if (editingTime.value != null) {
            val initial = if (editingTime.value == "start") startHour.value else endHour.value
            val now = Calendar.getInstance()
            val picker = rememberTimePickerState(
                initialHour = initial.substringBefore(":").toIntOrNull() ?: now.get(Calendar.HOUR_OF_DAY),
                initialMinute = initial.substringAfter(":", "").toIntOrNull() ?: now.get(Calendar.MINUTE),
                is24Hour = true
            )
            Dialog(onDismissRequest = { editingTime.value = null }) {
                MaterialTheme(colorScheme = lightColorScheme(
                    primary = AppTheme.colors.pinkPrincipal,
                    surface = AppTheme.colors.surface,
                    onSurface = AppTheme.colors.text
                )) {
                    Surface(shape = RoundedCornerShape(24.dp), color = AppTheme.colors.surface) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if (editingTime.value == "start") "Seleccionar hora" else "Seleccionar hora final",
                                color = AppTheme.colors.pinkPrincipal, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(14.dp))
                            TimePicker(state = picker, colors = TimePickerDefaults.colors(
                                clockDialColor = AppTheme.colors.surface,
                                selectorColor = AppTheme.colors.pinkPrincipal,
                                timeSelectorSelectedContainerColor = AppTheme.colors.pinkPrincipal
                            ))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { editingTime.value = null }) {
                                    Text("Cancelar", color = AppTheme.colors.pinkPrincipal)
                                }
                                TextButton(onClick = {
                                    val time = "%02d:%02d".format(picker.hour, picker.minute)
                                    if (editingTime.value == "start") startHour.value = time else endHour.value = time
                                    if (scheduleMode.value == "Hora exacta") onWorkScheduleChange(startHour.value)
                                    else if (startHour.value.isNotBlank() && endHour.value.isNotBlank())
                                        onWorkScheduleChange("${startHour.value} - ${endHour.value}")
                                    editingTime.value = null
                                }) { Text("Aceptar", color = AppTheme.colors.pinkPrincipal) }
                            }
                        }
                    }
                }
            }
        }
        Column {
            Text("Horario aproximado", color = AppTheme.colors.text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Hora exacta", "Rango").forEach { option ->
                    val selected = scheduleMode.value == option
                    Surface(
                        modifier = Modifier.weight(1f).clickable {
                            scheduleMode.value = option
                            if (option == "Hora exacta") onWorkScheduleChange(startHour.value)
                            else if (startHour.value.isNotBlank() && endHour.value.isNotBlank())
                                onWorkScheduleChange("${startHour.value} - ${endHour.value}")
                            else onWorkScheduleChange("")
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) AppTheme.colors.pinkPrincipal else AppTheme.colors.surface,
                        border = BorderStroke(1.dp, AppTheme.colors.pinkPrincipal)
                    ) {
                        Text(option, modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center,
                            color = if (selected) Color.White else AppTheme.colors.pinkPrincipal)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier.weight(1f).height(108.dp).clickable { editingTime.value = "start" },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AppTheme.colors.pinkPrincipal.copy(alpha = 0.55f)),
                    color = AppTheme.colors.surface
                ) {
                    Column(Modifier.fillMaxSize().padding(13.dp)) {
                        Text(if (scheduleMode.value == "Hora exacta") "Hora" else "Desde",
                            fontSize = 12.sp, color = AppTheme.colors.pinkPrincipal)
                        Spacer(Modifier.height(4.dp))
                        Text(startHour.value.ifBlank { "Pulsa para elegir hora" }, fontSize = 14.sp,
                            color = AppTheme.colors.text)
                    }
                }
                AnimatedVisibility(
                    visible = scheduleMode.value == "Rango",
                    modifier = Modifier.weight(1f),
                    enter = expandHorizontally(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                    exit = shrinkHorizontally(animationSpec = tween(250)) + fadeOut(animationSpec = tween(250))
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(108.dp)
                            .clickable { editingTime.value = "end" },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AppTheme.colors.pinkPrincipal.copy(alpha = 0.55f)),
                        color = AppTheme.colors.surface
                    ) {
                        Column(Modifier.fillMaxSize().padding(13.dp)) {
                            Text("Hasta", fontSize = 12.sp, color = AppTheme.colors.pinkPrincipal)
                            Spacer(Modifier.height(4.dp))
                            Text(endHour.value.ifBlank { "Pulsa para elegir hora" }, fontSize = 14.sp,
                                color = AppTheme.colors.text)
                        }
                    }
                }
            }
        }

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
            onClick = {
                validationError.value = when {
                    numberOfChildren.toIntOrNull() == null || numberOfChildren.toInt() < 1 -> "Ingresa una cantidad válida de niñas o niños."
                    !AgeBreakdown.valid(childrenAges, numberOfChildren.toIntOrNull() ?: 0) -> "Las cantidades por edad todavía no coinciden. Arriba indicaste cuántas niñas, niños y adolescentes viste; reparte esa misma cantidad entre los rangos de edad. Si no conoces una edad exacta, puedes calcularla aproximadamente."
                    workType.isBlank() -> "Selecciona el tipo de actividad."
                    workType.startsWith("Otro:") && workType.removePrefix("Otro:").trim().length < 3 -> "Especifica la otra actividad (mínimo 3 caracteres)."
                    workSchedule.isBlank() -> "Selecciona el horario."
                    mode == ReportMode.ANONYMOUS && (email.isBlank() || !email.equals(confirmEmail, true)) -> "Confirma correctamente tu correo."
                    else -> null
                }
                if (validationError.value == null) onNextClick()
            }
        )
        validationError.value?.let { Text(it, color = Color(0xFFD32F2F), fontSize = 13.sp) }
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

/**
 * Busca direcciones en el servicio de geocodificacion del dispositivo.
 * Solo se acepta una sugerencia con calle o un punto elegido en el mapa.
 * Autor: Said Valseca Figueroa.
 */
@Composable
private fun ReportAddressSearch(onLocationSelected: (LatLng) -> Unit) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var checkedQuery by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<android.location.Address>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    var searchHint by remember { mutableStateOf("") }

    LaunchedEffect(query) {
        if (query == checkedQuery || query.trim().length < 3) {
            results = emptyList()
            return@LaunchedEffect
        }
        kotlinx.coroutines.delay(500)
        searching = true
        searchHint = ""
        try {
            val candidates = withContext(Dispatchers.IO) {
                runCatching {
                    @Suppress("DEPRECATION")
                    Geocoder(context, java.util.Locale("es", "MX"))
                        .getFromLocationName(query.trim() + ", México", 5).orEmpty()
                }.getOrDefault(emptyList())
            }
            results = candidates.filter {
                it.latitude in 14.0..33.0 && it.longitude in -119.0..-86.0 &&
                    !it.getAddressLine(0).isNullOrBlank()
            }.distinctBy { it.getAddressLine(0) }
            if (results.isEmpty()) searchHint = "No encontramos sugerencias. Prueba con calle, municipio y número, o toca el mapa."
        } finally {
            searching = false
        }
    }

    ReportFieldWithLabel(
        label = "Busca una dirección o lugar",
        value = query,
        placeholder = "Ej. Calle Hidalgo 25, Cuauhtémoc o C.P. 06000",
        onValueChange = { query = it.take(180); checkedQuery = "" }
    )
    if (searching) {
        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp,
                color = AppTheme.colors.pinkPrincipal)
            Text("Buscando direcciones...", fontSize = 12.sp, color = AppTheme.colors.text)
        }
    }
    if (results.isNotEmpty()) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 7.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AppTheme.colors.text.copy(alpha = 0.12f)),
            color = AppTheme.colors.surface
        ) {
            Column {
                Text("Elige una de estas opciones", fontSize = 12.sp,
                    color = AppTheme.colors.text.copy(alpha = 0.8f),
                    modifier = Modifier.padding(12.dp))
                results.forEach { place ->
                    val name = place.getAddressLine(0).orEmpty()
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            checkedQuery = name
                            query = name
                            results = emptyList()
                            if (!place.thoroughfare.isNullOrBlank() ||
                                (!place.featureName.isNullOrBlank() && !place.subThoroughfare.isNullOrBlank())) {
                                searchHint = "Dirección elegida. Comprueba el punto en el mapa."
                                onLocationSelected(LatLng(place.latitude, place.longitude))
                            } else {
                                searchHint = "Encontramos esta zona. Para marcar el lugar exacto, agrega una calle y número a la búsqueda o toca el mapa."
                            }
                        }.padding(horizontal = 12.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null,
                            tint = AppTheme.colors.pinkPrincipal,
                            modifier = Modifier.size(20.dp))
                        Text(name, fontSize = 13.sp, color = AppTheme.colors.text,
                            modifier = Modifier.weight(1f))
                        Text("›", color = AppTheme.colors.pinkPrincipal, fontSize = 22.sp)
                    }
                }
            }
        }
    }
    if (searchHint.isNotBlank()) {
        Text(searchHint, color = AppTheme.colors.text.copy(alpha = 0.75f),
            fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun ReportLocationStep(
    selectedLocation: LatLng,
    onLocationSelected: (LatLng) -> Unit,
    hasConfirmedLocation: Boolean,

    fullAddress: String,
    onFullAddressChange: (String) -> Unit,

    neighborhood: String,
    onNeighborhoodChange: (String) -> Unit,

    postalCode: String,
    onPostalCodeChange: (String) -> Unit,

    municipality: String,
    onMunicipalityChange: (String) -> Unit,
    municipalityChecking: Boolean,
    municipalityMessage: String,
    municipalityOptions: List<String>,
    onRetryMunicipality: () -> Unit,

    onCurrentLocationClick: () -> Unit,
    onSearchAddressClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val reportScroll = rememberScrollState()
    var locationError by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(reportScroll).rietiScrollbar(reportScroll)
            .padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 24.dp)
    ) {
        StepTitle(
            title = "¿Dónde ocurrió?",
            description = "Busca una dirección y elige una sugerencia, o simplemente toca el lugar en el mapa."
        )
        Spacer(Modifier.height(19.dp))
        ReportAddressSearch(onLocationSelected)
        Spacer(Modifier.height(15.dp))
        ReportLocationMap(
            selectedLocation = selectedLocation,
            onLocationSelected = onLocationSelected
        )
        Spacer(Modifier.height(11.dp))
        Button(
            onClick = onCurrentLocationClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.pinkPrincipal)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Usar mi ubicación actual", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(13.dp))
        if (!hasConfirmedLocation) {
            Text("Elige el lugar tocando el mapa o selecciona una dirección de la lista.",
                fontSize = 13.sp, color = AppTheme.colors.text)
        } else {
            Text("Lugar seleccionado. Estos son los datos que encontramos:",
                fontSize = 13.sp, color = AppTheme.colors.text, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(13.dp))
        // Los datos se completan a partir del punto, no de una direccion inventada.
        ReportFieldWithLabel(
            label = "Dirección encontrada",
            value = fullAddress,
            placeholder = if (municipalityChecking) "Cargando dirección..." else "Elige una dirección o toca el mapa",
            onValueChange = {},
            minHeight = 70.dp,
            singleLine = false,
            readOnly = true
        )
        Spacer(Modifier.height(13.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ReportFieldWithLabel(
                label = "Colonia",
                value = neighborhood,
                placeholder = "Se llena automáticamente",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.weight(1.2f)
            )
            ReportFieldWithLabel(
                label = "Código postal",
                value = postalCode,
                placeholder = "Se llena automáticamente",
                onValueChange = {},
                keyboardType = KeyboardType.Number,
                readOnly = true,
                modifier = Modifier.weight(0.8f)
            )
        }
        Spacer(Modifier.height(13.dp))
        ReportFieldWithLabel(
            label = "Municipio o alcaldía",
            value = municipality,
            placeholder = "Se identifica al elegir el punto",
            onValueChange = {},
            readOnly = true
        )
        if (municipalityChecking) {
            Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp,
                    color = AppTheme.colors.pinkPrincipal)
                Text("Cargando...", fontSize = 12.sp, color = AppTheme.colors.text)
            }
        } else if (municipality.isBlank() && municipalityMessage.isNotBlank()) {
            Text("No encontramos el municipio para este punto. Prueba con otra ubicación.",
                fontSize = 12.sp, color = Color(0xFFD32F2F))
            Button(onClick = onRetryMunicipality,
                modifier = Modifier.padding(top = 8.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.pinkPrincipal)
            ) { Text("Volver a intentar") }
        }
        Spacer(Modifier.height(20.dp))
        NextButton(text = "Siguiente", onClick = {
            locationError = when {
                !hasConfirmedLocation -> "Primero elige el lugar en el mapa o selecciona una dirección sugerida."
                municipalityChecking -> "Un momento, estamos comprobando el lugar elegido."
                municipality.isBlank() -> "No pudimos comprobar la ubicación. Toca el mapa para elegir otro punto."
                fullAddress.isBlank() || neighborhood.isBlank() || !Regex("[0-9]{5}").matches(postalCode) ->
                    "No encontramos todos los datos de esta dirección. Prueba buscando una calle con número o elige otro punto en el mapa."
                else -> null
            }
            if (locationError == null) onNextClick()
        })
        locationError?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color(0xFFD32F2F), fontSize = 13.sp)
        }
    }
}

// =================================================================
// PASO 3: EVIDENCIA
// =================================================================

@Composable
private fun ReportEvidenceStep(
    onRemoveFile: (String) -> Unit,
    onSelectFilesClick: () -> Unit,
    selectedCount: Int,
    evidenceReferences: List<String>,
    onNextClick: () -> Unit
) {
    val reportScroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(reportScroll).rietiScrollbar(reportScroll)
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
        ReportPhotoStrip(evidenceReferences, modifier = Modifier.padding(top = 14.dp), onRemove = onRemoveFile)

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.height(40.dp))

        NextButton(
            text = "Siguiente",
            onClick = onNextClick
        )
    }
}

@Composable
private fun ReportPhotoStrip(
    photos: List<String>,
    modifier: Modifier = Modifier,
    onRemove: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    if (photos.isEmpty()) return
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos.size) { index ->
            val uriString = photos[index]
            val bitmap = remember(uriString) {
                runCatching {
                    context.contentResolver.openInputStream(android.net.Uri.parse(uriString))?.use {
                        BitmapFactory.decodeStream(it)?.asImageBitmap()
                    }
                }.getOrNull()
            }
            if (bitmap != null) {
                Box(Modifier.size(94.dp)) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Evidencia ${index + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(85.dp).clip(RoundedCornerShape(10.dp))
                            .align(Alignment.BottomStart)
                    )
                    if (onRemove != null) {
                        androidx.compose.material3.IconButton(
                            onClick = { onRemove(uriString) },
                            modifier = Modifier.align(Alignment.TopEnd).size(32.dp)
                                .background(Color(0xFFE60059), CircleShape)
                        ) {
                            Icon(Icons.Default.Close,
                                contentDescription = "Quitar esta foto", tint = Color.White,
                                modifier = Modifier.size(21.dp))
                        }
                    }
                }
            }
        }
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
    evidenceReferences: List<String>,

    onSendReportClick: () -> Unit
) {
    val reportScroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(reportScroll).rietiScrollbar(reportScroll)
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
                label = "Número de niñas, niños y adolescentes",
                value = valueOrEmpty(numberOfChildren)
            )

            ReviewRow(
                label = "Edades aproximadas",
                value = valueOrEmpty(AgeBreakdown.display(childrenAges, numberOfChildren.toIntOrNull() ?: 0))
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
ReportPhotoStrip(evidenceReferences)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // =========================================================
        // ENVÍO
        // =========================================================

        Button(
            onClick = onSendReportClick,
            enabled = true,
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
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    onFieldClick: (() -> Unit)? = null
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
            keyboardType = keyboardType,
            readOnly = readOnly,
            onFieldClick = onFieldClick,
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
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    onFieldClick: (() -> Unit)? = null
) {
    val colors = AppTheme.colors

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),

        textStyle = TextStyle(
            color = colors.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        ),

        cursorBrush = SolidColor(colors.pinkPrincipal),

        modifier = modifier
            .heightIn(min = minHeight)
            .then(if (onFieldClick != null) Modifier.clickable { onFieldClick() } else Modifier)
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
