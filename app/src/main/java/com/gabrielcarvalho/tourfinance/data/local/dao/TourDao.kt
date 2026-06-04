package com.gabrielcarvalho.tourfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gabrielcarvalho.tourfinance.data.local.entity.TourEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TourDao {

    @Query("SELECT * FROM tours ORDER BY startDate DESC")
    fun getAllTours(): Flow<List<TourEntity>>

    @Query("SELECT * FROM tours WHERE status = 'ACTIVE' ORDER BY startDate DESC")
    fun getActiveTours(): Flow<List<TourEntity>>

    @Query("SELECT * FROM tours WHERE id = :id")
    suspend fun getTourById(id: Long): TourEntity?

    @Query("SELECT * FROM tours WHERE bandId = :bandId ORDER BY startDate DESC")
    fun getToursByBand(bandId: Long): Flow<List<TourEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tour: TourEntity): Long

    @Update
    suspend fun update(tour: TourEntity)

    @Delete
    suspend fun delete(tour: TourEntity)
}