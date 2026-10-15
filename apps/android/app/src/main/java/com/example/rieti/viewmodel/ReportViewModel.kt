package com.example.rieti.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rieti.data.local.ReportDatabaseProvider
import com.example.rieti.data.local.ReportDraftFormData
import com.example.rieti.data.local.ReportDraftRepository
import com.example.rieti.data.local.reportDraftFormDataFromJson
import com.example.rieti.data.local.toJson
import com.example.rieti.model.LocationClient
import com.example.rieti.model.ReportUiState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Administra el estado del formulario de reportes de RIETI.
 *
 * Incluye:
 * - Actualización de campos.
 * - Selección de ubicación.
 * - Guardado automático de borradores.
 * - Recuperación de borradores cifrados.
 * - Protección ante errores de recuperación.
 */
class ReportViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ReportDraftRepository(
        ReportDatabaseProvider
            .getDatabase(application)
            .reportDraftDao()
    )

    private val _uiState = MutableStateFlow(
        ReportUiState()
    )

    val uiState: StateFlow<ReportUiState> =
        _uiState.asStateFlow()

    private val _isDraftLoaded = MutableStateFlow(false)

    val isDraftLoaded: StateFlow<Boolean> =
        _isDraftLoaded.asStateFlow()

    private val _draftError = MutableStateFlow<String?>(null)

    val draftError: StateFlow<String?> =
        _draftError.asStateFlow()

    private var activeDraftId: String? = null

    private var saveJob: Job? = null
    private var loadJob: Job? = null

    // =============================================================
    // ABRIR O RECUPERAR BORRADOR
    // =============================================================

    /**
     * Abre un reporte nuevo o recupera un borrador existente.
     *
     * Solo se habilita la edición cuando la carga
     * finaliza correctamente.
     */
    fun openDraft(
        mode: String,
        draftId: String? = null
    ) {

        require(
            mode == "ANONYMOUS" ||
                    mode == "TRACKING"
        ) {
            "Modalidad de reporte no válida."
        }

        saveJob?.cancel()
        loadJob?.cancel()

        val id =
            draftId ?: UUID.randomUUID().toString()

        activeDraftId = null
        _isDraftLoaded.value = false
        _draftError.value = null

        _uiState.value = ReportUiState(
            mode = mode
        )

        loadJob = viewModelScope.launch {

            try {

                if (draftId != null) {

                    val saved =
                        repository.getDraftById(id)
                            ?: throw IllegalStateException(
                                "El borrador solicitado no existe."
                            )

                    require(saved.mode == mode) {
                        "La modalidad del borrador no coincide."
                    }

                    require(saved.currentStep in 1..4) {
                        "El paso guardado no es válido."
                    }

                    val data =
                        reportDraftFormDataFromJson(
                            saved.payload
                        )

                    _uiState.value = ReportUiState(
                        mode = mode,
                        currentStep = saved.currentStep,

                        email = data.email,
                        confirmEmail = data.confirmEmail,

                        numberOfChildren =
                            data.numberOfChildren,

                        childrenAges =
                            data.childrenAges,

                        workType = data.workType,
                        workSchedule = data.workSchedule,
                        details = data.details,

                        fullAddress =
                            data.fullAddress,

                        neighborhood =
                            data.neighborhood,

                        postalCode =
                            data.postalCode,

                        municipality =
                            data.municipality,

                        selectedLocation =
                            if (
                                data.latitude != null &&
                                data.longitude != null
                            ) {
                                LatLng(
                                    data.latitude,
                                    data.longitude
                                )
                            } else {
                                ReportUiState()
                                    .selectedLocation
                            },

                        evidenceReferences =
                            data.evidenceReferences
                    )
                }

                // Solo después de una carga correcta
                // habilitamos las modificaciones.

                activeDraftId = id
                _isDraftLoaded.value = true

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                // Nunca permitir guardar encima
                // de un borrador que no se recuperó.

                activeDraftId = null
                _isDraftLoaded.value = false

                _draftError.value =
                    "No fue posible recuperar el borrador. " +
                            "La información guardada no se modificó."
            }
        }
    }

    // =============================================================
    // CONVERTIR ESTADO A DATOS PERSISTENTES
    // =============================================================

    private fun currentFormData(): ReportDraftFormData {

        val state = _uiState.value

        return ReportDraftFormData(
            email = state.email,
            confirmEmail = state.confirmEmail,

            numberOfChildren =
                state.numberOfChildren,

            childrenAges =
                state.childrenAges,

            workType = state.workType,
            workSchedule = state.workSchedule,
            details = state.details,

            fullAddress = state.fullAddress,
            neighborhood = state.neighborhood,
            postalCode = state.postalCode,
            municipality = state.municipality,

            latitude =
                state.selectedLocation.latitude,

            longitude =
                state.selectedLocation.longitude,

            evidenceReferences =
                state.evidenceReferences
        )
    }

    // =============================================================
    // COMPROBAR CONTENIDO
    // =============================================================

    private fun hasContent(
        data: ReportDraftFormData
    ): Boolean {

        return data.email.isNotBlank() ||
                data.confirmEmail.isNotBlank() ||
                data.numberOfChildren.isNotBlank() ||
                data.childrenAges.isNotBlank() ||
                data.workType.isNotBlank() ||
                data.workSchedule.isNotBlank() ||
                data.details.isNotBlank() ||
                data.fullAddress.isNotBlank() ||
                data.neighborhood.isNotBlank() ||
                data.postalCode.isNotBlank() ||
                data.municipality.isNotBlank() ||
                data.evidenceReferences.isNotEmpty()
    }

    // =============================================================
    // GUARDADO AUTOMÁTICO
    // =============================================================

    /**
     * Guarda los cambios 500 ms después
     * de la última modificación.
     */
    private fun scheduleSave() {

        if (!_isDraftLoaded.value) return

        val id = activeDraftId ?: return
        val state = _uiState.value
        val data = currentFormData()

        saveJob?.cancel()

        if (!hasContent(data)) return

        saveJob = viewModelScope.launch {

            try {

                delay(500)

                repository.saveDraft(
                    id = id,
                    mode = state.mode,
                    currentStep = state.currentStep,
                    payload = data.toJson()
                )

                _draftError.value = null

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _draftError.value =
                    "No fue posible guardar el borrador."
            }
        }
    }

    // =============================================================
    // GUARDADO INMEDIATO
    // =============================================================

    /**
     * Guarda el estado actual antes de navegar.
     *
     * Si ocurre un error, lo propaga para que
     * la pantalla pueda impedir la navegación.
     */
    suspend fun saveDraftNow() {

        if (!_isDraftLoaded.value) {
            throw IllegalStateException(
                "El borrador todavía no está disponible."
            )
        }

        val id = activeDraftId
            ?: throw IllegalStateException(
                "No existe un borrador activo."
            )

        saveJob?.cancel()

        val state = _uiState.value
        val data = currentFormData()

        if (!hasContent(data)) return

        try {

            repository.saveDraft(
                id = id,
                mode = state.mode,
                currentStep = state.currentStep,
                payload = data.toJson()
            )

            _draftError.value = null

        } catch (e: CancellationException) {

            throw e

        } catch (e: Exception) {

            _draftError.value =
                "No fue posible guardar el borrador."

            throw e
        }
    }

    // =============================================================
    // ACTUALIZAR FORMULARIO
    // =============================================================

    private fun updateReport(
        transform: (ReportUiState) -> ReportUiState
    ) {

        // No modificar el estado durante una
        // recuperación pendiente o fallida.

        if (!_isDraftLoaded.value) return

        if (activeDraftId == null) return

        _uiState.update(transform)

        scheduleSave()
    }


// =============================================================
// MODALIDAD Y PASO ACTUAL
// =============================================================

    /**
     * Actualiza el paso actual del formulario.
     *
     * Solo permite valores entre 1 y 4.
     */
    fun onStepChange(step: Int) {

        require(step in 1..4) {
            "El paso debe estar entre 1 y 4."
        }

        updateReport {
            it.copy(currentStep = step)
        }
    }

// =============================================================
// CORREO
// =============================================================

    fun onEmailChange(value: String) {

        updateReport {
            it.copy(email = value)
        }
    }

    fun onConfirmEmailChange(value: String) {

        updateReport {
            it.copy(confirmEmail = value)
        }
    }

// =============================================================
// INFORMACIÓN DEL REPORTE
// =============================================================

    fun onNumberOfChildrenChange(value: String) {

        updateReport {
            it.copy(numberOfChildren = value)
        }
    }

    fun onChildrenAgesChange(value: String) {

        updateReport {
            it.copy(childrenAges = value)
        }
    }

    fun onWorkTypeChange(value: String) {

        updateReport {
            it.copy(workType = value)
        }
    }

    fun onWorkScheduleChange(value: String) {

        updateReport {
            it.copy(workSchedule = value)
        }
    }

    fun onDetailsChange(value: String) {

        updateReport {
            it.copy(details = value)
        }
    }

// =============================================================
// UBICACIÓN
// =============================================================

    fun onFullAddressChange(value: String) {

        updateReport {
            it.copy(fullAddress = value)
        }
    }

    fun onNeighborhoodChange(value: String) {

        updateReport {
            it.copy(neighborhood = value)
        }
    }

    fun onPostalCodeChange(value: String) {

        updateReport {
            it.copy(postalCode = value)
        }
    }

    fun onMunicipalityChange(value: String) {

        updateReport {
            it.copy(municipality = value)
        }
    }

    /**
     * Actualiza la ubicación seleccionada en el mapa.
     *
     * El cambio también se incluye en el guardado
     * automático del borrador.
     */
    fun onMapLocationChanged(
        newLocation: LatLng
    ) {

        updateReport {
            it.copy(
                selectedLocation = newLocation
            )
        }
    }

    /**
     * Obtiene la última ubicación disponible.
     *
     * Se conserva la integración existente
     * con LocationClient.
     */
    fun fetchCurrentLocation(
        context: Context,
        onLocationFound: (LatLng) -> Unit = {}
    ) {

        if (!_isDraftLoaded.value) return

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoadingLocation = true
                )
            }

            try {

                val locationClient = LocationClient(
                    context.applicationContext
                )

                val location =
                    locationClient.getLastLocation()

                if (location != null) {

                    onMapLocationChanged(
                        location
                    )
                    onLocationFound(location)
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                // No alteramos la ubicación anterior
                // cuando falla la consulta.

                _draftError.value =
                    "No fue posible obtener la ubicación actual."

            } finally {

                _uiState.update {
                    it.copy(
                        isLoadingLocation = false
                    )
                }
            }
        }
    }

// =============================================================
// EVIDENCIAS
// =============================================================

    /**
     * Actualiza las referencias de las evidencias.
     *
     * TODO(EVIDENCE):
     * Cuando se integre el selector de archivos,
     * conservar referencias a copias locales
     * persistentes y protegidas.
     */
    fun onEvidenceReferencesChange(
        references: List<String>
    ) {

        updateReport {
            it.copy(
                evidenceReferences = references
            )
        }
    }

// =============================================================
// ENVÍO DEL REPORTE
// =============================================================

    /**
     * Pendiente de integración con el backend.
     *
     * IMPORTANTE:
     * No debemos eliminar el borrador ni mostrar
     * un folio hasta confirmar que el servidor
     * registró correctamente el reporte.
     */
    private val _sending = MutableStateFlow(false)
    val sending: StateFlow<Boolean> = _sending.asStateFlow()
    private val _sendError = MutableStateFlow<String?>(null)
    val sendError: StateFlow<String?> = _sendError.asStateFlow()

    /** Envia el reporte y sus imagenes sin borrar el borrador ante errores. */
    suspend fun sendReport(): String? {
        if (!_isDraftLoaded.value || activeDraftId == null || _sending.value) return null
        _sending.value = true
        _sendError.value = null
        return try {
            saveDraftNow()
            val folio = com.example.rieti.data.repository.ReportSubmissionService(
                getApplication()
            ).submit(_uiState.value, activeDraftId!!)
            saveJob?.cancel()
            repository.deleteDraft(activeDraftId!!)
            activeDraftId = null
            _isDraftLoaded.value = false
            folio
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            _sendError.value = e.message ?: "No se pudo enviar el reporte"
            null
        } finally {
            _sending.value = false
        }
    }
}



