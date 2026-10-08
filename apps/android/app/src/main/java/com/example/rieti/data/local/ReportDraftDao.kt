
package com.example.rieti.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Acceso a los borradores almacenados localmente.
 *
 * Room utiliza esta interfaz para generar automáticamente
 * las operaciones de base de datos.
 */
@Dao
interface ReportDraftDao {

    /**
     * Guarda un borrador nuevo o actualiza uno existente.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: ReportDraftEntity)

    /**
     * Recupera un borrador por su identificador.
     */
    @Query("SELECT * FROM report_drafts WHERE id = :draftId LIMIT 1")
    suspend fun getDraftById(draftId: String): ReportDraftEntity?

    /**
     * Observa el borrador más recientemente modificado.
     *
     * Flow permite actualizar automáticamente Home
     * cuando aparece, cambia o desaparece un borrador.
     */
    @Query("SELECT * FROM report_drafts ORDER BY updatedAt DESC LIMIT 1")
    fun observeLatestDraft(): Flow<ReportDraftEntity?>

    /**
     * Recupera todos los borradores guardados.
     */
    @Query("SELECT * FROM report_drafts ORDER BY updatedAt DESC")
    fun observeAllDrafts(): Flow<List<ReportDraftEntity>>

    /**
     * Elimina un borrador específico.
     *
     * Se utilizará cuando corresponda descartarlo
     * o después de confirmar su envío al backend.
     */
    @Query("DELETE FROM report_drafts WHERE id = :draftId")
    suspend fun deleteDraft(draftId: String)
}
