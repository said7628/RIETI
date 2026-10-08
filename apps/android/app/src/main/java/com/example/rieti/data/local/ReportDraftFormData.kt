
package com.example.rieti.data.local

/**
 * Datos que deben conservarse cuando un usuario
 * deja un reporte sin terminar.
 *
 * Este modelo representa el contenido del formulario,
 * no un reporte enviado al backend.
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

