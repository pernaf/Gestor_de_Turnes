package com.gabrielcarvalho.tourfinance.domain.model.repository

interface CityDeletionRepository {
    suspend fun deleteCityWithTransactions(
        stopId: Long,
        tourId: Long,
        cityName: String
    )
}

