package com.gabrielcarvalho.tourfinance.ui.screens.tourstop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.Normalizer
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private object BrazilCities {
    // Aqui está uma amostra maior.
    // Minha recomendação real: mover isso depois para um arquivo/object separado
    // e substituir pela lista completa dos municípios do Brasil.
    val all = listOf(
        "Abaetetuba",
        "Abreu e Lima",
        "Açailândia",
        "Acrelândia",
        "Adamantina",
        "Afonso Cláudio",
        "Agrestina",
        "Águas Lindas de Goiás",
        "Alagoinhas",
        "Alegre",
        "Alfenas",
        "Almirante Tamandaré",
        "Altamira",
        "Anápolis",
        "Angra dos Reis",
        "Ananindeua",
        "Aparecida de Goiânia",
        "Aquidauana",
        "Arapiraca",
        "Aracaju",
        "Araguaína",
        "Araguari",
        "Araçatuba",
        "Araraquara",
        "Araripina",
        "Araruama",
        "Araxá",
        "Atibaia",
        "Bagé",
        "Barbacena",
        "Barcarena",
        "Barreiras",
        "Barretos",
        "Belém",
        "Belford Roxo",
        "Belo Horizonte",
        "Blumenau",
        "Boa Vista",
        "Botucatu",
        "Bragança Paulista",
        "Brasília",
        "Cabo Frio",
        "Cabo de Santo Agostinho",
        "Cachoeiro de Itapemirim",
        "Camaçari",
        "Campina Grande",
        "Campinas",
        "Campo Grande",
        "Campos dos Goytacazes",
        "Canindé",
        "Carapicuíba",
        "Caruaru",
        "Cascavel",
        "Castanhal",
        "Catalão",
        "Catanduva",
        "Caxias",
        "Caxias do Sul",
        "Chapecó",
        "Colatina",
        "Contagem",
        "Corumbá",
        "Criciúma",
        "Cuiabá",
        "Curitiba",
        "Diadema",
        "Divinópolis",
        "Dourados",
        "Duque de Caxias",
        "Embu das Artes",
        "Erechim",
        "Eunápolis",
        "Feira de Santana",
        "Florianópolis",
        "Formosa",
        "Fortaleza",
        "Foz do Iguaçu",
        "Franca",
        "Garanhuns",
        "Goiânia",
        "Governador Valadares",
        "Gravataí",
        "Guarapuava",
        "Guarujá",
        "Guarulhos",
        "Ilhéus",
        "Imperatriz",
        "Indaiatuba",
        "Ipatinga",
        "Itabaiana",
        "Itabira",
        "Itaboraí",
        "Itabuna",
        "Itajaí",
        "Itapecerica da Serra",
        "Itapetininga",
        "Itapevi",
        "Itaquaquecetuba",
        "Jaboatão dos Guararapes",
        "Jacareí",
        "Ji-Paraná",
        "João Pessoa",
        "Joinville",
        "Juazeiro",
        "Juazeiro do Norte",
        "Juiz de Fora",
        "Jundiaí",
        "Lages",
        "Lauro de Freitas",
        "Limeira",
        "Linhares",
        "Londrina",
        "Luziânia",
        "Macaé",
        "Macapá",
        "Maceió",
        "Manaus",
        "Marabá",
        "Maricá",
        "Maringá",
        "Mauá",
        "Mogi das Cruzes",
        "Montes Claros",
        "Mossoró",
        "Natal",
        "Nilópolis",
        "Niterói",
        "Nova Friburgo",
        "Nova Iguaçu",
        "Novo Hamburgo",
        "Olinda",
        "Osasco",
        "Ourinhos",
        "Palhoça",
        "Palmas",
        "Parauapebas",
        "Parintins",
        "Parnamirim",
        "Passo Fundo",
        "Patos",
        "Patos de Minas",
        "Paulista",
        "Pelotas",
        "Petrolina",
        "Petrópolis",
        "Pindamonhangaba",
        "Piracicaba",
        "Ponta Grossa",
        "Porto Alegre",
        "Porto Seguro",
        "Porto Velho",
        "Poços de Caldas",
        "Praia Grande",
        "Presidente Prudente",
        "Recife",
        "Resende",
        "Ribeirão Preto",
        "Rio Branco",
        "Rio Claro",
        "Rio Verde",
        "Rio das Ostras",
        "Rio de Janeiro",
        "Rondonópolis",
        "Santarém",
        "Santo André",
        "Santo Ângelo",
        "Santos",
        "São Bernardo do Campo",
        "São Caetano do Sul",
        "São Carlos",
        "São Gonçalo",
        "São João de Meriti",
        "São José",
        "São José do Rio Preto",
        "São José dos Campos",
        "São José dos Pinhais",
        "São Leopoldo",
        "São Luís",
        "São Paulo",
        "São Vicente",
        "Serra",
        "Sete Lagoas",
        "Sinop",
        "Sobral",
        "Sorocaba",
        "Sumaré",
        "Suzano",
        "Taubaté",
        "Teixeira de Freitas",
        "Teresina",
        "Tubarão",
        "Uberaba",
        "Uberlândia",
        "Umuarama",
        "Uruguaiana",
        "Valinhos",
        "Varginha",
        "Várzea Grande",
        "Vitória",
        "Vitória da Conquista",
        "Volta Redonda"
    )
}

private fun normalize(text: String): String {
    return Normalizer
        .normalize(text.trim(), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .lowercase()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTourStopScreen(
    tourId: Long,
    onNavigateBack: () -> Unit,
    viewModel: TourStopViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val filteredCities = remember(uiState.cityName) {
        val query = normalize(uiState.cityName)

        if (query.isBlank()) {
            BrazilCities.all.take(20)
        } else {
            val startsWithMatches = BrazilCities.all.filter {
                normalize(it).startsWith(query)
            }

            val containsMatches = BrazilCities.all.filter {
                !normalize(it).startsWith(query) && normalize(it).contains(query)
            }

            (startsWithMatches + containsMatches).take(30)
        }
    }

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            viewModel.clearSavedState()
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar cidade da turnê") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    ExposedDropdownMenuBox(
                        expanded = expanded && filteredCities.isNotEmpty(),
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        OutlinedTextField(
                            value = uiState.cityName,
                            onValueChange = {
                                viewModel.onCityNameChange(it)
                                expanded = true
                            },
                            label = { Text("Cidade") },
                            placeholder = { Text("Digite a cidade do show") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            singleLine = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded && filteredCities.isNotEmpty(),
                            onDismissRequest = { expanded = false }
                        ) {
                            filteredCities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city) },
                                    onClick = {
                                        viewModel.onCityNameChange(city)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    ) {
                        OutlinedTextField(
                            value = uiState.showDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Data do show") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null
                                )
                            }
                        )
                    }

                    Text(
                        text = "Selecione a cidade e a data em que o show será realizado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { viewModel.saveTourStop(tourId) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    ) {
                        Text(if (uiState.isSaving) "Salvando..." else "Salvar cidade")
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.showDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val selectedDate = Instant.ofEpochMilli(selectedMillis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            viewModel.onShowDateChange(selectedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}