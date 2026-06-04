package com.gabrielcarvalho.tourfinance.ui.screens.tour

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gabrielcarvalho.tourfinance.ui.util.DateVisualTransformation
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTourScreen(
    bandId: Long,
    onTourCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TourViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var startDateText by remember { mutableStateOf("") }
    var endDateText   by remember { mutableStateOf("") }

    // Date Picker — Início
    if (showStartPicker) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let {
                        startDate = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                        startDateText = startDate!!.format(DateTimeFormatter.ofPattern("ddMMuuuu"))
                    }
                    showStartPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    // Date Picker — Fim
    if (showEndPicker) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let {
                        endDate = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                        endDateText = endDate!!.format(DateTimeFormatter.ofPattern("ddMMuuuu"))
                    }
                    showEndPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Turnê") },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Informações da Turnê", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = false },
                label = { Text("Nome da Turnê") },
                placeholder = { Text("Ex: Tour Sul 2025") },
                isError = nameError,
                supportingText = { if (nameError) Text("Informe um nome para a turnê") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Campo data início
            OutlinedTextField(
                value = startDateText,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }
                    if (digits.length <= 8) {
                        startDateText = digits
                        if (digits.length == 8) {
                            startDate = runCatching {
                                LocalDate.of(
                                    digits.substring(4, 8).toInt(),
                                    digits.substring(2, 4).toInt(),
                                    digits.substring(0, 2).toInt()
                                )
                            }.getOrNull()
                        }
                    }
                },
                label = { Text("Data de início") },
                placeholder = { Text("DD/MM/AAAA") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = DateVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showStartPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Abrir calendário")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo data fim (opcional)
            OutlinedTextField(
                value = endDateText,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }
                    if (digits.length <= 8) {
                        endDateText = digits
                        if (digits.length == 8) {
                            endDate = runCatching {
                                LocalDate.of(
                                    digits.substring(4, 8).toInt(),
                                    digits.substring(2, 4).toInt(),
                                    digits.substring(0, 2).toInt()
                                )
                            }.getOrNull()
                        }
                    }
                },
                label = { Text("Data de fim (opcional)") },
                placeholder = { Text("DD/MM/AAAA") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = DateVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showEndPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Abrir calendário")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        viewModel.createTour(
                            bandId = bandId,
                            name = name.trim(),
                            startDate = startDate ?: LocalDate.now(),
                            endDate = endDate,
                            notes = ""
                        )
                        onTourCreated()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Criar Turnê")
            }
        }
    }
}