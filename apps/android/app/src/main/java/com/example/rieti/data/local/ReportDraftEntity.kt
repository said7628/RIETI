
package com.example.rieti.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un borrador de reporte almacenado localmente.
 *
 * Define la estructura de la tabla "report_drafts"
 * utilizada por Room para conservar los borradores
 * de reportes ciudadanos de RIETI.
 *
 * Permite identificar el borrador, distinguir su modalidad,
 * conservar el paso actual del formulario y registrar
 * la fecha de su última modificación.
 *
 * IMPORTANTE:
 * Los campos sensibles se almacenarán cifrados antes
 * de insertarlos en Room.
 *
 * @property id Identificador único del borrador.
 * Se utiliza como clave primaria de la tabla.
 *
 * @property mode Modalidad del reporte.
 * Puede contener ANONYMOUS o TRACKING.
 *
 * @property currentStep Paso actual del formulario.
 * Su valor predeterminado es 1 y contempla los pasos del 1 al 4.
 *
 * @property encryptedPayload Contenido del formulario
 * que debe cifrarse antes de almacenarse en Room.
 *
 * @property updatedAt Momento de la última modificación
 * del borrador, expresado en milisegundos desde Unix epoch.
 * Su valor predeterminado corresponde al instante
 * de creación de la entidad.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 *  * Said Valseca Figueroa.
 *  */

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
