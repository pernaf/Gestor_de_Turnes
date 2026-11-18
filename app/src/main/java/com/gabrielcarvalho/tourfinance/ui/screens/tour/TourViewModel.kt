package com.gabrielcarvalho.tourfinance.ui.screens.tour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielcarvalho.tourfinance.domain.model.Expense
import com.gabrielcarvalho.tourfinance.domain.model.Income
import com.gabrielcarvalho.tourfinance.domain.model.Tour
import com.gabrielcarvalho.tourfinance.domain.model.TourStatus
import com.gabrielcarvalho.tourfinance.domain.model.TourStop
import com.gabrielcarvalho.tourfinance.domain.model.repository.CityDeletionRepository
import com.gabrielcarvalho.tourfinance.domain.model.repository.ExpenseRepository
import com.gabrielcarvalho.tourfinance.domain.model.repository.IncomeRepository
import com.gabrielcarvalho.tourfinance.domain.model.repository.TourRepository
import com.gabrielcarvalho.tourfinance.domain.model.repository.TourStopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

private data class TourDetailData(
    val totalIncome: Double,
    val totalExpenses: Double,
    val incomes: List<Income>,
    val expenses: List<Expense>,
    val tourStops: List<TourStop>
)

@HiltViewModel
class TourViewModel @Inject constructor(
    private val tourRepository: TourRepository,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val tourStopRepository: TourStopRepository,
    private val cityDeletionRepository: CityDeletionRepository
) : ViewModel() {

    private val _listUiState = MutableStateFlow(TourListUiState())
    val listUiState: StateFlow<TourListUiState> = _listUiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(TourDetailUiState())
    val detailUiState: StateFlow<TourDetailUiState> = _detailUiState.asStateFlow()

    private var currentBandId: Long = -1L
    private var tourDetailJob: Job? = null
    private var tourHeaderJob: Job? = null
    private var toursByBandJob: Job? = null

    fun loadTourDetail(tourId: Long) {
        _detailUiState.update { currentState ->
            currentState.copy(isLoading = true)
        }

        tourHeaderJob?.cancel()
        tourHeaderJob = viewModelScope.launch {
            val tour = tourRepository.getTourById(tourId)
            _detailUiState.update { currentState ->
                currentState.copy(tour = tour)
            }
        }

        tourDetailJob?.cancel()
        tourDetailJob = viewModelScope.launch {
            combine(
                incomeRepository.getTotalIncome(tourId),
                expenseRepository.getTotalExpenses(tourId),
                incomeRepository.getIncomesByTour(tourId),
                expenseRepository.getExpensesByTour(tourId),
                tourStopRepository.getStopsByTour(tourId)
            ) { totalIncome, totalExpenses, incomes, expenses, tourStops ->
                TourDetailData(
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    incomes = incomes,
                    expenses = expenses,
                    tourStops = tourStops
                )
            }.collect { data ->
                _detailUiState.update { currentState ->
                    currentState.copy(
                        totalIncome = data.totalIncome,
                        totalExpenses = data.totalExpenses,
                        incomes = data.incomes,
                        expenses = data.expenses,
                        tourStops = data.tourStops,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun createTour(
        bandId: Long,
        name: String,
        startDate: LocalDate,
        endDate: LocalDate?,
        notes: String
    ) {
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
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }

    fun deleteExpense(expenseId: Long) {
        val expense = detailUiState.value.expenses.firstOrNull { it.id == expenseId } ?: return

        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }

    fun deleteIncome(income: Income) {
        viewModelScope.launch {
            incomeRepository.deleteIncome(income)
        }
    }

    fun deleteIncome(incomeId: Long) {
        val income = detailUiState.value.incomes.firstOrNull { it.id == incomeId } ?: return

        viewModelScope.launch {
            incomeRepository.deleteIncome(income)
        }
    }

    fun deleteTourStop(tourStop: TourStop) {
        viewModelScope.launch {
            tourStopRepository.deleteStop(tourStop)
        }
    }

    fun loadToursByBand(bandId: Long) {
        if (currentBandId == bandId && toursByBandJob?.isActive == true) return

        currentBandId = bandId
        _listUiState.update { currentState ->
            currentState.copy(isLoading = true)
        }

        toursByBandJob?.cancel()
        toursByBandJob = viewModelScope.launch {
            tourRepository.getToursByBand(bandId).collect { tours ->
                _listUiState.update { currentState ->
                    currentState.copy(
                        tours = tours,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deleteTour(tour: Tour) {
        viewModelScope.launch {
            tourRepository.deleteTour(tour)
        }
    }

    fun deleteCityWithTransactions(tourStop: TourStop) {
        viewModelScope.launch {
            cityDeletionRepository.deleteCityWithTransactions(
                stopId = tourStop.id,
                tourId = tourStop.tourId,
                cityName = tourStop.cityName
            )
        }
    }
}