package com.gabrielcarvalho.tourfinance.domain.model

import java.time.LocalDate

data class Tour(
    val id: Long = 0,
    val bandId: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val currency: String = "BRL",
    val status: TourStatus = TourStatus.ACTIVE,
    val notes: String
)

enum class TourStatus { ACTIVE, CLOSED }