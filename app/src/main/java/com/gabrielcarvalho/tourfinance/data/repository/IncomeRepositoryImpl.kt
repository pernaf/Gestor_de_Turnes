package com.gabrielcarvalho.tourfinance.data.repository

import com.gabrielcarvalho.tourfinance.data.local.dao.IncomeDao
import com.gabrielcarvalho.tourfinance.data.local.entity.IncomeEntity
import com.gabrielcarvalho.tourfinance.domain.model.Income
import com.gabrielcarvalho.tourfinance.domain.model.IncomeType
import com.gabrielcarvalho.tourfinance.domain.model.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class IncomeRepositoryImpl @Inject constructor(
    private val dao: IncomeDao
) : IncomeRepository {

    override fun getIncomesByTour(tourId: Long): Flow<List<Income>> =
        dao.getIncomesByTour(tourId).map { it.map { e -> e.toDomain() } }

    override fun getTotalIncome(tourId: Long): Flow<Double> =
        dao.getTotalIncome(tourId)

    override suspend fun insertIncome(income: Income): Long =
        dao.insert(income.toEntity())

    override suspend fun deleteIncome(income: Income) =
        dao.delete(income.toEntity())

    private fun IncomeEntity.toDomain() = Income(
        id = id,
        tourId = tourId,
        description = description,
        amount = amount,
        date = LocalDate.parse(date),
        type = IncomeType.valueOf(type),
        city = city,
        notes = notes
    )

    private fun Income.toEntity() = IncomeEntity(
        id = id,
        tourId = tourId,
        description = description,
        amount = amount,
        date = date.toString(),
        type = type.name,
        city = city,
        notes = notes
    )
}