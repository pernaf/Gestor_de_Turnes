package com.gabrielcarvalho.tourfinance.ui.screens.tour

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
    onNavigateToCity: (String) -> Unit,
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.tour?.name ?: "Carregando...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToCity(section.city) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
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

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Toque para abrir os detalhes da cidade",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider()
                    Text(
                        text = "Início: ${uiState.tour?.startDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}" +
                                (uiState.tour?.endDate?.let {
                                    " • Encerrada: ${it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
                                } ?: ""),
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