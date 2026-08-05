package com.gabrielcarvalho.tourfinance.ui.screens.tourstop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcarvalho.tourfinance.domain.model.TourStop
import com.gabrielcarvalho.tourfinance.domain.model.repository.TourStopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TourStopUiState(
    val state: String = "",
    val cityName: String = "",
    val showDate: LocalDate = LocalDate.now(),
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val errorMessage: String? = null,
    val availableStates: List<String> = brazilStates
)

private val brazilStates = listOf(
    "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
    "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
    "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
)

@HiltViewModel
class TourStopViewModel @Inject constructor(
    private val repository: TourStopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TourStopUiState())
    val uiState = _uiState.asStateFlow()

    fun onStateChange(value: String) {
        _uiState.update { it.copy(state = value) }
    }

    fun onCityNameChange(value: String) {
        _uiState.update { it.copy(cityName = value) }
    }

    fun onShowDateChange(date: LocalDate) {
        _uiState.update { it.copy(showDate = date) }
    }

    fun clearSavedState() {
        _uiState.update { it.copy(savedSuccessfully = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun saveTourStop(tourId: Long) {
        val state = _uiState.value

        if (state.state.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Selecione o estado") }
            return
        }

        if (state.cityName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Digite a cidade") }
            return
        }

        val formattedCityName = "${state.cityName.trim()} - ${state.state}"

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            runCatching {
                repository.insertStop(
                    TourStop(
                        id = 0L,
                        tourId = tourId,
                        cityName = formattedCityName,
                        showDate = state.showDate
                    )
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        savedSuccessfully = true
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Não foi possível salvar a cidade"
                    )
                }
            }
        }
    }
}