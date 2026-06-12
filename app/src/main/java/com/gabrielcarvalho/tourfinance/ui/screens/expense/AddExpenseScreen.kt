package com.gabrielcarvalho.tourfinance.ui.screens.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielcarvalho.tourfinance.domain.model.ExpenseCategory

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    tourId: Long,
    expenseId: Long = 0L,
    onNavigateBack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val isEditing = expenseId != 0L
    val expenseToEdit by viewModel.expenseToEdit.collectAsStateWithLifecycle()

    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(ExpenseCategory.OTHER) }
    var notes by remember { mutableStateOf("") }
    var descError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var fieldsLoaded by remember { mutableStateOf(false) }
    var city by remember { mutableStateOf("") }

    // Carrega dados se for edição
    LaunchedEffect(expenseId) {
        if (isEditing) viewModel.loadExpense(expenseId, tourId)
    }

    // Preenche os campos quando o expense carregar
    LaunchedEffect(expenseToEdit) {
        if (isEditing && !fieldsLoaded && expenseToEdit != null) {
            expenseToEdit!!.let { e ->
                description = e.description
                amountText = e.amount.toString()
                selectedCat = e.category
                notes = e.notes
                fieldsLoaded = true
                city = e.city
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Despesa" else "Nova Despesa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    descError = false
                },
                label = { Text("Descrição") },
                placeholder = { Text("Ex: Combustível SP→RJ") },
                isError = descError,
                supportingText = { if (descError) Text("Informe a descrição") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    amountError = false
                },
                label = { Text("Valor (R$)") },
                placeholder = { Text("0,00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError,
                supportingText = { if (amountError) Text("Informe um valor válido") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Cidade (opcional)") },
                placeholder = { Text("Ex: São Paulo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "Categoria",
                style = MaterialTheme.typography.titleSmall
            )

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ExpenseCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = selectedCat == cat,
                        onClick = { selectedCat = cat },
                        label = { Text("${cat.emoji} ${cat.label}") }
                    )
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Observações (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Button(
                onClick = {
                    val amount = amountText.replace(",", ".").toDoubleOrNull()
                    descError = description.isBlank()
                    amountError = amount == null || amount <= 0
                    if (!descError && !amountError) {
                        viewModel.saveExpense(
                            tourId = tourId,
                            expenseId = if (isEditing) expenseId else 0L,
                            description = description.trim(),
                            amount = amount!!,
                            category = selectedCat,
                            notes = notes.trim(),
                            city = city.trim()
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (isEditing) "Salvar Alterações" else "Salvar Despesa")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}