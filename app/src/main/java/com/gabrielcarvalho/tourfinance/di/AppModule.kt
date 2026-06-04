package com.gabrielcarvalho.tourfinance.di

import android.content.Context
import androidx.room.Room
import com.gabrielcarvalho.tourfinance.data.local.AppDatabase
import com.gabrielcarvalho.tourfinance.data.local.dao.BandDao
import com.gabrielcarvalho.tourfinance.data.local.dao.ExpenseDao
import com.gabrielcarvalho.tourfinance.data.local.dao.IncomeDao
import com.gabrielcarvalho.tourfinance.data.local.dao.TourDao
import com.gabrielcarvalho.tourfinance.data.repository.BandRepositoryImpl
import com.gabrielcarvalho.tourfinance.data.repository.ExpenseRepositoryImpl
import com.gabrielcarvalho.tourfinance.data.repository.IncomeRepositoryImpl
import com.gabrielcarvalho.tourfinance.data.repository.TourRepositoryImpl
import com.gabrielcarvalho.tourfinance.domain.model.repository.BandRepository
import com.gabrielcarvalho.tourfinance.domain.model.repository.ExpenseRepository
import com.gabrielcarvalho.tourfinance.domain.model.repository.IncomeRepository
import com.gabrielcarvalho.tourfinance.domain.model.repository.TourRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTourDao(db: AppDatabase): TourDao = db.tourDao()

    @Provides
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideIncomeDao(db: AppDatabase): IncomeDao = db.incomeDao()

    @Provides
    @Singleton
    fun provideTourRepository(impl: TourRepositoryImpl): TourRepository = impl

    @Provides
    @Singleton
    fun provideExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository = impl

    @Provides
    @Singleton
    fun provideIncomeRepository(impl: IncomeRepositoryImpl): IncomeRepository = impl

    @Provides
    fun provideBandDao(db: AppDatabase): BandDao = db.bandDao()

    @Provides
    @Singleton
    fun provideBandRepository(impl: BandRepositoryImpl): BandRepository = impl

}