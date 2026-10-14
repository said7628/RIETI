
package com.example.rieti.data.local

import android.content.Context
import androidx.room.Room

/**
 * Proporciona una única instancia de la base de datos
 * local de borradores de RIETI.
 *
 * Implementa el patrón de diseño Singleton mediante
 * un objeto de Kotlin para centralizar el acceso
 * a la base de datos ReportDraftDatabase.
 *
 * Evita abrir múltiples instancias de Room
 * durante la ejecución de la aplicación.
 *
 * Utiliza inicialización diferida para crear
 * la base de datos únicamente cuando se solicita
 * por primera vez.
 *
 * Además, emplea @Volatile y synchronized para
 * coordinar el acceso concurrente a la instancia
 * y evitar inicializaciones duplicadas.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
object ReportDatabaseProvider {

    /**
     * Referencia compartida a la instancia de
     * la base de datos local de borradores.
     *
     * La anotación @Volatile permite que los cambios
     * realizados sobre esta variable sean visibles
     * entre los distintos hilos de ejecución.
     *
     * Inicialmente contiene null hasta que
     * se crea la primera instancia.
     */
    @Volatile
    private var INSTANCE: ReportDraftDatabase? = null

    /**
     * Obtiene la base de datos existente o crea una
     * nueva instancia cuando se utiliza por primera vez.
     *
     * Primero comprueba si INSTANCE ya contiene
     * una base de datos inicializada.
     *
     * Si todavía no existe, utiliza synchronized
     * para impedir que varios hilos creen
     * instancias simultáneamente.
     *
     * La base de datos se construye mediante
     * Room.databaseBuilder, utilizando el contexto
     * de aplicación y el nombre
     * "rieti_report_drafts.db".
     *
     * Finalmente, almacena la instancia creada
     * para reutilizarla en solicitudes posteriores.
     *
     * @param context Contexto de Android utilizado
     * para inicializar la base de datos local.
     *
     * @return Instancia compartida de ReportDraftDatabase,
     * existente o creada durante la primera solicitud.
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
