package com.example.moveperu_admin_conductor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.moveperu_admin_conductor.data.local.ViajeEntity
import com.example.moveperu_admin_conductor.model.EstadoViaje
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InsigniaEstadoViaje(estadoStr: String) {
    val estado = EstadoViaje.fromString(estadoStr)
    val fondo = when (estado) {
        EstadoViaje.SOLICITADO -> MaterialTheme.colorScheme.secondaryContainer
        EstadoViaje.ACEPTADO, EstadoViaje.EN_CAMINO -> MaterialTheme.colorScheme.tertiaryContainer
        EstadoViaje.INICIADO -> MaterialTheme.colorScheme.primaryContainer
        EstadoViaje.FINALIZADO -> Color(0xFFE8F5E9)
        EstadoViaje.CANCELADO -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textoColor = when (estado) {
        EstadoViaje.SOLICITADO -> MaterialTheme.colorScheme.onSecondaryContainer
        EstadoViaje.ACEPTADO, EstadoViaje.EN_CAMINO -> MaterialTheme.colorScheme.onTertiaryContainer
        EstadoViaje.INICIADO -> MaterialTheme.colorScheme.onPrimaryContainer
        EstadoViaje.FINALIZADO -> Color(0xFF2E7D32)
        EstadoViaje.CANCELADO -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(999.dp),
        color = fondo
    ) {
        Text(
            text = estado.etiqueta,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textoColor
        )
    }
}

@Composable
fun LineaRutaViaje(origen: String, destino: String) {
    Row(verticalAlignment = Alignment.Top) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Box(Modifier.size(8.dp).background(MaterialTheme.colorScheme.tertiary, CircleShape))
            Box(Modifier.width(2.dp).height(20.dp).background(MaterialTheme.colorScheme.outlineVariant))
            Box(Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = origen,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = destino,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TarjetaViajeItem(
    viaje: ViajeEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAvanzarEstado: (() -> Unit)? = null,
    onAsignarConductor: (() -> Unit)? = null
) {
    val fecha = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-PE")).format(Date(viaje.fechaMillis))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MP-${viaje.id}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = fecha,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                InsigniaEstadoViaje(viaje.estado)
            }

            Spacer(Modifier.height(12.dp))
            LineaRutaViaje(origen = viaje.origen, destino = viaje.destino)
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Pasajero: ${viaje.pasajeroNombre}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Conductor: ${viaje.conductorNombre}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "S/ %.2f".format(Locale.US, viaje.tarifa),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = viaje.tipoServicio,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            val estado = EstadoViaje.fromString(viaje.estado)
            if (estado == EstadoViaje.SOLICITADO && onAsignarConductor != null) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onAsignarConductor,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Asignar Conductor")
                }
            } else if (estado.siguiente != null && onAvanzarEstado != null) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onAvanzarEstado,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Avanzar a ${estado.siguiente?.etiqueta}")
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
    }
}
