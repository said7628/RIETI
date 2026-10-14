
package com.example.rieti.data.local

/**
 * Datos que deben conservarse cuando un usuario
 * deja un reporte sin terminar.
 *
 * Este modelo representa el contenido del formulario,
 * no un reporte enviado al backend.
 *
 * Agrupa la información general del reporte, los datos
 * de ubicación, las coordenadas seleccionadas y las
 * referencias a las evidencias fotográficas.
 *
 * Sus propiedades cuentan con valores predeterminados
 * para permitir la creación de formularios inicialmente vacíos.
 *
 * @property email Correo electrónico proporcionado
 * durante el llenado del formulario.
 * @property confirmEmail Confirmación del correo electrónico.
 * @property numberOfChildren Cantidad de menores de edad
 * involucrados en la situación reportada.
 * @property childrenAges Rango aproximado de edad
 * de los menores de edad.
 * @property workType Tipo de actividad laboral identificada.
 * @property workSchedule Horario aproximado en el que
 * ocurre la situación reportada.
 * @property details Descripción de la situación observada.
 * @property fullAddress Dirección completa del lugar reportado.
 * @property neighborhood Colonia correspondiente a la ubicación.
 * @property postalCode Código postal de la ubicación.
 * @property municipality Municipio donde ocurre la situación.
 * @property latitude Latitud seleccionada en el formulario,
 * o null si no existe una coordenada disponible.
 * @property longitude Longitud seleccionada en el formulario,
 * o null si no existe una coordenada disponible.
 * @property evidenceReferences Lista de referencias
 * a las evidencias fotográficas seleccionadas.
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
data class ReportDraftFormData(

    // Paso 1: información general
    val email: String = "",
    val confirmEmail: String = "",
    val numberOfChildren: String = "",
    val childrenAges: String = "",
    val workType: String = "",
    val workSchedule: String = "",
    val details: String = "",

    // Paso 2: ubicación
    val fullAddress: String = "",
    val neighborhood: String = "",
    val postalCode: String = "",
    val municipality: String = "",

    // Coordenadas seleccionadas
    val latitude: Double? = null,
    val longitude: Double? = null,

    // Paso 3: evidencia
    // Se integrará cuando exista un selector real
    // y almacenamiento seguro de fotografías.
    val evidenceReferences: List<String> = emptyList()
)

/**
 * Convierte los datos del formulario de un borrador
 * a una representación de texto en formato JSON.
 *
 * Incorpora los campos de información general,
 * ubicación y referencias a evidencias.
 *
 * Las coordenadas latitude y longitude solamente
 * se agregan al objeto JSON cuando contienen
 * un valor distinto de null.
 *
 * Las referencias a evidencias se convierten
 * a un arreglo JSON mediante JSONArray.
 *
 * Esta función realiza la serialización de los datos,
 * pero no aplica cifrado sobre el contenido generado.
 *
 * @receiver Instancia de ReportDraftFormData
 * cuyos valores serán convertidos a JSON.
 *
 * @return Cadena de texto JSON con la información
 * del formulario del borrador.
 */
fun ReportDraftFormData.toJson(): String {
    return org.json.JSONObject().apply {
        put("email", email)
        put("confirmEmail", confirmEmail)
        put("numberOfChildren", numberOfChildren)
        put("childrenAges", childrenAges)
        put("workType", workType)
        put("workSchedule", workSchedule)
        put("details", details)
        put("fullAddress", fullAddress)
        put("neighborhood", neighborhood)
        put("postalCode", postalCode)
        put("municipality", municipality)

        latitude?.let { put("latitude", it) }
        longitude?.let { put("longitude", it) }

        put(
            "evidenceReferences",
            org.json.JSONArray(evidenceReferences)
        )
    }.toString()
}

/**
 * Reconstruye los datos de un formulario de borrador
 * a partir de una cadena de texto en formato JSON.
 *
 * Interpreta las propiedades del objeto JSON y crea
 * una nueva instancia de ReportDraftFormData.
 *
 * Los campos de texto se recuperan mediante optString,
 * mientras que las coordenadas se asignan como null
 * cuando no existen o contienen un valor JSON nulo.
 *
 * Si no existe un arreglo de referencias a evidencias,
 * se utiliza una lista vacía como valor predeterminado.
 *
 * Esta función realiza la deserialización del contenido,
 * pero no descifra información previamente cifrada.
 *
 * @param json Cadena de texto JSON que contiene
 * los datos almacenados del formulario.
 *
 * @return Instancia de ReportDraftFormData con los
 * valores recuperados del objeto JSON.
 *
 * @throws org.json.JSONException Si el contenido JSON
 * es inválido o contiene valores incompatibles
 * con las conversiones requeridas.
 */

fun reportDraftFormDataFromJson(json: String): ReportDraftFormData {
    val obj = org.json.JSONObject(json)
    val evidence = obj.optJSONArray("evidenceReferences")

    return ReportDraftFormData(
        email = obj.optString("email"),
        confirmEmail = obj.optString("confirmEmail"),
        numberOfChildren = obj.optString("numberOfChildren"),
        childrenAges = obj.optString("childrenAges"),
        workType = obj.optString("workType"),
        workSchedule = obj.optString("workSchedule"),
        details = obj.optString("details"),
        fullAddress = obj.optString("fullAddress"),
        neighborhood = obj.optString("neighborhood"),
        postalCode = obj.optString("postalCode"),
        municipality = obj.optString("municipality"),
        latitude = if (obj.has("latitude") && !obj.isNull("latitude"))
            obj.getDouble("latitude") else null,
        longitude = if (obj.has("longitude") && !obj.isNull("longitude"))
            obj.getDouble("longitude") else null,
        evidenceReferences = evidence?.let { array ->
            List(array.length()) { index ->
                array.getString(index)
            }
        } ?: emptyList()
    )
}

