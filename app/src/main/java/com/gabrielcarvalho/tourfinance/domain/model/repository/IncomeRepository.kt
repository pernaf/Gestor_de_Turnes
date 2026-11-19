package com.gabrielcarvalho.tourfinance.domain.model.repository

import com.gabrielcarvalho.tourfinance.domain.model.Income
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {
    fun getIncomesByTour(tourId: Long): Flow<List<Income>>
    fun getTotalIncome(tourId: Long): Flow<Double>
    fun getIncomeById(incomeId: Long): Flow<Income?>
    suspend fun insertIncome(income: Income): Long
    suspend fun deleteIncome(income: Income)
    suspend fun deleteByTourAndCity(tourId: Long, cityName: String)
}