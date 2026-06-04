package com.gabrielcarvalho.tourfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gabrielcarvalho.tourfinance.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Query("SELECT * FROM incomes WHERE tourId = :tourId ORDER BY date DESC")
    fun getIncomesByTour(tourId: Long): Flow<List<IncomeEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM incomes WHERE tourId = :tourId")
    fun getTotalIncome(tourId: Long): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(income: IncomeEntity): Long

    @Delete
    suspend fun delete(income: IncomeEntity)
}