package com.example.moveperu_admin_conductor.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moveperu_admin_conductor.model.EstadoViaje
import com.example.moveperu_admin_conductor.model.TipoServicio
import com.example.moveperu_admin_conductor.ui.components.TarjetaViajeItem
import com.example.moveperu_admin_conductor.viewmodel.ViajeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaViajesScreen(
    viajeViewModel: ViajeViewModel,
    onVolver: () -> Unit,
    onVerDetalle: (Int) -> Unit
) {
    val state by viajeViewModel.uiState.collectAsState()
    val filtro = state.filtro

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión y Listado de Viajes") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = filtro.busqueda,
                onValueChange = viajeViewModel::onBusquedaChange,
                label = { Text("Buscar por pasajero, conductor, origen, destino o ID") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Filtro por Estado (chips horizontales)
            Text("Filtrar por Estado", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtro.estadoFiltro == null,
                    onClick = { viajeViewModel.onEstadoFiltroChange(null) },
                    label = { Text("Todos") }
                )
                EstadoViaje.entries.forEach { estado ->
                    FilterChip(
                        selected = filtro.estadoFiltro == estado,
                        onClick = { viajeViewModel.onEstadoFiltroChange(estado) },
                        label = { Text(estado.etiqueta) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Filtro por Tipo de Servicio
            Text("Filtrar por Tipo de Servicio", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtro.tipoServicioFiltro == null,
                    onClick = { viajeViewModel.onTipoServicioFiltroChange(null) },
                    label = { Text("Todos los tipos") }
                )
                TipoServicio.entries.forEach { tipo ->
                    FilterChip(
                        selected = filtro.tipoServicioFiltro == tipo,
                        onClick = { viajeViewModel.onTipoServicioFiltroChange(tipo) },
                        label = { Text(tipo.etiqueta) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Mostrando ${state.listaViajes.size} viajes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            if (state.listaViajes.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No se encontraron viajes con los filtros seleccionados", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.listaViajes, key = { it.id }) { viaje ->
                        TarjetaViajeItem(
                            viaje = viaje,
                            onClick = { onVerDetalle(viaje.id) },
                            onAvanzarEstado = { viajeViewModel.avanzarEstado(viaje) },
                            onAsignarConductor = { onVerDetalle(viaje.id) }
                        )
                    }
                }
            }
        }
    }
}
