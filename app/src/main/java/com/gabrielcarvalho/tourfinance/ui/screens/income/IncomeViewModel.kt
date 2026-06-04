package com.gabrielcarvalho.tourfinance.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcarvalho.tourfinance.domain.model.Income
import com.gabrielcarvalho.tourfinance.domain.model.IncomeType
import com.gabrielcarvalho.tourfinance.domain.model.repository.IncomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: IncomeRepository
) : ViewModel() {

    private val _incomeToEdit = MutableStateFlow<Income?>(null)
    val incomeToEdit: StateFlow<Income?> = _incomeToEdit.asStateFlow()

    fun loadIncome(incomeId: Long, tourId: Long) {
        viewModelScope.launch {
            repository.getIncomesByTour(tourId).collect { list ->
                _incomeToEdit.value = list.find { it.id == incomeId }
            }
        }
    }

    fun saveIncome(
        tourId: Long,
        incomeId: Long = 0L,
        description: String,
        amount: Double,
        type: IncomeType,
        city: String
    ) {
        viewModelScope.launch {
            repository.insertIncome(
                Income(
                    id = incomeId,
                    tourId = tourId,
                    description = description,
                    amount = amount,
                    date = LocalDate.now(),
                    type = type,
                    city = city
                )
            )
        }
    }

    fun deleteIncome(income: Income) {
        viewModelScope.launch {
            repository.deleteIncome(income)
        }
    }
}