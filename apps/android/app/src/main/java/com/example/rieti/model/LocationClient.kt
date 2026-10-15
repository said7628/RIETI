
package com.example.rieti.model

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Cliente encargado de consultar la última ubicación
 * conocida del dispositivo dentro de RIETI.
 *
 * Utiliza los servicios de ubicación de Google Play
 * mediante FusedLocationProviderClient para recuperar
 * las coordenadas geográficas del dispositivo.
 *
 * Proporciona una función suspendida que permite
 * obtener la ubicación sin bloquear directamente
 * el hilo que realiza la solicitud.
 *
 * Cuando existe una ubicación disponible, convierte
 * sus coordenadas a un objeto LatLng.
 *
 * Si no se encuentra una ubicación registrada
 * o se produce un error durante la consulta,
 * devuelve null.
 *
 * Esta clase supone que los permisos de ubicación
 * necesarios ya fueron verificados y concedidos
 * antes de invocar la función correspondiente.
 *
 * @param context Contexto de Android utilizado
 * para inicializar el cliente de ubicación.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
class LocationClient(context: Context) {

    /**
     * Cliente de los servicios de ubicación de Google
     * encargado de recuperar la última ubicación
     * conocida del dispositivo.
     *
     * Se inicializa mediante LocationServices
     * utilizando el contexto proporcionado.
     */
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Obtiene la última ubicación conocida del dispositivo.
     *
     * Utiliza la propiedad lastLocation de
     * FusedLocationProviderClient para consultar
     * una ubicación previamente registrada.
     *
     * La operación se adapta a una función suspendida
     * mediante suspendCoroutine, permitiendo esperar
     * el resultado de los listeners de Google Play Services.
     *
     * Si la consulta finaliza correctamente y existe
     * una ubicación disponible, recupera su latitud
     * y longitud para construir un objeto LatLng.
     *
     * Si la ubicación es null o se produce un error,
     * reanuda la corrutina devolviendo null.
     *
     * La anotación SuppressLint("MissingPermission")
     * suprime la advertencia de Android Lint relacionada
     * con la comprobación de permisos.
     *
     * IMPORTANTE:
     * Esta función no solicita ni verifica permisos
     * de ubicación. El componente que la invoque
     * debe comprobar previamente que el usuario
     * haya concedido los permisos necesarios.
     *
     * @return Objeto LatLng con la latitud y longitud
     * de la última ubicación conocida, o null
     * si no existe una ubicación disponible
     * o si ocurre un error durante la consulta.
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): LatLng? =
        suspendCoroutine { continuation ->

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->

                    if (location != null) {
                        continuation.resume(
                            LatLng(
                                location.latitude,
                                location.longitude
                            )
                        )
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
}
