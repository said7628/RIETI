
package com.example.rieti.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Propiedad de extensión de Context que proporciona
 * acceso a DataStore para almacenar las preferencias
 * visuales de la aplicación RIETI.
 *
 * Utiliza Preferences DataStore con el nombre
 * "theme_preferences" para conservar la configuración
 * seleccionada por el usuario.
 */
private val Context.dataStore by preferencesDataStore(
    name = "theme_preferences"
)

/**
 * Administra la persistencia de la preferencia
 * del modo oscuro de la aplicación RIETI.
 *
 * Utiliza Preferences DataStore para almacenar
 * y recuperar la configuración del tema visual.
 *
 * Expone un Flow<Boolean> que permite observar
 * los cambios de la preferencia y actualizar
 * la interfaz cuando se modifica el modo oscuro.
 *
 * Si todavía no existe una preferencia almacenada,
 * utiliza false como valor predeterminado,
 * correspondiente al modo claro.
 *
 * @property context Contexto de Android utilizado
 * para acceder al almacenamiento de preferencias.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
class ThemePreferences(
    private val context: Context
) {

    /**
     * Contiene las claves utilizadas para
     * identificar las preferencias del tema.
     */
    companion object {

        /**
         * Clave booleana utilizada para almacenar
         * la selección del modo oscuro.
         *
         * Se identifica mediante el nombre
         * "dark_theme" dentro de DataStore.
         */
        private val DARK_THEME_KEY =
            booleanPreferencesKey("dark_theme")
    }

    /**
     * Flujo que proporciona el estado almacenado
     * de la preferencia del modo oscuro.
     *
     * Observa los datos de DataStore y recupera
     * el valor asociado con DARK_THEME_KEY.
     *
     * Si la preferencia no ha sido configurada,
     * devuelve false como valor predeterminado.
     *
     * @return Flow<Boolean> que emite true cuando
     * el modo oscuro está seleccionado y false
     * cuando se utiliza el modo claro.
     */
    val darkTheme: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }

    /**
     * Guarda la preferencia del modo oscuro
     * seleccionada por el usuario.
     *
     * Utiliza la operación edit de DataStore
     * para actualizar el valor asociado
     * con DARK_THEME_KEY.
     *
     * Al finalizar la actualización, el flujo
     * darkTheme puede emitir el nuevo valor
     * a los componentes que lo estén observando.
     *
     * @param darkTheme Valor booleano que indica
     * si debe utilizarse el modo oscuro.
     * true representa el modo oscuro y
     * false representa el modo claro.
     */
    suspend fun saveDarkTheme(
        darkTheme: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = darkTheme
        }
    }
}
