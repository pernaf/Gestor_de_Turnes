package com.gabrielcarvalho.tourfinance.domain.model.repository

import com.gabrielcarvalho.tourfinance.domain.model.TourStop
import kotlinx.coroutines.flow.Flow

interface TourStopRepository {
    fun getStopsByTour(tourId: Long): Flow<List<TourStop>>
    suspend fun insertStop(stop: TourStop)
    suspend fun updateStop(stop: TourStop)
    suspend fun deleteStop(stop: TourStop)
}