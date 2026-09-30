package com.example.moveperu_admin_conductor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.moveperu_admin_conductor.model.EstimarTarifaUseCase
import com.example.moveperu_admin_conductor.model.MetodoPago
import com.example.moveperu_admin_conductor.model.TipoServicio
import com.example.moveperu_admin_conductor.ui.components.CampoTextoForm
import com.example.moveperu_admin_conductor.viewmodel.ViajeViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    viajeId: Int?,
    viajeViewModel: ViajeViewModel,
    onVolver: () -> Unit
) {
    val state by viajeViewModel.uiState.collectAsState()

    var pasajeroNombre by remember { mutableStateOf("Diana Flores") }
    var origen by remember { mutableStateOf("Av. Larco 1301, Miraflores") }
    var destino by remember { mutableStateOf("Aeropuerto Jorge Chávez, Callao") }
    var tipoServicio by remember { mutableStateOf(TipoServicio.ECONOMICO) }
    var metodoPago by remember { mutableStateOf(MetodoPago.YAPE) }
    var tarifaEstimada by remember { mutableDoubleStateOf(32.50) }
    var distanciaKm by remember { mutableDoubleStateOf(18.5) }
    var duracionMin by remember { mutableIntStateOf(35) }

    val estimador = remember { EstimarTarifaUseCase() }

    fun recalcular() {
        if (origen.isNotBlank() && destino.isNotBlank()) {
            val calc = estimador(origen, destino, tipoServicio)
            tarifaEstimada = calc.total
            distanciaKm = calc.distanciaKm
            duracionMin = calc.duracionMin
        }
    }

    LaunchedEffect(viajeId) {
        if (viajeId != null) {
            viajeViewModel.seleccionarViaje(viajeId)
        }
    }

    val viajeEditar = state.viajeSeleccionado
    LaunchedEffect(viajeEditar) {
        if (viajeEditar != null && viajeId != null) {
            pasajeroNombre = viajeEditar.pasajeroNombre
            origen = viajeEditar.origen
            destino = viajeEditar.destino
            tipoServicio = TipoServicio.fromString(viajeEditar.tipoServicio)
            metodoPago = MetodoPago.fromString(viajeEditar.metodoPago)
            tarifaEstimada = viajeEditar.tarifa
            distanciaKm = viajeEditar.distanciaKm
            duracionMin = viajeEditar.duracionMin
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viajeId == null) "Crear Registro de Viaje" else "Editar Viaje MP-$viajeId",
                        fontWeight = FontWeight.Bold
                    )
                },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(20.dp)
        ) {
            CampoTextoForm(
                valor = pasajeroNombre,
                onValorChange = { pasajeroNombre = it },
                etiqueta = "Nombre del Pasajero",
                placeholder = "Ejemplo: Arny Saravia",
                icono = Icons.Default.Person,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(12.dp))

            CampoTextoForm(
                valor = origen,
                onValorChange = {
                    origen = it
                    recalcular()
                },
                etiqueta = "Punto de Origen",
                placeholder = "Av. Javier Prado Este 2050, San Isidro",
                icono = Icons.Default.LocationOn,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(12.dp))

            CampoTextoForm(
                valor = destino,
                onValorChange = {
                    destino = it
                    recalcular()
                },
                etiqueta = "Punto de Destino",
                placeholder = "Jockey Plaza, Surco",
                icono = Icons.Default.Place,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(Modifier.height(16.dp))

            Text("Tipo de Servicio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoServicio.entries.forEach { tipo ->
                    FilterChip(
                        selected = tipoServicio == tipo,
                        onClick = {
                            tipoServicio = tipo
                            recalcular()
                        },
                        label = { Text(tipo.etiqueta) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Método de Pago", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetodoPago.entries.forEach { metodo ->
                    FilterChip(
                        selected = metodoPago == metodo,
                        onClick = { metodoPago = metodo },
                        label = { Text(metodo.etiqueta) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Cálculo Automático de Tarifa", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Distancia Estimada: $distanciaKm km", style = MaterialTheme.typography.bodyMedium)
                    Text("Tiempo Estimado: $duracionMin minutos", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Tarifa Calculada: S/ %.2f".format(Locale.US, tarifaEstimada),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (viajeEditar == null || viajeId == null) {
                        viajeViewModel.crearViaje(
                            pasajeroNombre = pasajeroNombre,
                            origen = origen,
                            destino = destino,
                            tipoServicio = tipoServicio,
                            metodoPago = metodoPago.etiqueta,
                            tarifa = tarifaEstimada,
                            distanciaKm = distanciaKm,
                            duracionMin = duracionMin
                        )
                    } else {
                        val editado = viajeEditar.copy(
                            pasajeroNombre = pasajeroNombre,
                            origen = origen,
                            destino = destino,
                            tipoServicio = tipoServicio.etiqueta,
                            metodoPago = metodoPago.etiqueta,
                            tarifa = tarifaEstimada,
                            distanciaKm = distanciaKm,
                            duracionMin = duracionMin
                        )
                        viajeViewModel.actualizarViaje(editado)
                    }
                    onVolver()
                },
                enabled = pasajeroNombre.isNotBlank() && origen.isNotBlank() && destino.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (viajeId == null) "Guardar Nuevo Viaje" else "Actualizar Registro de Viaje",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
