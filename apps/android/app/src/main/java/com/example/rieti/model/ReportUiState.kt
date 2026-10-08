
package com.example.rieti.model

import com.google.android.gms.maps.model.LatLng

/**
 * Estado del formulario de reportes de RIETI.
 *
 * Conserva los datos de las cuatro etapas del formulario.
 * El almacenamiento permanente se realizará mediante
 * el repositorio de borradores y Room.
 */
data class ReportUiState(

    // =========================================================
    // MODALIDAD Y NAVEGACIÓN DEL FORMULARIO
    // =========================================================

    val mode: String = "TRACKING",
    val currentStep: Int = 1,

    // =========================================================
    // PASO 1: INFORMACIÓN DEL REPORTE
    // =========================================================

    val email: String = "",
    val confirmEmail: String = "",

    val numberOfChildren: String = "",
    val childrenAges: String = "",
    val workType: String = "",
    val workSchedule: String = "",
    val details: String = "",

    // =========================================================
    // PASO 2: UBICACIÓN
    // =========================================================

    val fullAddress: String = "",
    val neighborhood: String = "",
    val postalCode: String = "",
    val municipality: String = "",

    val selectedLocation: LatLng =
        LatLng(19.4330263, -99.1358623),

    val isLoadingLocation: Boolean = false,

    // =========================================================
    // PASO 3: EVIDENCIAS
    // =========================================================

    // Se conectará cuando integremos el selector real
    // y el almacenamiento seguro de fotografías.
    val evidenceReferences: List<String> = emptyList()
)
