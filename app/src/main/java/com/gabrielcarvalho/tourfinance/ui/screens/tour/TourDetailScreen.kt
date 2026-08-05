package com.gabrielcarvalho.tourfinance.ui.screens.tour

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.clip
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
                                text = "Resumo da turnê",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )

                            Text(
                                text = uiState.tour?.name ?: "",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )

                            Text(
                                text = "Início: ${uiState.tour?.startDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}" +
                                        (uiState.tour?.endDate?.let {
                                            " • Encerrada: ${it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
                                        } ?: ""),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
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
                        title = "Saldo da tour",
                        amount = uiState.balance,
                        isPositive = uiState.balance >= 0,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedButton(
                        onClick = onAddTourStop,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("+ Cidade da turnê")
                    }
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Cidades da turnê",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (uiState.tourStops.isEmpty()) {
                                "Adicione as paradas para organizar receitas e despesas por cidade."
                            } else {
                                "${uiState.tourStops.size} cidade(s) cadastrada(s)"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (uiState.tourStops.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Nenhuma cidade adicionada ainda",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Cadastre as paradas da turnê para acompanhar resultados por cidade e manter a organização financeira do percurso.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(citySections, key = { "city_${it.stopId}" }) { section ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToCity(section.city) },
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 24.dp,
                                                topEnd = 24.dp,
                                                bottomStart = 18.dp,
                                                bottomEnd = 18.dp
                                            )
                                        )
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                                        .padding(horizontal = 18.dp, vertical = 14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = section.city,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Text(
                                                text = "Show em ${section.showDateText}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                                    shape = CircleShape
                                                )
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "${section.totalTransactions} mov.",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
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

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(18.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
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
                                                text = "Saldo da cidade",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Text(
                                                text = "R$ ${"%,.2f".format(section.balance)}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (section.balance >= 0) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.error
                                                }
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = CircleShape
                                                )
                                        )

                                        Text(
                                            text = "Toque para abrir os detalhes da cidade",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
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