package com.gabrielcarvalho.tourfinance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gabrielcarvalho.tourfinance.data.local.dao.BandDao
import com.gabrielcarvalho.tourfinance.data.local.dao.ExpenseDao
import com.gabrielcarvalho.tourfinance.data.local.dao.IncomeDao
import com.gabrielcarvalho.tourfinance.data.local.dao.TourDao
import com.gabrielcarvalho.tourfinance.data.local.dao.TourStopDao
import com.gabrielcarvalho.tourfinance.data.local.entity.BandEntity
import com.gabrielcarvalho.tourfinance.data.local.entity.ExpenseEntity
import com.gabrielcarvalho.tourfinance.data.local.entity.IncomeEntity
import com.gabrielcarvalho.tourfinance.data.local.entity.TourEntity
import com.gabrielcarvalho.tourfinance.data.local.entity.TourStopEntity

@Database(
    entities = [
        BandEntity::class,
        TourEntity::class,
        ExpenseEntity::class,
        IncomeEntity::class,
        TourStopEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bandDao(): BandDao
    abstract fun tourDao(): TourDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun tourStopDao(): TourStopDao

    companion object {
        const val DATABASE_NAME = "tourfinance_db"
    }
}