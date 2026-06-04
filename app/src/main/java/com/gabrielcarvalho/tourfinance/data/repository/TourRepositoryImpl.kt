package com.gabrielcarvalho.tourfinance.data.repository

import com.gabrielcarvalho.tourfinance.data.local.dao.TourDao
import com.gabrielcarvalho.tourfinance.data.local.entity.TourEntity
import com.gabrielcarvalho.tourfinance.domain.model.Tour
import com.gabrielcarvalho.tourfinance.domain.model.TourStatus
import com.gabrielcarvalho.tourfinance.domain.model.repository.TourRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TourRepositoryImpl @Inject constructor(
    private val dao: TourDao
) : TourRepository {

    override fun getToursByBand(bandId: Long): Flow<List<Tour>> =
        dao.getToursByBand(bandId).map { list -> list.map { it.toDomain() } }

    override fun getAllTours(): Flow<List<Tour>> =
        dao.getAllTours().map { it.map { e -> e.toDomain() } }

    override fun getActiveTours(): Flow<List<Tour>> =
        dao.getActiveTours().map { it.map { e -> e.toDomain() } }

    override suspend fun getTourById(id: Long): Tour? =
        dao.getTourById(id)?.toDomain()

    override suspend fun insertTour(tour: Tour): Long =
        dao.insert(tour.toEntity())

    override suspend fun updateTour(tour: Tour) =
        dao.update(tour.toEntity())

    override suspend fun deleteTour(tour: Tour) =
        dao.delete(tour.toEntity())

    private fun TourEntity.toDomain() = Tour(
        id = id,
        bandId = bandId,
        name = name,
        startDate = LocalDate.parse(startDate),
        endDate = endDate?.let { LocalDate.parse(it) },
        status = TourStatus.valueOf(status),
        notes = notes
    )

    private fun Tour.toEntity() = TourEntity(
        id = id,
        bandId = bandId,
        name = name,
        startDate = startDate.toString(),
        endDate = endDate?.toString(),
        status = status.name,
        notes = notes
    )
}