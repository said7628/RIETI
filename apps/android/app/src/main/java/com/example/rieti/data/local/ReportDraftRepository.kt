
package com.example.rieti.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repositorio de borradores de RIETI.
 *
 * Conecta las operaciones de persistencia de Room
 * con el mecanismo de cifrado AES-GCM implementado
 * mediante ReportDraftCrypto.
 *
 * Permite guardar, actualizar, recuperar, observar
 * y eliminar borradores de reportes que todavía
 * no han sido enviados al backend.
 *
 * Antes de almacenar un borrador, cifra el contenido
 * sensible del formulario para evitar que se entregue
 * a Room en texto sin cifrar.
 *
 * Al recuperar un borrador, descifra su contenido
 * para permitir que el usuario continúe llenando
 * el formulario desde el paso correspondiente.
 *
 * Las operaciones suspendidas de almacenamiento
 * se ejecutan utilizando Dispatchers.IO.
 *
 * @property dao Interfaz de acceso a datos utilizada
 * para realizar las operaciones sobre los borradores.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
class ReportDraftRepository(
    private val dao: ReportDraftDao
) {

    /**
     * Guarda o actualiza un borrador de reporte.
     *
     * Recibe los datos del formulario serializados
     * como texto y valida la modalidad del reporte
     * y el paso actual antes de almacenarlos.
     *
     * Únicamente acepta las modalidades ANONYMOUS
     * y TRACKING, así como pasos comprendidos
     * entre 1 y 4.
     *
     * Posteriormente, cifra el contenido mediante
     * ReportDraftCrypto.encrypt() y construye
     * una entidad ReportDraftEntity.
     *
     * La entidad almacena el identificador,
     * la modalidad, el paso actual, el contenido
     * cifrado y la fecha de actualización.
     *
     * La operación se ejecuta en Dispatchers.IO.
     *
     * @param id Identificador único del borrador.
     * @param mode Modalidad del reporte, que debe
     * ser ANONYMOUS o TRACKING.
     * @param currentStep Paso actual del formulario,
     * comprendido entre 1 y 4.
     * @param payload Datos del formulario serializados
     * como una cadena de texto.
     *
     * @throws IllegalArgumentException Si la modalidad
     * o el paso actual no cumplen las validaciones.
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
     * Recupera y descifra un borrador almacenado.
     *
     * Consulta la base de datos local mediante
     * el identificador proporcionado.
     *
     * Si el borrador existe, recupera sus datos
     * y descifra el contenido sensible mediante
     * ReportDraftCrypto.decrypt().
     *
     * Finalmente, construye un objeto SavedReportDraft
     * con los datos necesarios para continuar
     * el llenado del formulario.
     *
     * Si no existe un registro con el identificador
     * solicitado, devuelve null.
     *
     * La operación se ejecuta en Dispatchers.IO.
     *
     * @param id Identificador del borrador
     * que se desea recuperar.
     *
     * @return Instancia de SavedReportDraft con
     * el contenido descifrado, o null si no existe.
     *
     * @throws java.security.GeneralSecurityException
     * Si ocurre un error durante el descifrado
     * o la verificación de autenticidad del contenido.
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
     * Devuelve el Flow proporcionado por el DAO
     * para recibir actualizaciones cuando cambia
     * el borrador más reciente almacenado en Room.
     *
     * La pantalla de inicio puede utilizar
     * este flujo para determinar si debe mostrar
     * u ocultar la tarjeta "Continuar reporte".
     *
     * Esta función devuelve la entidad almacenada
     * en Room, cuyo contenido permanece cifrado.
     * No realiza el descifrado del borrador.
     *
     * @return Flow que emite el borrador más reciente
     * como ReportDraftEntity, o null si no existe.
     */
    fun observeLatestDraft(): Flow<ReportDraftEntity?> {
        return dao.observeLatestDraft()
    }

    /**
     * Elimina un borrador específico.
     *
     * Solicita al DAO eliminar el registro
     * asociado con el identificador recibido.
     *
     * Esta operación se ejecuta en Dispatchers.IO.
     *
     * No debe ejecutarse simplemente al pulsar
     * "Enviar" si todavía no existe confirmación
     * de que el reporte fue recibido correctamente.
     *
     * @param id Identificador del borrador
     * que se desea eliminar.
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
 * Contiene los datos necesarios para identificar
 * el reporte, conocer su modalidad, recuperar
 * el paso actual y restaurar el formulario.
 *
 * A diferencia de ReportDraftEntity, esta clase
 * contiene el payload en texto descifrado.
 *
 * Por motivos de seguridad, su contenido no debe
 * almacenarse en registros de depuración ni
 * exponerse fuera de las capas que lo necesitan.
 *
 * @property id Identificador único del borrador.
 * @property mode Modalidad del reporte almacenado.
 * @property currentStep Paso del formulario
 * desde el cual puede continuar el usuario.
 * @property payload Contenido descifrado
 * del formulario del reporte.
 *
 */
data class SavedReportDraft(
    val id: String,
    val mode: String,
    val currentStep: Int,
    val payload: String
)
