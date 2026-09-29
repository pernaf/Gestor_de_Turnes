package com.gabrielcarvalho.tourfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface CityDeletionDao {

    @Query("DELETE FROM incomes WHERE tourId = :tourId AND LOWER(city) = LOWER(:cityName)")
    suspend fun deleteIncomesByTourAndCity(tourId: Long, cityName: String)

    @Query("DELETE FROM expenses WHERE tourId = :tourId AND LOWER(city) = LOWER(:cityName)")
    suspend fun deleteExpensesByTourAndCity(tourId: Long, cityName: String)

    @Query("DELETE FROM tour_stops WHERE id = :stopId")
    suspend fun deleteTourStopById(stopId: Long)

    @Transaction
    suspend fun deleteCityWithTransactions(
        stopId: Long,
        tourId: Long,
        cityName: String
    ) {
        deleteIncomesByTourAndCity(tourId, cityName)
        deleteExpensesByTourAndCity(tourId, cityName)
        deleteTourStopById(stopId)
    }
}

