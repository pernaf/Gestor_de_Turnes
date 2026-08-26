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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielcarvalho.tourfinance.domain.model.IncomeType
import com.gabrielcarvalho.tourfinance.ui.components.TransactionItem
import com.gabrielcarvalho.tourfinance.ui.screens.tour.TourViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val incomeCategoryDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeCategoryDetailScreen(
    tourId: Long,
    cityName: String,
    typeName: String,
    onEditIncome: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TourViewModel = hiltViewModel()
) {
    LaunchedEffect(tourId) {
        viewModel.loadTourDetail(tourId)
    }

    val uiState by viewModel.detailUiState.collectAsStateWithLifecycle()

    val incomeType = remember(typeName) {
        IncomeType.entries.firstOrNull { it.name == typeName }
    }

    val incomes = remember(uiState.incomes, cityName, typeName) {
        uiState.incomes
            .filter { it.city.equals(cityName, ignoreCase = true) }
            .filter { it.type.name == typeName }
            .sortedByDescending { it.date }
    }

    val total = remember(incomes) {
        incomes.sumOf { it.amount }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = incomeType?.let { "${it.emoji} ${it.label}" } ?: typeName
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = cityName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )

                        Text(
                            text = incomeType?.let { "${it.emoji} ${it.label}" } ?: typeName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = buildString {
                                append(incomes.size)
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
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            items(
                items = incomes,
                key = { it.id }
            ) { income ->
                TransactionItem(
                    emoji = income.type.emoji,
                    title = income.description,
                    subtitle = buildString {
                        append(income.date.format(incomeCategoryDateFormatter))
                        if (income.notes.isNotBlank()) {
                            append(" • ${income.notes}")
                        }
                    },
                    amount = income.amount,
                    isExpense = false,
                    onClick = {
                        onEditIncome(income.id)
                    }
                )
            }
        }
    }
}

