package com.gabrielcarvalho.tourfinance.domain.model.repository

import com.gabrielcarvalho.tourfinance.domain.model.Band
import kotlinx.coroutines.flow.Flow

interface BandRepository {
    fun getAllBands(): Flow<List<Band>>
    suspend fun insertBand(band: Band): Long
    suspend fun updateBand(band: Band)
    suspend fun deleteBand(band: Band)
}