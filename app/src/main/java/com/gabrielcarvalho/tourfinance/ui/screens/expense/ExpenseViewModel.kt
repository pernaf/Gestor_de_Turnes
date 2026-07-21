package com.gabrielcarvalho.tourfinance.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcarvalho.tourfinance.domain.model.Expense
import com.gabrielcarvalho.tourfinance.domain.model.ExpenseCategory
import com.gabrielcarvalho.tourfinance.domain.model.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _expenseToEdit = MutableStateFlow<Expense?>(null)
    val expenseToEdit: StateFlow<Expense?> = _expenseToEdit.asStateFlow()

    private val _savedSuccessfully = MutableStateFlow(false)
    val savedSuccessfully: StateFlow<Boolean> = _savedSuccessfully.asStateFlow()

    fun loadExpense(expenseId: Long, tourId: Long) {
        viewModelScope.launch {
            repository.getExpensesByTour(tourId).collect { list ->
                _expenseToEdit.value = list.find { it.id == expenseId }
            }
        }
    }

    fun saveExpense(
        tourId: Long,
        expenseId: Long = 0L,
        description: String,
        amount: Double,
        category: ExpenseCategory,
        notes: String,
        city: String
    ) {
        viewModelScope.launch {
            _savedSuccessfully.value = false

            val originalExpense = if (expenseId != 0L) _expenseToEdit.value else null
            val dateToUse = originalExpense?.date ?: LocalDate.now()

            repository.insertExpense(
                Expense(
                    id = expenseId,
                    tourId = tourId,
                    description = description,
                    amount = amount,
                    category = category,
                    date = dateToUse,
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
}