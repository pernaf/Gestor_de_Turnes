package com.gabrielcarvalho.tourfinance.ui.screens.tourstop

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcarvalho.tourfinance.data.local.staticdata.BrazilCitiesAssetDataSource
import com.gabrielcarvalho.tourfinance.domain.model.TourStop
import com.gabrielcarvalho.tourfinance.domain.model.repository.TourStopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

data class SearchableCity(
    val original: String,
    val normalized: String
)

data class TourStopUiState(
    val cityName: String = "",
    val showDate: LocalDate = LocalDate.now(),
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val errorMessage: String? = null,
    val availableCities: List<String> = emptyList(),
    val filteredCities: List<String> = emptyList(),
    val showSuggestions: Boolean = false
)

@HiltViewModel
class TourStopViewModel @Inject constructor(
    private val application: Application,
    private val repository: TourStopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TourStopUiState())
    val uiState = _uiState.asStateFlow()

    private var searchableCities: List<SearchableCity> = emptyList()

    init {
        loadBrazilCities()
    }

    private fun loadBrazilCities() {
        runCatching {
            BrazilCitiesAssetDataSource.loadCities(application)
        }.onSuccess { cities ->
            searchableCities = cities.map { city ->
                SearchableCity(
                    original = city,
                    normalized = normalizeCityText(city)
                )
            }

            _uiState.update { current ->
                current.copy(availableCities = cities)
            }
        }.onFailure {
            _uiState.update {
                it.copy(errorMessage = "Não foi possível carregar a lista de cidades")
            }
        }
    }

    fun onCityNameChange(value: String) {
        val query = value.trim()
        val normalizedQuery = normalizeCityText(query)

        val suggestions = if (normalizedQuery.isBlank()) {
            emptyList()
        } else {
            val startsWithMatches = searchableCities
                .asSequence()
                .filter { it.normalized.startsWith(normalizedQuery) }
                .map { it.original }
                .toList()

            val containsMatches = searchableCities
                .asSequence()
                .filter {
                    !it.normalized.startsWith(normalizedQuery) &&
                            it.normalized.contains(normalizedQuery)
                }
                .map { it.original }
                .toList()

            (startsWithMatches + containsMatches)
                .distinct()
                .take(20)
        }

        _uiState.update {
            it.copy(
                cityName = value,
                filteredCities = suggestions,
                showSuggestions = suggestions.isNotEmpty()
            )
        }
    }

    fun onCitySelected(city: String) {
        _uiState.update {
            it.copy(
                cityName = city,
                filteredCities = emptyList(),
                showSuggestions = false
            )
        }
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

    fun dismissSuggestions() {
        _uiState.update { it.copy(showSuggestions = false) }
    }

    fun saveTourStop(tourId: Long) {
        val state = _uiState.value

        if (state.cityName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Selecione uma cidade") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            runCatching {
                repository.insertStop(
                    TourStop(
                        id = 0L,
                        tourId = tourId,
                        cityName = state.cityName.trim(),
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

    private fun normalizeCityText(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase(Locale.getDefault())
            .trim()
    }
}