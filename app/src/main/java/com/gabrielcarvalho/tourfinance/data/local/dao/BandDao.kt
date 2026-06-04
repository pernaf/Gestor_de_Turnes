package com.gabrielcarvalho.tourfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gabrielcarvalho.tourfinance.data.local.entity.BandEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BandDao {

    @Query("SELECT * FROM bands ORDER BY name ASC")
    fun getAllBands(): Flow<List<BandEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(band: BandEntity): Long

    @Update
    suspend fun update(band: BandEntity)

    @Delete
    suspend fun delete(band: BandEntity)
}