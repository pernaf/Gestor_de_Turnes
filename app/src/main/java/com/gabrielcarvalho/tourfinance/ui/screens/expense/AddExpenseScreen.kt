package com.gabrielcarvalho.tourfinance.ui.screens.expense

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gabrielcarvalho.tourfinance.domain.model.ExpenseCategory
import com.gabrielcarvalho.tourfinance.ui.utils.MoneyInputUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    tourId: Long,
    onNavigateBack: () -> Unit,
    expenseId: Long = 0L,
    preselectedCity: String = "",
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val editState by viewModel.editUiState.collectAsState()
    val savedSuccessfully by viewModel.savedSuccessfully.collectAsState()

    val isEditing = expenseId > 0L
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    var description by rememberSaveable(tourId, expenseId) {
        mutableStateOf("")
    }
    var amountText by rememberSaveable(tourId, expenseId) {
        mutableStateOf("")
    }
    var selectedDateText by rememberSaveable(tourId, expenseId) {
        mutableStateOf(LocalDate.now().toString())
    }
    var categoryName by rememberSaveable(tourId, expenseId) {
        mutableStateOf(ExpenseCategory.OTHER.name)
    }
    var cityText by rememberSaveable(tourId, expenseId, preselectedCity) {
        mutableStateOf(preselectedCity)
    }
    var notes by rememberSaveable(tourId, expenseId) {
        mutableStateOf("")
    }
    var initialized by rememberSaveable(tourId, expenseId) {
        mutableStateOf(false)
    }
    var descriptionError by rememberSaveable(tourId, expenseId) {
        mutableStateOf(false)
    }
    var amountError by rememberSaveable(tourId, expenseId) {
        mutableStateOf(false)
    }
    var saveRequested by rememberSaveable(tourId, expenseId) {
        mutableStateOf(false)
    }
    var categoryMenuExpanded by remember {
        mutableStateOf(false)
    }

    val selectedDate = LocalDate.parse(selectedDateText)
    val selectedCategory = ExpenseCategory.valueOf(categoryName)
    val canEdit = !saveRequested && (!isEditing || initialized)

    LaunchedEffect(tourId, expenseId) {
        viewModel.resetSaveState()

        if (isEditing) {
            viewModel.loadExpense(
                expenseId = expenseId,
                tourId = tourId
            )
        } else {
            viewModel.clearEditState()
        }
    }

    LaunchedEffect(editState.expense, tourId, expenseId) {
        val expense = editState.expense

        if (
            isEditing &&
            !initialized &&
            expense != null &&
            expense.id == expenseId &&
            expense.tourId == tourId
        ) {
            description = expense.description
            amountText = MoneyInputUtils.formatMoneyForInput(expense.amount)
            selectedDateText = expense.date.toString()
            categoryName = expense.category.name
            cityText = expense.city
            notes = expense.notes
            initialized = true
        }
    }

    LaunchedEffect(savedSuccessfully) {
        if (savedSuccessfully) {
            viewModel.resetSaveState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) {
                            "Editar despesa"
                        } else {
                            "Nova despesa"
                        }
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Voltar")
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isEditing && !initialized) {
                if (editState.isLoading) {
                    CircularProgressIndicator()
                }

                editState.error?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    descriptionError = false
                },
                label = { Text("Descrição") },
                singleLine = true,
                enabled = canEdit,
                isError = descriptionError,
                supportingText = {
                    if (descriptionError) {
                        Text("Informe a descrição.")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = MoneyInputUtils.sanitizeMoneyInput(it)
                    amountError = false
                },
                label = { Text("Valor (R$)") },
                placeholder = { Text("0,00") },
                singleLine = true,
                enabled = canEdit,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                isError = amountError,
                supportingText = {
                    if (amountError) {
                        Text("Informe um valor válido maior que zero.")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                OutlinedButton(
                    onClick = { categoryMenuExpanded = true },
                    enabled = canEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Categoria: ${selectedCategory.label}")
                }

                DropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = {
                        categoryMenuExpanded = false
                    }
                ) {
                    ExpenseCategory.values().forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.label) },
                            onClick = {
                                categoryName = category.name
                                categoryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            selectedDateText = LocalDate.of(
                                year,
                                month + 1,
                                day
                            ).toString()
                        },
                        selectedDate.year,
                        selectedDate.monthValue - 1,
                        selectedDate.dayOfMonth
                    ).show()
                },
                enabled = canEdit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Data: ${selectedDate.format(dateFormatter)}")
            }

            OutlinedTextField(
                value = cityText,
                onValueChange = { cityText = it },
                label = { Text("Cidade") },
                singleLine = true,
                enabled = canEdit,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Observações") },
                minLines = 3,
                enabled = canEdit,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                enabled = canEdit,
                onClick = {
                    val amount = MoneyInputUtils.parseMoneyValue(amountText)

                    descriptionError = description.isBlank()
                    amountError = amount == null ||
                            !amount.isFinite() ||
                            amount <= 0.0

                    if (!descriptionError && !amountError && amount != null) {
                        saveRequested = true

                        viewModel.saveExpense(
                            tourId = tourId,
                            expenseId = expenseId,
                            description = description.trim(),
                            amount = amount,
                            date = selectedDate,
                            category = selectedCategory,
                            notes = notes.trim(),
                            city = cityText.trim()
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (saveRequested) {
                        "Salvando..."
                    } else {
                        "Salvar despesa"
                    }
                )
            }
        }
    }
}