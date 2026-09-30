package com.example.moveperu_admin_conductor.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moveperu_admin_conductor.model.EstimarTarifaUseCase
import com.example.moveperu_admin_conductor.model.MetodoPago
import com.example.moveperu_admin_conductor.model.TipoServicio
import com.example.moveperu_admin_conductor.ui.components.CampoTextoForm
import com.example.moveperu_admin_conductor.viewmodel.ViajeViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreacionViajeScreen(
    viajeViewModel: ViajeViewModel,
    onVolver: () -> Unit,
    onCreadoExitosamente: () -> Unit
) {
    var pasajeroNombre by remember { mutableStateOf("") }
    var origen by remember { mutableStateOf("Av. Javier Prado 1200, San Isidro") }
    var destino by remember { mutableStateOf("Aeropuerto Jorge Chávez, Callao") }
    var tipoServicio by remember { mutableStateOf(TipoServicio.CONFORT) }
    var metodoPago by remember { mutableStateOf(MetodoPago.YAPE) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    val estimador = remember { EstimarTarifaUseCase() }
    val tarifaEstimada = remember(origen, destino, tipoServicio) {
        estimador(origen = origen, destino = destino, tipo = tipoServicio)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Solicitud de Viaje") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Información del Pasajero y Ruta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            CampoTextoForm(
                valor = pasajeroNombre,
                onValorChange = { pasajeroNombre = it; errorMensaje = null },
                etiqueta = "Nombre del Pasajero",
                placeholder = "Ej. Mariana Solís",
                icono = Icons.Default.Person
            )

            Spacer(Modifier.height(12.dp))

            CampoTextoForm(
                valor = origen,
                onValorChange = { origen = it },
                etiqueta = "Punto de Origen (Recojo)",
                placeholder = "Dirección de recojo",
                icono = Icons.Default.LocationOn
            )

            Spacer(Modifier.height(12.dp))

            CampoTextoForm(
                valor = destino,
                onValorChange = { destino = it },
                etiqueta = "Punto de Destino",
                placeholder = "Dirección de destino",
                icono = Icons.Default.LocationOn
            )

            Spacer(Modifier.height(20.dp))

            Text("Seleccionar Tipo de Servicio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TipoServicio.entries.forEach { tipo ->
                    val seleccionado = tipoServicio == tipo
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { tipoServicio = tipo },
                        shape = RoundedCornerShape(10.dp),
                        color = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            width = if (seleccionado) 2.dp else 1.dp,
                            color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = tipo.etiqueta,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (seleccionado) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = tipo.capacidad,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Método de Pago", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetodoPago.entries.forEach { pago ->
                    val seleccionado = metodoPago == pago
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { metodoPago = pago },
                        shape = RoundedCornerShape(10.dp),
                        color = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            width = if (seleccionado) 2.dp else 1.dp,
                            color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = pago.etiqueta,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (seleccionado) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Tarjeta de Estimación
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Estimación Automática de Tarifa", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Distancia calculada:", style = MaterialTheme.typography.bodySmall)
                        Text("${tarifaEstimada.distanciaKm} km", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Duración estimada:", style = MaterialTheme.typography.bodySmall)
                        Text("${tarifaEstimada.duracionMin} min", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tarifa Final Total:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "S/ %.2f".format(Locale.US, tarifaEstimada.total),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            errorMensaje?.let { error ->
                Spacer(Modifier.height(12.dp))
                Text(error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (pasajeroNombre.isBlank()) {
                        errorMensaje = "Por favor ingresa el nombre del pasajero"
                        return@Button
                    }
                    if (origen.isBlank() || destino.isBlank()) {
                        errorMensaje = "Origen y destino son obligatorios"
                        return@Button
                    }

                    viajeViewModel.crearViaje(
                        pasajeroNombre = pasajeroNombre.trim(),
                        origen = origen.trim(),
                        destino = destino.trim(),
                        tipoServicio = tipoServicio,
                        metodoPago = metodoPago.etiqueta,
                        tarifa = tarifaEstimada.total,
                        distanciaKm = tarifaEstimada.distanciaKm,
                        duracionMin = tarifaEstimada.duracionMin
                    )
                    onCreadoExitosamente()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Emitir y Guardar Solicitud de Viaje", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
