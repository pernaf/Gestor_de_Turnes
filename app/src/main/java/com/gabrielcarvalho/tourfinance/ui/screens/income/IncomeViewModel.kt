package com.gabrielcarvalho.tourfinance.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcarvalho.tourfinance.domain.model.Income
import com.gabrielcarvalho.tourfinance.domain.model.IncomeType
import com.gabrielcarvalho.tourfinance.domain.model.repository.IncomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class IncomeEditUiState(
    val income: Income? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: IncomeRepository
) : ViewModel() {

    private val _editUiState = MutableStateFlow(IncomeEditUiState())
    val editUiState: StateFlow<IncomeEditUiState> = _editUiState.asStateFlow()

    private val _savedSuccessfully = MutableStateFlow(false)
    val savedSuccessfully: StateFlow<Boolean> = _savedSuccessfully.asStateFlow()

    private var loadIncomeJob: Job? = null

    fun loadIncome(
        incomeId: Long,
        tourId: Long
    ) {
        _editUiState.update { currentState ->
            currentState.copy(
                isLoading = true,
                error = null
            )
        }

        loadIncomeJob?.cancel()
        loadIncomeJob = viewModelScope.launch {
            repository.getIncomeById(incomeId).collect { income ->
                if (income == null) {
                    _editUiState.update { currentState ->
                        currentState.copy(
                            income = null,
                            isLoading = false,
                            error = "Receita não encontrada."
                        )
                    }
                } else {
                    _editUiState.update { currentState ->
                        currentState.copy(
                            income = income,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            }
        }
    }

    fun saveIncome(
        tourId: Long,
        incomeId: Long = 0L,
        description: String,
        amount: Double,
        date: LocalDate,
        type: IncomeType,
        city: String,
        notes: String
    ) {
        viewModelScope.launch {
            _savedSuccessfully.value = false

            repository.insertIncome(
                Income(
                    id = incomeId,
                    tourId = tourId,
                    description = description,
                    amount = amount,
                    date = date,
                    type = type,
                    city = city,
                    notes = notes
                )
            )

            _savedSuccessfully.value = true
        }
    }

    fun deleteIncome(income: Income) {
        viewModelScope.launch {
            repository.deleteIncome(income)
        }
    }

    fun resetSaveState() {
        _savedSuccessfully.value = false
    }

    fun clearEditState() {
        _editUiState.value = IncomeEditUiState()
    }
}