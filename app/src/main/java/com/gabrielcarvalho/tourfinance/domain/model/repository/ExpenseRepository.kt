package com.gabrielcarvalho.tourfinance.domain.model.repository

import com.gabrielcarvalho.tourfinance.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpensesByTour(tourId: Long): Flow<List<Expense>>
    fun getTotalExpenses(tourId: Long): Flow<Double>
    suspend fun insertExpense(expense: Expense): Long
    suspend fun deleteExpense(expense: Expense)
}