package com.gabrielcarvalho.tourfinance.domain.model.repository

import com.gabrielcarvalho.tourfinance.domain.model.Tour
import kotlinx.coroutines.flow.Flow

interface TourRepository {
    fun getToursByBand(bandId: Long): Flow<List<Tour>>
    fun getAllTours(): Flow<List<Tour>>
    fun getActiveTours(): Flow<List<Tour>>
    suspend fun getTourById(id: Long): Tour?
    suspend fun insertTour(tour: Tour): Long
    suspend fun updateTour(tour: Tour)
    suspend fun deleteTour(tour: Tour)
}