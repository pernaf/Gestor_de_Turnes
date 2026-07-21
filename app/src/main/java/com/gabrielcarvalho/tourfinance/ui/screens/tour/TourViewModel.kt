package com.gabrielcarvalho.tourfinance.ui.screens.tour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcarvalho.tourfinance.domain.model.Expense
import com.gabrielcarvalho.tourfinance.domain.model.Income
import com.gabrielcarvalho.tourfinance.domain.model.Tour
import com.gabrielcarvalho.tourfinance.domain.model.TourStatus
import com.gabrielcarvalho.tourfinance.domain.model.TourStop
import com.gabrielcarvalho.tourfinance.domain.model.repository.ExpenseRepository
import com.gabrielcarvalho.tourfinance.domain.model.repository.IncomeRepository
import com.gabrielcarvalho.tourfinance.domain.model.repository.TourRepository
import com.gabrielcarvalho.tourfinance.domain.model.repository.TourStopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TourListUiState(
    val tours: List<Tour> = emptyList(),
    val isLoading: Boolean = true
)

data class TourDetailUiState(
    val tour: Tour? = null,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val expenses: List<Expense> = emptyList(),
    val incomes: List<Income> = emptyList(),
    val isLoading: Boolean = true,
    val tourStops: List<TourStop> = emptyList()
) {
    val balance: Double get() = totalIncome - totalExpenses
}

@HiltViewModel
class TourViewModel @Inject constructor(
    private val tourRepository: TourRepository,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val tourStopRepository: TourStopRepository
) : ViewModel() {

    private val _listUiState = MutableStateFlow(TourListUiState())
    val listUiState: StateFlow<TourListUiState> = _listUiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(TourDetailUiState())
    val detailUiState: StateFlow<TourDetailUiState> = _detailUiState.asStateFlow()

    fun loadTourDetail(tourId: Long) {
        _detailUiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val tour = tourRepository.getTourById(tourId)
            _detailUiState.update { it.copy(tour = tour) }
        }

        viewModelScope.launch {
            combine(
                incomeRepository.getTotalIncome(tourId),
                expenseRepository.getTotalExpenses(tourId),
                incomeRepository.getIncomesByTour(tourId),
                expenseRepository.getExpensesByTour(tourId),
                tourStopRepository.getStopsByTour(tourId)
            ) { totalIncome, totalExpenses, incomes, expenses, tourStops ->
                _detailUiState.update {
                    it.copy(
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses,
                        incomes = incomes,
                        expenses = expenses,
                        tourStops = tourStops,
                        isLoading = false
                    )
                }
            }.collect {}
        }
    }

    fun createTour(bandId: Long, name: String, startDate: LocalDate, endDate: LocalDate?, notes: String) {
        viewModelScope.launch {
            tourRepository.insertTour(
                Tour(
                    bandId = bandId,
                    name = name,
                    startDate = startDate,
                    endDate = endDate,
                    notes = notes
                )
            )
        }
    }

    fun closeTour(tour: Tour) {
        viewModelScope.launch {
            tourRepository.updateTour(
                tour.copy(
                    status = TourStatus.CLOSED,
                    endDate = LocalDate.now()
                )
            )
        }
    }
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { expenseRepository.deleteExpense(expense) }
    }

    fun deleteIncome(income: Income) {
        viewModelScope.launch { incomeRepository.deleteIncome(income) }
    }

    private var currentBandId: Long = -1L

    fun loadToursByBand(bandId: Long) {
        if (currentBandId == bandId) return
        currentBandId = bandId
        viewModelScope.launch {
            tourRepository.getToursByBand(bandId).collect { tours ->
                _listUiState.update { it.copy(tours = tours, isLoading = false) }
            }
        }
    }
    fun deleteTour(tour: Tour) {
        viewModelScope.launch {
            tourRepository.deleteTour(tour)
        }
    }
}