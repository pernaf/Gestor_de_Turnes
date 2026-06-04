package com.gabrielcarvalho.tourfinance.ui.screens.tour

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
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielcarvalho.tourfinance.ui.components.FinanceCard
import com.gabrielcarvalho.tourfinance.ui.components.SwipeToDeleteItem
import com.gabrielcarvalho.tourfinance.ui.components.TransactionItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourDetailScreen(
    tourId: Long,
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onAddIncome,
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
                                onClick = onAddExpense,
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
                }

                if (uiState.incomes.isNotEmpty()) {
                    item {
                        HorizontalDivider()
                        Text(
                            text = "Receitas (${uiState.incomes.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(uiState.incomes, key = { "income_${it.id}" }) { income ->
                        SwipeToDeleteItem(
                            onDelete = { viewModel.deleteIncome(income) }
                        ) {
                            TransactionItem(
                                emoji = "💰",
                                title = income.description,
                                subtitle = buildString {
                                    append(income.type.label)
                                    if (income.city.isNotBlank()) append(" • ${income.city}")
                                    append(" • ${income.date}")
                                },
                                amount = income.amount,
                                isExpense = false,
                                onClick = { onEditIncome(income.id) }
                            )
                        }
                    }
                }

                if (uiState.expenses.isNotEmpty()) {
                    item {
                        HorizontalDivider()
                        Text(
                            text = "Despesas (${uiState.expenses.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(uiState.expenses, key = { "expense_${it.id}" }) { expense ->
                        SwipeToDeleteItem(
                            onDelete = { viewModel.deleteExpense(expense) }
                        ) {
                            TransactionItem(
                                emoji = expense.category.emoji,
                                title = expense.description,
                                subtitle = buildString {
                                    append("${expense.category.label} • ${expense.date}")
                                    if (expense.notes.isNotBlank()) append(" • ${expense.notes}")
                                },
                                amount = expense.amount,
                                isExpense = true,
                                onClick = { onEditExpense(expense.id) }
                            )
                        }
                    }
                }

                if (uiState.incomes.isEmpty() && uiState.expenses.isEmpty()) {
                    item {
                        HorizontalDivider()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎵",
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nenhuma transação ainda.\nAdicione receitas e despesas da tour!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                item {
                    HorizontalDivider()
                    Text(
                        text = "Início: ${uiState.tour?.startDate}" +
                                (uiState.tour?.endDate?.let { " • Encerrada: $it" } ?: ""),
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