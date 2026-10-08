
package com.example.rieti.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un borrador de reporte almacenado localmente.
 *
 * IMPORTANTE:
 * Los campos sensibles se almacenarán cifrados antes
 * de insertarlos en Room.
 */
@Entity(tableName = "report_drafts")
data class ReportDraftEntity(

    @PrimaryKey
    val id: String,

    // ANONYMOUS o TRACKING
    val mode: String,

    // Paso actual del formulario (1 a 4)
    val currentStep: Int = 1,

    // Información del reporte, cifrada antes de guardarse.
    val encryptedPayload: String,

    // Momento de la última modificación.
    val updatedAt: Long = System.currentTimeMillis()
)
