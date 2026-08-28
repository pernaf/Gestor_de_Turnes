package com.gabrielcarvalho.tourfinance.ui.screens.city

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielcarvalho.tourfinance.domain.model.Expense
import com.gabrielcarvalho.tourfinance.domain.model.ExpenseCategory
import com.gabrielcarvalho.tourfinance.ui.components.TransactionItem
import com.gabrielcarvalho.tourfinance.ui.screens.tour.TourViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val expenseCategoryDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCategoryDetailScreen(
    tourId: Long,
    cityName: String,
    categoryName: String,
    onEditExpense: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TourViewModel = hiltViewModel()
) {
    LaunchedEffect(tourId) {
        viewModel.loadTourDetail(tourId)
    }

    val uiState by viewModel.detailUiState.collectAsStateWithLifecycle()

    var expenseToDelete by remember {
        mutableStateOf<Expense?>(null)
    }

    val category = remember(categoryName) {
        ExpenseCategory.entries.firstOrNull { it.name == categoryName }
    }

    val expenses = remember(uiState.expenses, cityName, categoryName) {
        uiState.expenses
            .filter { it.city.equals(cityName, ignoreCase = true) }
            .filter { it.category.name == categoryName }
            .sortedByDescending { it.date }
    }

    val total = remember(expenses) {
        expenses.sumOf { it.amount }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = category?.let { "${it.emoji} ${it.label}" } ?: categoryName
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = cityName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
                        )

                        Text(
                            text = category?.let { "${it.emoji} ${it.label}" } ?: categoryName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )

                        Text(
                            text = buildString {
                                append(expenses.size)
                                append(" item(ns) • Total R$ ")
                                append(
                                    String.format(
                                        Locale.getDefault(),
                                        "%,.2f",
                                        total
                                    )
                                )
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            items(
                items = expenses,
                key = { it.id }
            ) { expense ->
                TransactionItem(
                    emoji = expense.category.emoji,
                    title = expense.description,
                    subtitle = buildString {
                        append(expense.date.format(expenseCategoryDateFormatter))
                        if (expense.notes.isNotBlank()) {
                            append(" • ${expense.notes}")
                        }
                    },
                    amount = expense.amount,
                    isExpense = true,
                    onClick = {
                        onEditExpense(expense.id)
                    },
                    onLongClick = {
                        expenseToDelete = expense
                    }
                )
            }
        }
    }

    expenseToDelete?.let { selectedExpense ->
        AlertDialog(
            onDismissRequest = {
                expenseToDelete = null
            },
            title = {
                Text(text = "Excluir despesa")
            },
            text = {
                Text(
                    text = "Deseja excluir a despesa \"${selectedExpense.description}\"?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteExpense(selectedExpense.id)
                        expenseToDelete = null
                    }
                ) {
                    Text(text = "Excluir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        expenseToDelete = null
                    }
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}