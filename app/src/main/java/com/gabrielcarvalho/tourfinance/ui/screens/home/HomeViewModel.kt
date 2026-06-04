package com.gabrielcarvalho.tourfinance.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcarvalho.tourfinance.domain.model.Tour
import com.gabrielcarvalho.tourfinance.domain.model.repository.TourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val activeTours: List<Tour> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tourRepository: TourRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        tourRepository.getActiveTours()
            .map { tours ->
                HomeUiState(
                    activeTours = tours,
                    isLoading = false
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState()
            )
}