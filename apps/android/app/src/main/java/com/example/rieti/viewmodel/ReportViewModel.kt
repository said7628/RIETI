package com.example.rieti.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rieti.model.LocationClient
import com.example.rieti.model.ReportUiState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun onNumberOfChildrenChange(value: String) {
        _uiState.update { it.copy(numberOfChildren = value) }
    }

    fun onChildrenAgesChange(value: String) {
        _uiState.update { it.copy(childrenAges = value) }
    }

    fun onWorkTypeChange(value: String) {
        _uiState.update { it.copy(workType = value) }
    }

    fun onWorkScheduleChange(value: String) {
        _uiState.update { it.copy(workSchedule = value) }
    }

    fun onDetailsChange(value: String) {
        _uiState.update { it.copy(details = value) }
    }

    fun onFullAddressChange(value: String) {
        _uiState.update { it.copy(fullAddress = value) }
    }

    fun onNeighborhoodChange(value: String) {
        _uiState.update { it.copy(neighborhood = value) }
    }

    fun onPostalCodeChange(value: String) {
        _uiState.update { it.copy(postalCode = value) }
    }

    fun onMunicipalityChange(value: String) {
        _uiState.update { it.copy(municipality = value) }
    }

    fun onMapLocationChanged(newLocation: LatLng) {
        _uiState.update { it.copy(selectedLocation = newLocation) }
    }

    fun fetchCurrentLocation(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true) }
            val locationClient = LocationClient(context)
            val location = locationClient.getLastLocation()
            if (location != null) {
                _uiState.update { it.copy(selectedLocation = location, isLoadingLocation = false) }
            } else {
                _uiState.update { it.copy(isLoadingLocation = false) }
            }
        }
    }

    fun sendReport() {
        val currentState = _uiState.value
        println("Enviando reporte con datos:")
        println("Coordenadas: ${currentState.selectedLocation.latitude}, ${currentState.selectedLocation.longitude}")
        println("Detalles: ${currentState.details}")
    }
}