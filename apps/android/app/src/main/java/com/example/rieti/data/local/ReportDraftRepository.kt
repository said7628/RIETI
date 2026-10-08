
package com.example.rieti.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repositorio de borradores de RIETI.
 *
 * Conecta Room con el cifrado AES-GCM.
 * El contenido sensible nunca se entrega
 * a Room sin cifrar.
 */
class ReportDraftRepository(
    private val dao: ReportDraftDao
) {

    /**
     * Guarda o actualiza un borrador.
     *
     * payload debe contener los datos del
     * formulario serializados como texto.
     */
    suspend fun saveDraft(
        id: String,
        mode: String,
        currentStep: Int,
        payload: String
    ) = withContext(Dispatchers.IO) {

        require(mode == "ANONYMOUS" || mode == "TRACKING") {
            "Modalidad de reporte no válida."
        }

        require(currentStep in 1..4) {
            "El paso del reporte debe estar entre 1 y 4."
        }

        val encryptedPayload =
            ReportDraftCrypto.encrypt(payload)

        dao.saveDraft(
            ReportDraftEntity(
                id = id,
                mode = mode,
                currentStep = currentStep,
                encryptedPayload = encryptedPayload,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * Recupera y descifra un borrador.
     *
     * Devuelve null cuando no existe.
     */
    suspend fun getDraftById(
        id: String
    ): SavedReportDraft? = withContext(Dispatchers.IO) {

        val draft = dao.getDraftById(id)
            ?: return@withContext null

        SavedReportDraft(
            id = draft.id,
            mode = draft.mode,
            currentStep = draft.currentStep,
            payload = ReportDraftCrypto.decrypt(
                draft.encryptedPayload
            )
        )
    }

    /**
     * Permite observar el borrador más reciente.
     *
     * Home podrá utilizar este Flow para mostrar
     * u ocultar la tarjeta "Continuar reporte".
     */
    fun observeLatestDraft(): Flow<ReportDraftEntity?> {
        return dao.observeLatestDraft()
    }

    /**
     * Elimina un borrador específico.
     *
     * No debe ejecutarse simplemente al pulsar
     * "Enviar" si todavía no existe confirmación.
     */
    suspend fun deleteDraft(
        id: String
    ) = withContext(Dispatchers.IO) {
        dao.deleteDraft(id)
    }
}

/**
 * Representa un borrador recuperado y descifrado.
 *
 * No debe almacenarse en registros de depuración
 * ni exponerse fuera de las capas que lo necesitan.
 */
data class SavedReportDraft(
    val id: String,
    val mode: String,
    val currentStep: Int,
    val payload: String
)
