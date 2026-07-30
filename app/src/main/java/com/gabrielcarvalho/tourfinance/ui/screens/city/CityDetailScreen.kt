package com.gabrielcarvalho.tourfinance.ui.screens.city

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielcarvalho.tourfinance.ui.components.FinanceCard
import com.gabrielcarvalho.tourfinance.ui.components.SwipeToDeleteItem
import com.gabrielcarvalho.tourfinance.ui.components.TransactionItem
import com.gabrielcarvalho.tourfinance.ui.screens.tour.TourViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDetailScreen(
    tourId: Long,
    cityName: String,
    onAddIncome: (String) -> Unit,
    onAddExpense: (String) -> Unit,
    onEditIncome: (Long) -> Unit,
    onEditExpense: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TourViewModel = hiltViewModel()
) {
    LaunchedEffect(tourId) {
        viewModel.loadTourDetail(tourId)
    }

    val uiState by viewModel.detailUiState.collectAsStateWithLifecycle()

    val cityStop = remember(uiState.tourStops, cityName) {
        uiState.tourStops.firstOrNull { it.cityName.equals(cityName, ignoreCase = true) }
    }

    val cityIncomes = remember(uiState.incomes, cityName) {
        uiState.incomes
            .filter { it.city.equals(cityName, ignoreCase = true) }
            .sortedByDescending { it.date }
    }

    val cityExpenses = remember(uiState.expenses, cityName) {
        uiState.expenses
            .filter { it.city.equals(cityName, ignoreCase = true) }
            .sortedByDescending { it.date }
    }

    val totalIncome = remember(cityIncomes) { cityIncomes.sumOf { it.amount } }
    val totalExpenses = remember(cityExpenses) { cityExpenses.sumOf { it.amount } }
    val balance = totalIncome - totalExpenses

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cityName) },
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = cityName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        cityStop?.let { stop ->
                            Text(
                                text = "Show em ${stop.showDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "${cityIncomes.size + cityExpenses.size} movimentação(ões)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FinanceCard(
                            title = "Receitas",
                            amount = totalIncome,
                            isPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                        FinanceCard(
                            title = "Despesas",
                            amount = totalExpenses,
                            isPositive = false,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    FinanceCard(
                        title = "Saldo em $cityName",
                        amount = balance,
                        isPositive = balance >= 0,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { onAddIncome(cityName) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Receita")
                        }

                        OutlinedButton(
                            onClick = { onAddExpense(cityName) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RemoveShoppingCart,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Despesa")
                        }
                    }
                }

                if (cityIncomes.isNotEmpty()) {
                    item {
                        HorizontalDivider()
                        Text(
                            text = "Receitas (${cityIncomes.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(cityIncomes, key = { "income_${it.id}" }) { income ->
                        SwipeToDeleteItem(
                            onDelete = { viewModel.deleteIncome(income) }
                        ) {
                            TransactionItem(
                                emoji = income.type.emoji,
                                title = income.description,
                                subtitle = buildString {
                                    append("${income.type.emoji} ${income.type.label}")
                                    if (income.city.isNotBlank()) {
                                        append(" • ${income.city}")
                                    }
                                    append(" • ${income.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}")
                                    if (income.notes.isNotBlank()) {
                                        append(" • ${income.notes}")
                                    }
                                },
                                amount = income.amount,
                                isExpense = false,
                                onClick = { onEditIncome(income.id) }
                            )
                        }
                    }
                }

                if (cityExpenses.isNotEmpty()) {
                    item {
                        HorizontalDivider()
                        Text(
                            text = "Despesas (${cityExpenses.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(cityExpenses, key = { "expense_${it.id}" }) { expense ->
                        SwipeToDeleteItem(
                            onDelete = { viewModel.deleteExpense(expense) }
                        ) {
                            TransactionItem(
                                emoji = expense.category.emoji,
                                title = expense.description,
                                subtitle = buildString {
                                    append("${expense.category.emoji} ${expense.category.label}")
                                    if (expense.city.isNotBlank()) {
                                        append(" • ${expense.city}")
                                    }
                                    append(" • ${expense.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}")
                                    if (expense.notes.isNotBlank()) {
                                        append(" • ${expense.notes}")
                                    }
                                },
                                amount = expense.amount,
                                isExpense = true,
                                onClick = { onEditExpense(expense.id) }
                            )
                        }
                    }
                }

                if (cityIncomes.isEmpty() && cityExpenses.isEmpty()) {
                    item {
                        HorizontalDivider()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Nenhuma movimentação ainda",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Adicione receitas e despesas desta cidade para acompanhar o resultado do show.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}