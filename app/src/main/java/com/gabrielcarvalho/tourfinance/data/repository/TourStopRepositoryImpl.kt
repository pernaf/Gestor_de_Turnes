package com.gabrielcarvalho.tourfinance.data.repository

import com.gabrielcarvalho.tourfinance.data.local.dao.TourStopDao
import com.gabrielcarvalho.tourfinance.data.local.entity.TourStopEntity
import com.gabrielcarvalho.tourfinance.domain.model.TourStop
import com.gabrielcarvalho.tourfinance.domain.model.repository.TourStopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TourStopRepositoryImpl @Inject constructor(
    private val dao: TourStopDao
) : TourStopRepository {

    override fun getStopsByTour(tourId: Long): Flow<List<TourStop>> {
        return dao.getStopsByTour(tourId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertStop(stop: TourStop) {
        dao.insert(stop.toEntity())
    }

    override suspend fun updateStop(stop: TourStop) {
        dao.update(stop.toEntity())
    }

    override suspend fun deleteStop(stop: TourStop) {
        dao.delete(stop.toEntity())
    }

    private fun TourStopEntity.toDomain(): TourStop {
        return TourStop(
            id = id,
            tourId = tourId,
            cityName = cityName,
            showDate = LocalDate.parse(showDate)
        )
    }

    private fun TourStop.toEntity(): TourStopEntity {
        return TourStopEntity(
            id = id,
            tourId = tourId,
            cityName = cityName,
            showDate = showDate.toString()
        )
    }
}