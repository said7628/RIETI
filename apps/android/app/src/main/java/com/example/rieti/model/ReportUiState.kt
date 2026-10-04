package com.example.rieti.model

import com.google.android.gms.maps.model.LatLng

data class ReportUiState(
    val numberOfChildren: String = "",
    val childrenAges: String = "",
    val workType: String = "",
    val workSchedule: String = "",
    val details: String = "",
    val fullAddress: String = "",
    val neighborhood: String = "",
    val postalCode: String = "",
    val municipality: String = "",

    val selectedLocation: LatLng = LatLng(19.4330263, -99.1358623),
    val isLoadingLocation: Boolean = false
)