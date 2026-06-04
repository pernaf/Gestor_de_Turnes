package com.gabrielcarvalho.tourfinance.data.repository

import com.gabrielcarvalho.tourfinance.data.local.dao.BandDao
import com.gabrielcarvalho.tourfinance.data.local.entity.BandEntity
import com.gabrielcarvalho.tourfinance.domain.model.Band
import com.gabrielcarvalho.tourfinance.domain.model.repository.BandRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BandRepositoryImpl @Inject constructor(
    private val dao: BandDao
) : BandRepository {

    override fun getAllBands(): Flow<List<Band>> =
        dao.getAllBands().map { list -> list.map { it.toDomain() } }

    override suspend fun insertBand(band: Band): Long =
        dao.insert(band.toEntity())

    override suspend fun updateBand(band: Band) =
        dao.update(band.toEntity())

    override suspend fun deleteBand(band: Band) =
        dao.delete(band.toEntity())

    private fun BandEntity.toDomain() = Band(id = id, name = name, description = description)
    private fun Band.toEntity() = BandEntity(id = id, name = name, description = description)
}