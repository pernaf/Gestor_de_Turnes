package com.gabrielcarvalho.tourfinance.ui.screens.tourstop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcarvalho.tourfinance.domain.model.TourStop
import com.gabrielcarvalho.tourfinance.domain.model.repository.TourStopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TourStopUiState(
    val cityName: String = "",
    val showDate: LocalDate = LocalDate.now(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val savedSuccessfully: Boolean = false
)

@HiltViewModel
class TourStopViewModel @Inject constructor(
    private val repository: TourStopRepository
) : ViewModel() {

    var uiState = TourStopUiState()
        private set

    fun onCityNameChange(value: String) {
        uiState = uiState.copy(cityName = value)
    }

    fun onShowDateChange(date: LocalDate) {
        uiState = uiState.copy(showDate = date)
    }

    fun saveTourStop(tourId: Long) {
        val city = uiState.cityName.trim()

        if (city.isBlank()) {
            uiState = uiState.copy(errorMessage = "Informe a cidade do show.")
            return
        }

        uiState = uiState.copy(isSaving = true, errorMessage = null)

        viewModelScope.launch {
            try {
                repository.insertStop(
                    TourStop(
                        tourId = tourId,
                        cityName = city,
                        showDate = uiState.showDate
                    )
                )
                uiState = uiState.copy(
                    isSaving = false,
                    savedSuccessfully = true
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Erro ao salvar cidade da turnê."
                )
            }
        }
    }

    fun clearSavedState() {
        uiState = uiState.copy(savedSuccessfully = false)
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}