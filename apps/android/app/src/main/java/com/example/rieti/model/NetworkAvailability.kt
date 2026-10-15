
package com.example.rieti.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Comprueba la disponibilidad de una conexión a Internet
 * validada antes de iniciar solicitudes de autenticación
 * dentro de la aplicación RIETI.
 *
 * Utiliza ConnectivityManager para consultar la red
 * activa del dispositivo y NetworkCapabilities para
 * verificar sus capacidades de conectividad.
 *
 * La conexión se considera disponible únicamente
 * cuando la red activa cuenta con las capacidades
 * NET_CAPABILITY_INTERNET y NET_CAPABILITY_VALIDATED.
 *
 * Si no existe un servicio de conectividad, una red
 * activa o información sobre sus capacidades,
 * la comprobación devuelve false.
 *
 * Esta verificación permite identificar si Android
 * considera que la red dispone de acceso a Internet
 * validado, aunque no garantiza que un servidor
 * específico se encuentre disponible.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
object NetworkAvailability {

    /**
     * Comprueba si el dispositivo dispone de una
     * conexión a Internet validada por Android.
     *
     * Obtiene el servicio ConnectivityManager
     * a partir del contexto proporcionado.
     *
     * Posteriormente, consulta la red activa
     * y recupera sus capacidades mediante
     * getNetworkCapabilities().
     *
     * La función devuelve true solamente cuando
     * la red activa tiene las capacidades
     * NET_CAPABILITY_INTERNET y
     * NET_CAPABILITY_VALIDATED.
     *
     * Si alguna de las consultas no proporciona
     * un resultado válido, devuelve false.
     *
     * @param context Contexto de Android utilizado
     * para acceder al servicio de conectividad.
     *
     * @return true si existe una red activa con
     * acceso a Internet validado; false si no existe
     * una conexión que cumpla ambas condiciones.
     */
    fun isOnline(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = connectivity.activeNetwork ?: return false
        val capabilities = connectivity.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
