
package com.example.rieti.data.local

import android.content.Context
import androidx.room.Room

/**
 * Proporciona una única instancia de la base de datos
 * local de borradores de RIETI.
 *
 * Evita abrir múltiples instancias de Room
 * durante la ejecución de la aplicación.
 */
object ReportDatabaseProvider {

    @Volatile
    private var INSTANCE: ReportDraftDatabase? = null

    /**
     * Obtiene la base de datos existente o crea una
     * nueva instancia cuando se utiliza por primera vez.
     */
    fun getDatabase(context: Context): ReportDraftDatabase {

        return INSTANCE ?: synchronized(this) {

            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                ReportDraftDatabase::class.java,
                "rieti_report_drafts.db"
            ).build().also { database ->

                INSTANCE = database
            }
        }
    }
}
