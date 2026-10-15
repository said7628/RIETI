
package com.example.rieti.model

/**
 * Administra la distribución de menores de edad
 * por rangos de edad dentro de los reportes de RIETI.
 *
 * Define los rangos permitidos y proporciona
 * funciones para interpretar, codificar, mostrar
 * y validar la cantidad de menores correspondiente
 * a cada intervalo.
 *
 * Mantiene compatibilidad con borradores antiguos
 * que almacenaban un único rango de edad.
 *
 * También permite trabajar con distribuciones
 * que contienen varios rangos y sus respectivas
 * cantidades, utilizando el formato "rango:cantidad"
 * separado por punto y coma.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
object AgeBreakdown {

    /**
     * Lista de rangos de edad permitidos
     * para clasificar a los menores de edad.
     *
     * Los intervalos establecidos son:
     * 0-5, 6-11, 12-14 y 15-17 años.
     */
    val ranges = listOf("0-5", "6-11", "12-14", "15-17")

    /**
     * Interpreta una representación textual
     * de la distribución por rangos de edad.
     *
     * Permite recuperar tanto los formatos actuales
     * como los utilizados en borradores antiguos.
     *
     * Si el valor recibido corresponde directamente
     * a uno de los rangos permitidos, asigna a dicho
     * rango la cantidad total proporcionada,
     * utilizando como mínimo una persona.
     *
     * Para distribuciones con varios rangos,
     * separa los elementos mediante punto y coma
     * y obtiene cada pareja "rango:cantidad".
     *
     * Descarta los elementos que contienen rangos
     * no permitidos, cantidades inválidas
     * o valores menores o iguales a cero.
     *
     * @param value Cadena que representa un rango
     * individual o una distribución de edades.
     * @param total Cantidad total de menores utilizada
     * para interpretar los formatos antiguos.
     * Su valor predeterminado es cero.
     *
     * @return Mapa que relaciona cada rango válido
     * con la cantidad de menores correspondiente.
     */
    fun read(
        value: String,
        total: Int = 0
    ): Map<String, Int> {

        if (value in ranges) {
            return mapOf(value to total.coerceAtLeast(1))
        }

        return value.split(';').mapNotNull { item ->
            val pair = item.split(':', limit = 2)
            val count = pair.getOrNull(1)?.toIntOrNull()

            pair.firstOrNull()
                ?.takeIf {
                    it in ranges && count != null && count > 0
                }
                ?.let { it to count!! }
        }.toMap()
    }

    /**
     * Convierte una distribución de edades
     * a una representación textual.
     *
     * Recorre los rangos permitidos en el orden
     * establecido y recupera las cantidades
     * correspondientes de cada uno.
     *
     * Únicamente incluye los rangos cuya cantidad
     * es mayor que cero.
     *
     * Cada elemento se representa mediante
     * el formato "rango:cantidad" y se separa
     * de los demás utilizando punto y coma.
     *
     * @param counts Mapa que contiene los rangos
     * de edad y sus cantidades correspondientes.
     *
     * @return Cadena con la distribución codificada.
     * Devuelve una cadena vacía si no existen
     * cantidades positivas en los rangos permitidos.
     */
    fun encode(
        counts: Map<String, Int>
    ): String = ranges.mapNotNull { range ->

        counts[range]
            ?.takeIf { it > 0 }
            ?.let { "$range:$it" }

    }.joinToString(";")

    /**
     * Genera una representación legible
     * de la distribución por rangos de edad.
     *
     * Primero interpreta el contenido mediante
     * read() y posteriormente convierte cada
     * pareja de rango y cantidad a un texto
     * con el formato "rango años: cantidad".
     *
     * Los elementos se separan mediante comas
     * para facilitar su presentación en la interfaz.
     *
     * @param value Distribución de edades
     * almacenada como cadena de texto.
     * @param total Cantidad total de menores
     * utilizada para interpretar formatos antiguos.
     *
     * @return Cadena legible con los rangos
     * y cantidades correspondientes.
     */
    fun display(
        value: String,
        total: Int = 0
    ): String = read(value, total)
        .entries.joinToString(", ") {
            "${it.key} años: ${it.value}"
        }

    /**
     * Valida que la distribución de edades
     * coincida con el total de menores reportados.
     *
     * Comprueba que el total proporcionado
     * sea mayor que cero.
     *
     * Posteriormente, interpreta la distribución
     * mediante read() y suma las cantidades
     * correspondientes a los rangos válidos.
     *
     * La distribución se considera válida
     * únicamente cuando dicha suma coincide
     * exactamente con el total indicado.
     *
     * @param value Distribución de edades
     * que se desea validar.
     * @param total Cantidad total de menores
     * declarada en el formulario.
     *
     * @return true si el total es positivo
     * y coincide con la suma de las cantidades;
     * false en caso contrario.
     */
    fun valid(
        value: String,
        total: Int
    ): Boolean =
        total > 0 &&
                read(value, total).values.sum() == total
}
