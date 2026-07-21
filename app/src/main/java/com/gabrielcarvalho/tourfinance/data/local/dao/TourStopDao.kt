package com.gabrielcarvalho.tourfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gabrielcarvalho.tourfinance.data.local.entity.TourStopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TourStopDao {

    @Query("SELECT * FROM tour_stops WHERE tourId = :tourId ORDER BY showDate ASC")
    fun getStopsByTour(tourId: Long): Flow<List<TourStopEntity>>

    @Insert
    suspend fun insert(stop: TourStopEntity)

    @Update
    suspend fun update(stop: TourStopEntity)

    @Delete
    suspend fun delete(stop: TourStopEntity)
}