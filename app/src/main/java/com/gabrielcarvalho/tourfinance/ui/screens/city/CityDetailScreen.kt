package com.gabrielcarvalho.tourfinance.ui.screens.city

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielcarvalho.tourfinance.ui.components.FinanceCard
import com.gabrielcarvalho.tourfinance.ui.components.FinanceCategoryChart
import com.gabrielcarvalho.tourfinance.ui.components.FinanceChartItem
import com.gabrielcarvalho.tourfinance.ui.screens.tour.TourViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDetailScreen(
    tourId: Long,
    cityName: String,
    onAddIncome: (String) -> Unit,
    onAddExpense: (String) -> Unit,
    onOpenIncomeCategory: (String, String) -> Unit,
    onOpenExpenseCategory: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TourViewModel = hiltViewModel()
) {
    LaunchedEffect(tourId) {
        viewModel.loadTourDetail(tourId)
    }

    val uiState by viewModel.detailUiState.collectAsStateWithLifecycle()

    var showDeleteCityDialog by remember {
        mutableStateOf(false)
    }

    val cityStop = remember(uiState.tourStops, cityName) {
        uiState.tourStops.firstOrNull {
            it.cityName.equals(cityName, ignoreCase = true)
        }
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

    val groupedIncomes = remember(cityIncomes) {
        cityIncomes
            .groupBy { it.type }
            .toList()
            .sortedByDescending { (_, incomes) ->
                incomes.sumOf { it.amount }
            }
    }

    val groupedExpenses = remember(cityExpenses) {
        cityExpenses
            .groupBy { it.category }
            .toList()
            .sortedByDescending { (_, expenses) ->
                expenses.sumOf { it.amount }
            }
    }

    val totalIncome = remember(cityIncomes) {
        cityIncomes.sumOf { it.amount }
    }

    val totalExpenses = remember(cityExpenses) {
        cityExpenses.sumOf { it.amount }
    }

    val balance = totalIncome - totalExpenses
    val totalTransactions = cityIncomes.size + cityExpenses.size

    val incomePalette = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        Color(0xFF2A9D8F),
        Color(0xFF4CAF50),
        Color(0xFF219EBC)
    )

    val expensePalette = listOf(
        MaterialTheme.colorScheme.error,
        Color(0xFFE76F51),
        Color(0xFFF4A261),
        Color(0xFFD62828),
        Color(0xFFB56576),
        Color(0xFF9C6644)
    )

    val chartItems = remember(groupedIncomes, groupedExpenses, incomePalette, expensePalette) {
        buildList {
            groupedIncomes.forEachIndexed { index, (type, incomes) ->
                val total = incomes.sumOf { it.amount }

                if (total > 0.0) {
                    add(
                        FinanceChartItem(
                            label = "Receita • ${type.label}",
                            value = total.toFloat(),
                            color = incomePalette[index % incomePalette.size]
                        )
                    )
                }
            }

            groupedExpenses.forEachIndexed { index, (category, expenses) ->
                val total = expenses.sumOf { it.amount }

                if (total > 0.0) {
                    add(
                        FinanceChartItem(
                            label = "Despesa • ${category.label}",
                            value = total.toFloat(),
                            color = expensePalette[index % expensePalette.size]
                        )
                    )
                }
            }
        }
            .sortedByDescending { it.value }
            .take(8)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = cityName)
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
                        modifier = Modifier.fillMaxWidth(),
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
                                    text = "Show em ${stop.showDate.format(dateFormatter)}",
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                if (chartItems.isNotEmpty()) {
                    item {
                        MainSectionHeader(
                            title = "Distribuição por categoria",
                            count = chartItems.size,
                            accentColor = MaterialTheme.colorScheme.primary
                        )
                    }

                    item {
                        FinanceCategoryChart(
                            items = chartItems,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onAddIncome(cityName)
                            },
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

                            Text(text = "+ Receita")
                        }

                        OutlinedButton(
                            onClick = {
                                onAddExpense(cityName)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RemoveShoppingCart,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(text = "+ Despesa")
                        }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = {
                            showDeleteCityDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.45f)
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

                if (groupedIncomes.isNotEmpty()) {
                    item {
                        MainSectionHeader(
                            title = "Receitas por categoria",
                            count = cityIncomes.size,
                            accentColor = MaterialTheme.colorScheme.primary
                        )
                    }

                    groupedIncomes.forEach { (type, incomes) ->
                        item(key = "income_group_${type.name}") {
                            CategoryGroupCard(
                                title = "${type.emoji} ${type.label}",
                                total = incomes.sumOf { it.amount },
                                accentColor = MaterialTheme.colorScheme.primary,
                                isExpense = false,
                                itemCount = incomes.size,
                                onClick = {
                                    onOpenIncomeCategory(cityName, type.name)
                                }
                            )
                        }
                    }
                }

                if (groupedExpenses.isNotEmpty()) {
                    item {
                        MainSectionHeader(
                            title = "Despesas por categoria",
                            count = cityExpenses.size,
                            accentColor = MaterialTheme.colorScheme.error
                        )
                    }

                    groupedExpenses.forEach { (category, expenses) ->
                        item(key = "expense_group_${category.name}") {
                            CategoryGroupCard(
                                title = "${category.emoji} ${category.label}",
                                total = expenses.sumOf { it.amount },
                                accentColor = MaterialTheme.colorScheme.error,
                                isExpense = true,
                                itemCount = expenses.size,
                                onClick = {
                                    onOpenExpenseCategory(cityName, category.name)
                                }
                            )
                        }
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

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showDeleteCityDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteCityDialog = false
            },
            title = {
                Text(text = "Excluir cidade")
            },
            text = {
                Text(
                    text = "Deseja excluir \"$cityName\" da turnê? " +
                            "Esta ação remove a cidade e também todas as receitas " +
                            "e despesas vinculadas a ela."
                )
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
                    Text(text = "Excluir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteCityDialog = false
                    }
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}

@Composable
private fun MainSectionHeader(
    title: String,
    count: Int,
    accentColor: Color
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

@Composable
private fun CategoryGroupCard(
    title: String,
    total: Double,
    accentColor: Color,
    isExpense: Boolean,
    itemCount: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 2.dp,
            color = accentColor.copy(alpha = 0.35f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    text = buildString {
                        append(if (isExpense) "- " else "+ ")
                        append("R$ ")
                        append(
                            String.format(
                                Locale.getDefault(),
                                "%,.2f",
                                total
                            )
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Text(
                text = "$itemCount lançamento(s)",
                style = MaterialTheme.typography.bodySmall,
                color = accentColor.copy(alpha = 0.85f)
            )
        }
    }
}