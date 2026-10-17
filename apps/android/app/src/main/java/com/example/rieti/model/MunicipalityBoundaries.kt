package com.example.rieti.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.Normalizer
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Identifica municipios con coordenadas y el Marco Geoestadistico Municipal del INEGI.
 * Nunca interpreta locality, subLocality ni subAdminArea como un municipio confirmado.
 * La geometria es GeoJSON (WGS84: [longitud, latitud]); incluye islas y huecos.
 * Fuente: https://gaia.inegi.org.mx/wscatgeo/v2/geo/mgem/{cve_ent}
 *
 * @author Equipo de desarrollo RIETI:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
class MunicipalityBoundaries(private val context: Context) {
    data class Municipality(val name: String, val geoCode: String)

    private val http = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(35, TimeUnit.SECONDS)
        .build()

    /** Solo la clave de la ENTIDAD viene del geocodificador, nunca el municipio. */
    fun stateCode(state: String?): String? {
        val normalized = normalize(state.orEmpty())
            .removePrefix("estado de ").removePrefix("estado del ")
        return states[normalized]
    }

    /** Busca en los limites territoriales; si no hay geometria valida, no adivina. */
    suspend fun resolve(latitude: Double, longitude: Double, stateHint: String? = null): Municipality? =
        withContext(Dispatchers.IO) {
            require(latitude in -90.0..90.0 && longitude in -180.0..180.0)
            val preferred = stateCode(stateHint)
            val available = context.filesDir.listFiles().orEmpty()
                .mapNotNull { Regex("rieti-inegi-mgem-([0-9]{2})\\.geojson").matchEntire(it.name)?.groupValues?.get(1) }
            // Zona metropolitana: comprobar AMBAS entidades antes de aceptar un municipio.
            // Un poligono descargado previamente no tiene prioridad sobre CDMX.
            val nearBorder = latitude in 19.0..19.8 && longitude in -99.6..-98.8
            val likely = if (latitude in 18.0..21.0 && longitude in -101.0..-98.0)
                listOf("09", "15") else emptyList()
            val candidates = if (nearBorder) listOf("09", "15")
                else (listOfNotNull(preferred) + likely + available).distinct()
            val matches = mutableListOf<Municipality>()
            for (code in candidates) {
                val geo = load(code)
                // Si falta el poligono de cualquiera de las dos entidades limítrofes,
                // no adivinar el municipio usando solo la otra.
                if (nearBorder && geo == null) return@withContext null
                if (geo == null) continue
                findForPoint(geo, latitude, longitude)?.let { matches.add(it) }
            }
            // En un solapamiento geometrico, abstenerse y pedir ajustar el punto.
            if (matches.distinctBy { it.geoCode to it.name }.size > 1) null else matches.firstOrNull()
        }

    /** Devuelve nombres exclusivamente de poligonos oficiales descargados, no de ciudades. */
    suspend fun municipalities(stateHint: String?, knownGeoCode: String? = null): List<Municipality> = withContext(Dispatchers.IO) {
        val code = stateCode(stateHint) ?: knownGeoCode?.take(2)?.takeIf { it.matches(Regex("[0-9]{2}")) }
            ?: return@withContext emptyList()
        val geo = load(code) ?: return@withContext emptyList()
        val features = geo.optJSONArray("features") ?: return@withContext emptyList()
        (0 until features.length()).mapNotNull { i ->
            val p = features.optJSONObject(i)?.optJSONObject("properties") ?: return@mapNotNull null
            val name = p.optString("nomgeo").ifBlank { p.optString("shapeName") }.trim()
            val key = p.optString("cvegeo").ifBlank { p.optString("cve_geo") }
            if (name.isBlank()) null else Municipality(name, key)
        }.distinctBy { it.name }.sortedBy { it.name }
    }

    private fun cacheFile(code: String): File = File(context.filesDir, "rieti-inegi-mgem-$code.geojson")

    private fun load(code: String): JSONObject? {
        val file = cacheFile(code)
        val maxAge = TimeUnit.DAYS.toMillis(30)
        if (file.isFile && System.currentTimeMillis() - file.lastModified() < maxAge) {
            parseGeo(file.readText())?.let { return it }
        }
        val downloaded = runCatching {
            val request = Request.Builder()
                .url("https://gaia.inegi.org.mx/wscatgeo/v2/geo/mgem/$code")
                .header("Accept", "application/geo+json, application/json")
                .build()
            http.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use null
                // Limitar tamano para evitar consumo excesivo en el dispositivo.
                val stream = response.body?.byteStream() ?: return@use null
                val bytes = java.io.ByteArrayOutputStream()
                var tooLarge = false
                stream.use { input ->
                    val buffer = ByteArray(16 * 1024)
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        bytes.write(buffer, 0, count)
                        if (bytes.size() > 36 * 1024 * 1024) {
                            tooLarge = true
                            break
                        }
                    }
                }
                if (tooLarge) return@use null
                bytes.toString("UTF-8")
            }
        }.getOrNull()
        if (downloaded != null) {
            parseGeo(downloaded)?.let { valid ->
                runCatching {
                    val temporary = File(context.filesDir, file.name + ".tmp")
                    temporary.writeText(downloaded)
                    if (!temporary.renameTo(file)) {
                        file.writeText(downloaded)
                        temporary.delete()
                    }
                }
                return valid
            }
        }
        // Si la conexion fallo, usar una copia oficial previamente guardada.
        return if (file.isFile) runCatching { parseGeo(file.readText()) }.getOrNull() else null
    }

    private fun parseGeo(json: String): JSONObject? = runCatching {
        JSONObject(json).takeIf { it.optString("type") == "FeatureCollection" &&
            (it.optJSONArray("features")?.length() ?: 0) > 0 }
    }.getOrNull()

    private fun findForPoint(geo: JSONObject, lat: Double, lon: Double): Municipality? {
        val features = geo.optJSONArray("features") ?: return null
        var resolved: Municipality? = null
        for (index in 0 until features.length()) {
            val item = features.optJSONObject(index) ?: continue
            val geometry = item.optJSONObject("geometry") ?: continue
            if (!contains(geometry, lat, lon)) continue
            val props = item.optJSONObject("properties") ?: continue
            val name = props.optString("nomgeo").ifBlank { props.optString("shapeName") }.trim()
            val code = props.optString("cvegeo").ifBlank { props.optString("cve_geo") }
            if (name.isNotBlank()) {
                // Si dos limites reclaman el mismo punto no seleccionar arbitrariamente.
                if (resolved != null) return null
                resolved = Municipality(name, code)
            }
        }
        return resolved
    }

    private fun contains(geometry: JSONObject, lat: Double, lon: Double): Boolean {
        val coordinates = geometry.optJSONArray("coordinates") ?: return false
        return when (geometry.optString("type")) {
            "Polygon" -> polygonContains(coordinates, lat, lon)
            "MultiPolygon" -> (0 until coordinates.length()).any { index ->
                coordinates.optJSONArray(index)?.let { polygonContains(it, lat, lon) } == true
            }
            else -> false
        }
    }

    private fun polygonContains(rings: JSONArray, lat: Double, lon: Double): Boolean {
        val outer = rings.optJSONArray(0) ?: return false
        if (!ringContains(outer, lat, lon)) return false
        // El primer anillo es exterior; los siguientes son huecos.
        return (1 until rings.length()).none { index ->
            rings.optJSONArray(index)?.let { ringContains(it, lat, lon) } == true
        }
    }

    private fun ringContains(ring: JSONArray, lat: Double, lon: Double): Boolean {
        if (ring.length() < 4) return false
        var inside = false
        var previous = ring.optJSONArray(ring.length() - 1) ?: return false
        for (index in 0 until ring.length()) {
            val current = ring.optJSONArray(index) ?: return false
            val x1 = current.optDouble(0, Double.NaN)
            val y1 = current.optDouble(1, Double.NaN)
            val x2 = previous.optDouble(0, Double.NaN)
            val y2 = previous.optDouble(1, Double.NaN)
            if (!x1.isFinite() || !y1.isFinite() || !x2.isFinite() || !y2.isFinite()) return false
            // En puntos exactamente sobre la frontera, no asegurar municipio automaticamente.
            val cross = (lon - x1) * (y2 - y1) - (lat - y1) * (x2 - x1)
            if (kotlin.math.abs(cross) < 1e-11 &&
                lon >= minOf(x1, x2) && lon <= maxOf(x1, x2) &&
                lat >= minOf(y1, y2) && lat <= maxOf(y1, y2)) return false
            if ((y1 > lat) != (y2 > lat) &&
                lon < (x2 - x1) * (lat - y1) / (y2 - y1) + x1) inside = !inside
            previous = current
        }
        return inside
    }

    private fun normalize(input: String) = Normalizer.normalize(
        input.trim().lowercase(Locale.ROOT), Normalizer.Form.NFD
    ).replace(Regex("\\p{Mn}+"), "").replace(Regex("\\s+"), " ")

    companion object {
        private val states = mapOf(
            "aguascalientes" to "01", "baja california" to "02", "baja california sur" to "03",
            "campeche" to "04", "coahuila" to "05", "coahuila de zaragoza" to "05",
            "colima" to "06", "chiapas" to "07", "chihuahua" to "08", "ciudad de mexico" to "09",
            "distrito federal" to "09", "cdmx" to "09", "durango" to "10", "guanajuato" to "11",
            "guerrero" to "12", "hidalgo" to "13", "mexico" to "15", "edomex" to "15",
            "estado de mexico" to "15", "michoacan" to "16", "michoacan de ocampo" to "16",
            "morelos" to "17", "nayarit" to "18", "nuevo leon" to "19", "oaxaca" to "20",
            "puebla" to "21", "queretaro" to "22", "quintana roo" to "23",
            "san luis potosi" to "24", "sinaloa" to "25", "sonora" to "26", "tabasco" to "27",
            "tamaulipas" to "28", "tlaxcala" to "29", "veracruz" to "30",
            "veracruz de ignacio de la llave" to "30", "yucatan" to "31", "zacatecas" to "32",
            "jalisco" to "14"
        )
    }
}
