
package com.example.rieti.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Define las operaciones de acceso a los borradores
 * de reportes almacenados localmente en RIETI.
 *
 * Room utiliza esta interfaz para generar automáticamente
 * las operaciones de base de datos.
 *
 * Permite guardar y actualizar borradores, recuperarlos
 * mediante su identificador, observar los cambios en los
 * registros almacenados y eliminar borradores específicos.
 *
 * Las consultas se realizan sobre la tabla "report_drafts",
 * definida mediante la entidad ReportDraftEntity.
 *
 * @author Equipo 3:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
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
