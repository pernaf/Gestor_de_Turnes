package com.gabrielcarvalho.tourfinance.ui.screens.tour

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielcarvalho.tourfinance.domain.model.Tour
import com.gabrielcarvalho.tourfinance.domain.model.TourStatus
import com.gabrielcarvalho.tourfinance.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TourListScreen(
    bandId: Long,
    bandName: String,
    onTourClick: (Long) -> Unit,
    onCreateTour: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TourViewModel = hiltViewModel()
) {
    val uiState by viewModel.listUiState.collectAsStateWithLifecycle()
    var tourToDelete by remember { mutableStateOf<Tour?>(null) }

    LaunchedEffect(bandId) {
        viewModel.loadToursByBand(bandId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(bandName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTour) {
                Icon(Icons.Default.Add, contentDescription = "Nova Turnê")
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.tours.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.MusicNote,
                    message = "Nenhuma turnê cadastrada ainda.\nCrie a sua primeira!",
                    actionLabel = "Criar Turnê",
                    onAction = onCreateTour,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.tours, key = { it.id }) { tour ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { onTourClick(tour.id) },
                                    onLongClick = {
                                        // Só permite excluir turnês encerradas
                                        if (tour.status == TourStatus.CLOSED) {
                                            tourToDelete = tour
                                        }
                                    }
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tour.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Início: ${tour.startDate}" +
                                                (tour.endDate?.let { " • Fim: $it" } ?: ""),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Badge(
                                    containerColor = if (tour.status == TourStatus.ACTIVE) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ) {
                                    Text(
                                        text = if (tour.status == TourStatus.ACTIVE) "Ativa" else "Encerrada",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: confirmar exclusão de turnê
    tourToDelete?.let { tour ->
        AlertDialog(
            onDismissRequest = { tourToDelete = null },
            title = { Text("Excluir turnê") },
            text = {
                Text("Excluir \"${tour.name}\"? Todas as receitas e despesas dessa turnê serão apagadas.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTour(tour)
                    tourToDelete = null
                }) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { tourToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}