
package com.example.rieti.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de datos local de RIETI.
 *
 * Administra los borradores de reportes que todavía
 * no han sido enviados.
 *
 * IMPORTANTE:
 * La información sensible deberá cifrarse antes
 * de almacenarse mediante Room.
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
     */
    abstract fun reportDraftDao(): ReportDraftDao
}
