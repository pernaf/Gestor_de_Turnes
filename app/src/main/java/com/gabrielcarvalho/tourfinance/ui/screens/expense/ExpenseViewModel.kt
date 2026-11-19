package com.gabrielcarvalho.tourfinance.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcarvalho.tourfinance.domain.model.Expense
import com.gabrielcarvalho.tourfinance.domain.model.ExpenseCategory
import com.gabrielcarvalho.tourfinance.domain.model.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ExpenseEditUiState(
    val expense: Expense? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _editUiState = MutableStateFlow(ExpenseEditUiState())
    val editUiState: StateFlow<ExpenseEditUiState> = _editUiState.asStateFlow()

    private val _savedSuccessfully = MutableStateFlow(false)
    val savedSuccessfully: StateFlow<Boolean> = _savedSuccessfully.asStateFlow()

    private var loadExpenseJob: Job? = null

    fun loadExpense(
        expenseId: Long,
        tourId: Long
    ) {
        _editUiState.update { currentState ->
            currentState.copy(
                isLoading = true,
                error = null
            )
        }

        loadExpenseJob?.cancel()
        loadExpenseJob = viewModelScope.launch {
            repository.getExpenseById(expenseId).collect { expense ->
                if (expense == null) {
                    _editUiState.update { currentState ->
                        currentState.copy(
                            expense = null,
                            isLoading = false,
                            error = "Despesa não encontrada."
                        )
                    }
                } else {
                    _editUiState.update { currentState ->
                        currentState.copy(
                            expense = expense,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            }
        }
    }

    fun saveExpense(
        tourId: Long,
        expenseId: Long = 0L,
        description: String,
        amount: Double,
        date: LocalDate,
        category: ExpenseCategory,
        notes: String,
        city: String
    ) {
        viewModelScope.launch {
            _savedSuccessfully.value = false

            repository.insertExpense(
                Expense(
                    id = expenseId,
                    tourId = tourId,
                    description = description,
                    amount = amount,
                    category = category,
                    date = date,
                    notes = notes,
                    city = city
                )
            )

            _savedSuccessfully.value = true
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun resetSaveState() {
        _savedSuccessfully.value = false
    }

    fun clearEditState() {
        _editUiState.value = ExpenseEditUiState()
    }
}