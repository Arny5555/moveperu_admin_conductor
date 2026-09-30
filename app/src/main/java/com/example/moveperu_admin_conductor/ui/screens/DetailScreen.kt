package com.example.moveperu_admin_conductor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moveperu_admin_conductor.model.DatosDemo
import com.example.moveperu_admin_conductor.model.EstadoViaje
import com.example.moveperu_admin_conductor.ui.components.InsigniaEstadoViaje
import com.example.moveperu_admin_conductor.ui.components.LineaRutaViaje
import com.example.moveperu_admin_conductor.viewmodel.ViajeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viajeId: Int,
    viajeViewModel: ViajeViewModel,
    onVolver: () -> Unit,
    onEditarViaje: (Int) -> Unit
) {
    val state by viajeViewModel.uiState.collectAsState()

    LaunchedEffect(viajeId) {
        viajeViewModel.seleccionarViaje(viajeId)
    }

    val viaje = state.viajeSeleccionado

    var mostrarModalAsignar by remember { mutableStateOf(false) }
    var mostrarModalEliminar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Viaje MP-$viajeId", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (viaje != null) {
                        IconButton(onClick = { onEditarViaje(viaje.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Viaje", tint = Color.White)
                        }
                        IconButton(onClick = { mostrarModalEliminar = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (viaje == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Cargando información del viaje...")
            }
            return@Scaffold
        }

        val fechaStr = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.forLanguageTag("es-PE")).format(Date(viaje.fechaMillis))
        val estadoObj = EstadoViaje.fromString(viaje.estado)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(18.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(fechaStr, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        InsigniaEstadoViaje(viaje.estado)
                    }

                    Spacer(Modifier.height(16.dp))
                    LineaRutaViaje(origen = viaje.origen, destino = viaje.destino)
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(14.dp))

                    FilaDatoDetalle("Pasajero", viaje.pasajeroNombre)
                    FilaDatoDetalle("Tipo de Servicio", viaje.tipoServicio)
                    FilaDatoDetalle("Método de Pago", viaje.metodoPago)
                    FilaDatoDetalle("Distancia Estimada", "${viaje.distanciaKm} km")
                    FilaDatoDetalle("Duración Estimada", "${viaje.duracionMin} min")
                    FilaDatoDetalle("Tarifa Total", "S/ %.2f".format(Locale.US, viaje.tarifa))
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Información del Conductor", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))

                    FilaDatoDetalle("Nombre", viaje.conductorNombre)
                    FilaDatoDetalle("Vehículo", viaje.conductorVehiculo)
                    FilaDatoDetalle("Placa", viaje.conductorPlaca)
                    FilaDatoDetalle("Celular", viaje.conductorCelular)

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { mostrarModalAsignar = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Reasignar / Cambiar Conductor")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Control de Estado
            Text("Gestión de Estado del Viaje", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            if (estadoObj.siguiente != null) {
                Button(
                    onClick = { viajeViewModel.avanzarEstado(viaje) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Avanzar Estado a '${estadoObj.siguiente?.etiqueta}'", fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { viajeViewModel.cancelarViaje(viaje.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cancelar Viaje")
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Viaje ${viaje.estado}", fontWeight = FontWeight.Bold)
                        Text(
                            "Este viaje ya finalizó o fue cancelado. No admite más transiciones de estado.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (mostrarModalAsignar && viaje != null) {
        ModalAsignarConductor(
            onDismiss = { mostrarModalAsignar = false },
            onConfirmar = { nombre, vehiculo, placa, celular ->
                viajeViewModel.asignarConductor(viaje.id, nombre, vehiculo, placa, celular)
                mostrarModalAsignar = false
            }
        )
    }

    if (mostrarModalEliminar && viaje != null) {
        AlertDialog(
            onDismissRequest = { mostrarModalEliminar = false },
            title = { Text("¿Eliminar viaje MP-${viaje.id}?") },
            text = { Text("Esta acción eliminará permanentemente el registro del viaje de la base de datos.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viajeViewModel.eliminarViaje(viaje)
                        mostrarModalEliminar = false
                        onVolver()
                    }
                ) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarModalEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FilaDatoDetalle(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ModalAsignarConductor(
    onDismiss: () -> Unit,
    onConfirmar: (String, String, String, String) -> Unit
) {
    var nombre by remember { mutableStateOf(DatosDemo.conductoresDemo.first().nombre) }
    var vehiculo by remember { mutableStateOf(DatosDemo.conductoresDemo.first().vehiculo) }
    var placa by remember { mutableStateOf(DatosDemo.conductoresDemo.first().placa) }
    var celular by remember { mutableStateOf(DatosDemo.conductoresDemo.first().celular) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Asignar Conductor al Viaje") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Elige de la lista o edita los datos:", style = MaterialTheme.typography.labelMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    DatosDemo.conductoresDemo.take(3).forEach { c ->
                        TextButton(
                            onClick = {
                                nombre = c.nombre
                                vehiculo = c.vehiculo
                                placa = c.placa
                                celular = c.celular
                            }
                        ) {
                            Text(c.nombre.split(" ").first(), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Conductor") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = vehiculo,
                    onValueChange = { vehiculo = it },
                    label = { Text("Vehículo") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = placa,
                    onValueChange = { placa = it },
                    label = { Text("Placa") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = celular,
                    onValueChange = { celular = it },
                    label = { Text("Celular") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(nombre, vehiculo, placa, celular) },
                enabled = nombre.isNotBlank()
            ) { Text("Guardar Asignación") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
