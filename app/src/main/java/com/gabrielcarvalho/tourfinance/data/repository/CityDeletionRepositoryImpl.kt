package com.gabrielcarvalho.tourfinance.data.repository

import com.gabrielcarvalho.tourfinance.data.local.dao.CityDeletionDao
import com.gabrielcarvalho.tourfinance.domain.model.repository.CityDeletionRepository
import javax.inject.Inject

class CityDeletionRepositoryImpl @Inject constructor(
    private val dao: CityDeletionDao
) : CityDeletionRepository {

    override suspend fun deleteCityWithTransactions(
        stopId: Long,
        tourId: Long,
        cityName: String
    ) {
        dao.deleteCityWithTransactions(
            stopId = stopId,
            tourId = tourId,
            cityName = cityName
        )
    }
}

