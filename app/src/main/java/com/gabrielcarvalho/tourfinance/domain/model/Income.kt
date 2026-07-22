package com.gabrielcarvalho.tourfinance.domain.model

import java.time.LocalDate

data class Income(
    val id: Long = 0,
    val tourId: Long,
    val description: String,
    val amount: Double,
    val date: LocalDate,
    val type: IncomeType,
    val city: String = "",
    val notes: String = ""
)

enum class IncomeType(val label: String, val emoji: String) {
    SHOW("Cachê de show", "🎤"),
    MERCH("Merch", "🛍️"),
    STREAMING("Streaming", "🎧"),
    SPONSORSHIP("Patrocínio", "🤝")
}