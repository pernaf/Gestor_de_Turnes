package com.gabrielcarvalho.tourfinance.data.repository

import com.gabrielcarvalho.tourfinance.data.local.dao.ExpenseDao
import com.gabrielcarvalho.tourfinance.data.local.entity.ExpenseEntity
import com.gabrielcarvalho.tourfinance.domain.model.Expense
import com.gabrielcarvalho.tourfinance.domain.model.ExpenseCategory
import com.gabrielcarvalho.tourfinance.domain.model.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao
) : ExpenseRepository {

    override fun getExpensesByTour(tourId: Long): Flow<List<Expense>> =
        dao.getExpensesByTour(tourId).map { it.map { e -> e.toDomain() } }

    override fun getTotalExpenses(tourId: Long): Flow<Double> =
        dao.getTotalExpenses(tourId)

    override suspend fun insertExpense(expense: Expense): Long =
        dao.insert(expense.toEntity())

    override suspend fun deleteExpense(expense: Expense) =
        dao.delete(expense.toEntity())

    private fun ExpenseEntity.toDomain() = Expense(
        id = id,
        tourId = tourId,
        description = description,
        amount = amount,
        category = ExpenseCategory.valueOf(category),
        date = LocalDate.parse(date),
        notes = notes
    )

    private fun Expense.toEntity() = ExpenseEntity(
        id = id,
        tourId = tourId,
        description = description,
        amount = amount,
        category = category.name,
        date = date.toString(),
        notes = notes
    )
}