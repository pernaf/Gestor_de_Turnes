package com.gabrielcarvalho.tourfinance.ui.screens.tourstop

import java.text.Normalizer

fun normalizeCityText(text: String): String {
    return Normalizer
        .normalize(text.trim(), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .lowercase()
}

fun filterBrazilCities(
    allCities: List<String>,
    query: String,
    limit: Int = 40
): List<String> {
    val normalizedQuery = normalizeCityText(query)

    if (normalizedQuery.isBlank()) {
        return allCities.take(limit)
    }

    val startsWithMatches = allCities.filter {
        normalizeCityText(it).startsWith(normalizedQuery)
    }

    val containsMatches = allCities.filter {
        val normalizedCity = normalizeCityText(it)
        !normalizedCity.startsWith(normalizedQuery) && normalizedCity.contains(normalizedQuery)
    }

    return (startsWithMatches + containsMatches).distinct().take(limit)
}

