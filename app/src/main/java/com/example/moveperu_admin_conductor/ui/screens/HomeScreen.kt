package com.example.moveperu_admin_conductor.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.moveperu_admin_conductor.data.local.ViajeEntity
import com.example.moveperu_admin_conductor.model.EstadoViaje
import com.example.moveperu_admin_conductor.model.RolUsuario
import com.example.moveperu_admin_conductor.ui.components.TarjetaViajeItem
import com.example.moveperu_admin_conductor.ui.theme.AguaClara
import com.example.moveperu_admin_conductor.viewmodel.AuthViewModel
import com.example.moveperu_admin_conductor.viewmodel.ViajeUiState
import com.example.moveperu_admin_conductor.viewmodel.ViajeViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    viajeViewModel: ViajeViewModel,
    onIrALista: () -> Unit,
    onIrACrearViaje: () -> Unit,
    onIrAGestionUsuarios: () -> Unit,
    onVerDetalleViaje: (Int) -> Unit
) {
    val authState by authViewModel.state.collectAsState()
    val viajeState by viajeViewModel.uiState.collectAsState()
    val usuario = authState.usuarioActual
    val rol = authState.rolActual

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderUsuarioHome(
                nombre = usuario?.nombre ?: "Usuario Pro",
                rolEtiqueta = rol.etiqueta,
                correo = usuario?.correo ?: ""
            )

            Column(Modifier.padding(16.dp)) {

                if (rol == RolUsuario.CONDUCTOR) {
                    HomeConductorContent(
                        nombreConductor = usuario?.nombre ?: "Carlos Mamani",
                        state = viajeState,
                        onAvanzarEstado = { viajeViewModel.avanzarEstado(it) },
                        onVerDetalle = onVerDetalleViaje,
                        onIrALista = onIrALista
                    )
                } else {
                    HomeAdminContent(
                        state = viajeState,
                        rol = rol,
                        onIrACrearViaje = onIrACrearViaje,
                        onIrALista = onIrALista,
                        onIrAGestionUsuarios = onIrAGestionUsuarios,
                        onVerDetalle = onVerDetalleViaje,
                        onAvanzarEstado = { viajeViewModel.avanzarEstado(it) }
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HeaderUsuarioHome(nombre: String, rolEtiqueta: String, correo: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nombre.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Bienvenido,",
                    style = MaterialTheme.typography.labelMedium,
                    color = AguaClara
                )
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "$rolEtiqueta • $correo",
                    style = MaterialTheme.typography.bodySmall,
                    color = AguaClara
                )
            }
        }
    }
}

@Composable
private fun HomeConductorContent(
    nombreConductor: String,
    state: ViajeUiState,
    onAvanzarEstado: (ViajeEntity) -> Unit,
    onVerDetalle: (Int) -> Unit,
    onIrALista: () -> Unit
) {
    var disponible by remember { mutableStateOf(true) }

    val misViajes = state.listaViajes.filter {
        it.conductorNombre.contains(nombreConductor, ignoreCase = true) ||
                nombreConductor.contains(it.conductorNombre, ignoreCase = true)
    }

    val activos = misViajes.filter {
        val est = EstadoViaje.fromString(it.estado)
        est.activo
    }

    val finalizadosHoy = misViajes.filter {
        it.estado.equals(EstadoViaje.FINALIZADO.name, ignoreCase = true) ||
                it.estado.equals(EstadoViaje.FINALIZADO.etiqueta, ignoreCase = true)
    }

    val gananciasHoy = finalizadosHoy.sumOf { it.tarifa + it.propina }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (disponible) "Estado: Disponible" else "Estado: Desconectado",
                    fontWeight = FontWeight.Bold,
                    color = if (disponible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (disponible) "Recibiendo viajes asignados por sistema" else "No se te asignarán viajes nuevos",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = disponible,
                onCheckedChange = { disponible = it }
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TarjetaMetrica(
            valor = "${activos.size}",
            etiqueta = "Viajes Activos",
            modifier = Modifier.weight(1f)
        )
        TarjetaMetrica(
            valor = "S/ %.2f".format(Locale.US, gananciasHoy),
            etiqueta = "Ganancias de Hoy",
            modifier = Modifier.weight(1f)
        )
        TarjetaMetrica(
            valor = "${finalizadosHoy.size}",
            etiqueta = "Completados",
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(Modifier.height(20.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Tus Viajes Asignados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        TextButton(onClick = onIrALista) { Text("Ver todos") }
    }

    Spacer(Modifier.height(8.dp))

    if (activos.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("No tienes viajes activos asignados en este momento", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Mantén tu estado 'Disponible'. Los administradores asignarán solicitudes entrantes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        activos.forEach { viaje ->
            TarjetaViajeItem(
                viaje = viaje,
                onClick = { onVerDetalle(viaje.id) },
                onAvanzarEstado = { onAvanzarEstado(viaje) }
            )
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun HomeAdminContent(
    state: ViajeUiState,
    rol: RolUsuario,
    onIrACrearViaje: () -> Unit,
    onIrALista: () -> Unit,
    onIrAGestionUsuarios: () -> Unit,
    onVerDetalle: (Int) -> Unit,
    onAvanzarEstado: (ViajeEntity) -> Unit
) {
    Text(
        text = "Resumen del Sistema MovePerú",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(10.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TarjetaMetrica(
            valor = "S/ %.2f".format(Locale.US, state.resumen.totalIngresos),
            etiqueta = "Ingresos Totales",
            modifier = Modifier.weight(1f)
        )
        TarjetaMetrica(
            valor = "${state.resumen.solicitadosPendientes}",
            etiqueta = "Por Asignar",
            modifier = Modifier.weight(1f)
        )
        TarjetaMetrica(
            valor = "${state.resumen.viajesActivos}",
            etiqueta = "En Curso",
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(Modifier.height(16.dp))

    Text(
        text = "Acciones Rápidas de Gestión",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onIrACrearViaje,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Nuevo Viaje")
        }

        OutlinedButton(
            onClick = onIrALista,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Ver Viajes")
        }
    }

    if (rol == RolUsuario.ADMINISTRADOR) {
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onIrAGestionUsuarios,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Gestionar Usuarios & Conductores")
        }
    }

    Spacer(Modifier.height(20.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Solicitudes de Viaje Pendientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        TextButton(onClick = onIrALista) { Text("Ver todos") }
    }

    Spacer(Modifier.height(8.dp))

    val pendientes = state.listaViajes.filter {
        it.estado.equals(EstadoViaje.SOLICITADO.name, ignoreCase = true) ||
                it.estado.equals(EstadoViaje.SOLICITADO.etiqueta, ignoreCase = true)
    }

    if (pendientes.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("No hay solicitudes pendientes de asignación", fontWeight = FontWeight.SemiBold)
                Text(
                    "Todas las solicitudes tienen un conductor asignado o no hay viajes nuevos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        pendientes.take(3).forEach { viaje ->
            TarjetaViajeItem(
                viaje = viaje,
                onClick = { onVerDetalle(viaje.id) },
                onAvanzarEstado = { onAvanzarEstado(viaje) },
                onAsignarConductor = { onVerDetalle(viaje.id) }
            )
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun TarjetaMetrica(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
