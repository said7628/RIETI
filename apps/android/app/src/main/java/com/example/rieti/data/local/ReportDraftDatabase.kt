
package com.example.rieti.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de datos local de RIETI.
 *
 * Administra los borradores de reportes que todavía
 * no han sido enviados.
 *
 * Utiliza la biblioteca Room para gestionar
 * la persistencia de información local mediante
 * la entidad ReportDraftEntity.
 *
 * La base de datos se encuentra configurada
 * con la versión 1 del esquema y proporciona
 * acceso a las operaciones definidas en ReportDraftDao.
 *
 * IMPORTANTE:
 * La información sensible deberá cifrarse antes
 * de almacenarse mediante Room.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
@Database(
    entities = [ReportDraftEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ReportDraftDatabase : RoomDatabase() {

    /**
     * Proporciona acceso a las operaciones
     * de almacenamiento de borradores.
     *
     * Devuelve la interfaz DAO encargada
     * de insertar, consultar, observar y eliminar
     * los registros de la tabla "report_drafts".
     *
     * Room genera automáticamente la implementación
     * de esta función a partir de la configuración
     * de la base de datos.
     *
     * @return Instancia de ReportDraftDao que permite
     * ejecutar las operaciones de persistencia local.
     */
    abstract fun reportDraftDao(): ReportDraftDao
}
