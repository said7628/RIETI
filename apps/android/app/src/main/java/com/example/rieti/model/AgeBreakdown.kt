package com.example.rieti.model

/** Los borradores antiguos (un rango) siguen siendo legibles. */
object AgeBreakdown {
    val ranges = listOf("0-5", "6-11", "12-14", "15-17")

    fun read(value: String, total: Int = 0): Map<String, Int> {
        if (value in ranges) return mapOf(value to total.coerceAtLeast(1))
        return value.split(';').mapNotNull { item ->
            val pair = item.split(':', limit = 2)
            val count = pair.getOrNull(1)?.toIntOrNull()
            pair.firstOrNull()?.takeIf { it in ranges && count != null && count > 0 }?.let { it to count!! }
        }.toMap()
    }

    fun encode(counts: Map<String, Int>): String = ranges.mapNotNull { range ->
        counts[range]?.takeIf { it > 0 }?.let { "$range:$it" }
    }.joinToString(";")

    fun display(value: String, total: Int = 0): String = read(value, total)
        .entries.joinToString(", ") { "${it.key} años: ${it.value}" }

    fun valid(value: String, total: Int): Boolean =
        total > 0 && read(value, total).values.sum() == total
}
