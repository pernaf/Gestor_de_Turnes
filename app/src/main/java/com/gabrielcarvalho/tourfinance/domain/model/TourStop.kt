package com.gabrielcarvalho.tourfinance.domain.model

import java.time.LocalDate

data class TourStop(
    val id: Long = 0,
    val tourId: Long,
    val cityName: String,
    val showDate: LocalDate
)