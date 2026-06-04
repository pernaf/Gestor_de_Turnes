package com.gabrielcarvalho.tourfinance.ui.screens.band

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcarvalho.tourfinance.domain.model.Band
import com.gabrielcarvalho.tourfinance.domain.model.repository.BandRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BandUiState(
    val bands: List<Band> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class BandViewModel @Inject constructor(
    private val repository: BandRepository
) : ViewModel() {

    val uiState: StateFlow<BandUiState> =
        repository.getAllBands().map { bands ->
            BandUiState(bands = bands, isLoading = false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BandUiState()
        )

    fun addBand(name: String, description: String) {
        viewModelScope.launch {
            repository.insertBand(Band(name = name, description = description))
        }
    }

    fun deleteBand(band: Band) {
        viewModelScope.launch {
            repository.deleteBand(band)
        }
    }
}