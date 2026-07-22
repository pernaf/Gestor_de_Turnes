package com.gabrielcarvalho.tourfinance.ui.screens.tour

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielcarvalho.tourfinance.domain.model.Expense
import com.gabrielcarvalho.tourfinance.domain.model.Income
import com.gabrielcarvalho.tourfinance.ui.components.FinanceCard
import com.gabrielcarvalho.tourfinance.ui.components.SwipeToDeleteItem
import com.gabrielcarvalho.tourfinance.ui.components.TransactionItem
import java.time.format.DateTimeFormatter

private data class CitySection(
    val stopId: Long,
    val city: String,
    val showDateText: String,
    val incomes: List<Income>,
    val expenses: List<Expense>
) {
    val totalIncome: Double get() = incomes.sumOf { it.amount }
    val totalExpenses: Double get() = expenses.sumOf { it.amount }
    val balance: Double get() = totalIncome - totalExpenses
    val totalTransactions: Int get() = incomes.size + expenses.size
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourDetailScreen(
    tourId: Long,
    onAddExpense: (city: String) -> Unit,
    onAddIncome: (city: String) -> Unit,
    onAddTourStop: () -> Unit,
    onEditExpense: (expenseId: Long) -> Unit,
    onEditIncome: (incomeId: Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TourViewModel = hiltViewModel()
) {
    LaunchedEffect(tourId) {
        viewModel.loadTourDetail(tourId)
    }

    val uiState by viewModel.detailUiState.collectAsStateWithLifecycle()
    var showCloseTourDialog by remember { mutableStateOf(false) }

    val citySections = remember(uiState.tourStops, uiState.incomes, uiState.expenses) {
        uiState.tourStops
            .sortedBy { it.showDate }
            .map { stop ->
                CitySection(
                    stopId = stop.id,
                    city = stop.cityName,
                    showDateText = stop.showDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    incomes = uiState.incomes
                        .filter { it.city.equals(stop.cityName, ignoreCase = true) }
                        .sortedByDescending { it.date },
                    expenses = uiState.expenses
                        .filter { it.city.equals(stop.cityName, ignoreCase = true) }
                        .sortedByDescending { it.date }
                )
            }
    }

    val expandedCities = remember { mutableStateMapOf<Long, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.tour?.name ?: "Carregando...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    if (uiState.tour?.status?.name == "ACTIVE") {
                        TextButton(onClick = { showCloseTourDialog = true }) {
                            Text("Encerrar")
                        }
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FinanceCard(
                            title = "Receitas",
                            amount = uiState.totalIncome,
                            isPositive = true,
                            modifier = Modifier.weight(1f)
                        )
                        FinanceCard(
                            title = "Despesas",
                            amount = uiState.totalExpenses,
                            isPositive = false,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    FinanceCard(
                        title = "Saldo da Tour",
                        amount = uiState.balance,
                        isPositive = uiState.balance >= 0,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedButton(
                        onClick = onAddTourStop,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Cidade da turnê")
                    }
                }

                if (uiState.tourStops.isEmpty()) {
                    item {
                        HorizontalDivider()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Cidades da Turnê",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Nenhuma cidade adicionada ainda. Cadastre as paradas da turnê para organizar receitas e despesas por cidade.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    item {
                        HorizontalDivider()
                        Text(
                            text = "Cidades da Turnê (${uiState.tourStops.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(citySections, key = { "city_${it.stopId}" }) { section ->
                        val expanded = expandedCities[section.stopId] ?: false
                        val rotation by animateFloatAsState(
                            targetValue = if (expanded) 180f else 0f,
                            label = "arrowRotation"
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedCities[section.stopId] = !expanded
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = section.city,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Show em ${section.showDateText}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${section.totalTransactions} movimentação(ões)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.rotate(rotation)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    FinanceCard(
                                        title = "Receitas",
                                        amount = section.totalIncome,
                                        isPositive = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    FinanceCard(
                                        title = "Despesas",
                                        amount = section.totalExpenses,
                                        isPositive = false,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                FinanceCard(
                                    title = "Saldo em ${section.city}",
                                    amount = section.balance,
                                    isPositive = section.balance >= 0,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                AnimatedVisibility(visible = expanded) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.padding(top = 12.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Button(
                                                onClick = { onAddIncome(section.city) },
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
                                                onClick = { onAddExpense(section.city) },
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

                                        if (section.incomes.isNotEmpty()) {
                                            HorizontalDivider()
                                            Text(
                                                text = "Receitas (${section.incomes.size})",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold
                                            )

                                            section.incomes.forEach { income ->
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

                                        if (section.expenses.isNotEmpty()) {
                                            HorizontalDivider()
                                            Text(
                                                text = "Despesas (${section.expenses.size})",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold
                                            )

                                            section.expenses.forEach { expense ->
                                                SwipeToDeleteItem(
                                                    onDelete = { viewModel.deleteExpense(expense) }
                                                ) {
                                                    TransactionItem(
                                                        emoji = expense.category.emoji,
                                                        title = expense.description,
                                                        subtitle = buildString {
                                                            append("${expense.category.emoji} ${expense.category.label}")
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
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider()
                    Text(
                        text = "Início: ${uiState.tour?.startDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}" +
                                (uiState.tour?.endDate?.let { " • Encerrada: ${it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}" } ?: ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    if (showCloseTourDialog) {
        AlertDialog(
            onDismissRequest = { showCloseTourDialog = false },
            title = { Text("Encerrar Turnê") },
            text = {
                Text(
                    "Deseja encerrar \"${uiState.tour?.name}\"? Esta ação registrará a data de hoje como fim da turnê."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        uiState.tour?.let { viewModel.closeTour(it) }
                        showCloseTourDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Encerrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseTourDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}