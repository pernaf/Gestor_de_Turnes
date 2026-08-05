package com.gabrielcarvalho.tourfinance.ui.screens.city

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielcarvalho.tourfinance.domain.model.Expense
import com.gabrielcarvalho.tourfinance.domain.model.Income
import com.gabrielcarvalho.tourfinance.ui.components.FinanceCard
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

    var showDeleteCityDialog by remember { mutableStateOf(false) }
    var incomeToDelete by remember { mutableStateOf<Income?>(null) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

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
    val totalTransactions = cityIncomes.size + cityExpenses.size

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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Visão da cidade",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )

                            Text(
                                text = cityName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )

                            cityStop?.let { stop ->
                                Text(
                                    text = "Show em ${stop.showDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f)
                                )
                            }

                            Text(
                                text = "$totalTransactions movimentação(ões)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f)
                            )
                        }
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { onAddIncome(cityName) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
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
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
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

                item {
                    OutlinedButton(
                        onClick = { showDeleteCityDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.5.dp,
                            MaterialTheme.colorScheme.error.copy(alpha = 0.45f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Excluir cidade",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (cityIncomes.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Receitas",
                            count = cityIncomes.size,
                            accentColor = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(cityIncomes, key = { "income_${it.id}" }) { income ->
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
                            onClick = { onEditIncome(income.id) },
                            onLongClick = { incomeToDelete = income }
                        )
                    }
                }

                if (cityExpenses.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Despesas",
                            count = cityExpenses.size,
                            accentColor = MaterialTheme.colorScheme.error
                        )
                    }

                    items(cityExpenses, key = { "expense_${it.id}" }) { expense ->
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
                            onClick = { onEditExpense(expense.id) },
                            onLongClick = { expenseToDelete = expense }
                        )
                    }
                }

                if (cityIncomes.isEmpty() && cityExpenses.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            border = BorderStroke(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = CircleShape
                                            )
                                    )

                                    Text(
                                        text = "Nenhuma movimentação ainda",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Text(
                                    text = "Adicione receitas e despesas desta cidade para acompanhar o resultado do show.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.padding(bottom = 6.dp))
                }
            }
        }
    }

    if (showDeleteCityDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteCityDialog = false },
            title = { Text("Excluir cidade") },
            text = {
                Text("Deseja excluir \"$cityName\" da turnê? Esta ação remove a cidade e também todas as receitas e despesas vinculadas a ela.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        cityStop?.let { stop ->
                            viewModel.deleteCityWithTransactions(stop)
                        }
                        showDeleteCityDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCityDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (incomeToDelete != null) {
        AlertDialog(
            onDismissRequest = { incomeToDelete = null },
            title = { Text("Excluir receita") },
            text = {
                Text("Deseja excluir a receita \"${incomeToDelete?.description}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        incomeToDelete?.let { viewModel.deleteIncome(it) }
                        incomeToDelete = null
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { incomeToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (expenseToDelete != null) {
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Excluir despesa") },
            text = {
                Text("Deseja excluir a despesa \"${expenseToDelete?.description}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        expenseToDelete?.let { viewModel.deleteExpense(it) }
                        expenseToDelete = null
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.10f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )

            Text(
                text = "$count item(ns)",
                style = MaterialTheme.typography.bodySmall,
                color = accentColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}