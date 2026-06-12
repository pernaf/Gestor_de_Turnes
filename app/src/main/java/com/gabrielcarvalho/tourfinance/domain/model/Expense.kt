package com.gabrielcarvalho.tourfinance.domain.model

import java.time.LocalDate

data class Expense(
    val id: Long = 0,
    val tourId: Long,
    val description: String,
    val amount: Double,
    val category: ExpenseCategory,
    val date: LocalDate,
    val notes: String = "",
    val city: String = ""
)

enum class ExpenseCategory(val label: String, val emoji: String) {
    TRANSPORT("Transporte", "🚐"),
    ACCOMMODATION("Hospedagem", "🏨"),
    FOOD("Alimentação", "🍕"),
    EQUIPMENT("Equipamento", "🎸"),
    MARKETING("Marketing", "📣"),
    FEES("Taxas", "📄"),
    OTHER("Outros", "📦")
}