package com.example.moveperu_admin_conductor.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.moveperu_admin_conductor.model.RolUsuario
import com.example.moveperu_admin_conductor.ui.theme.AguaClara
import com.example.moveperu_admin_conductor.viewmodel.AuthViewModel
import com.example.moveperu_admin_conductor.viewmodel.ViajeViewModel
import java.util.Locale

@Composable
fun PerfilScreen(
    authViewModel: AuthViewModel,
    viajeViewModel: ViajeViewModel,
    onCerrarSesion: () -> Unit
) {
    val authState by authViewModel.state.collectAsState()
    val viajeState by viajeViewModel.uiState.collectAsState()
    val usuario = authState.usuarioActual
    val rol = authState.rolActual

    var confirmarSalida by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 28.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = (usuario?.nombre ?: "MP").take(2).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = usuario?.nombre ?: "Personal MovePerú",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = usuario?.correo ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AguaClara
                )

                Spacer(Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = rol.etiqueta,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Column(Modifier.padding(16.dp)) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Detalles de la Cuenta", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(10.dp))

                        FilaPerfil("ID de Usuario", "USR-${usuario?.id ?: 0}")
                        FilaPerfil("Nombre", usuario?.nombre ?: "-")
                        FilaPerfil("Correo", usuario?.correo ?: "-")
                        FilaPerfil("Celular", usuario?.celular?.ifEmpty { "+51 987 654 321" } ?: "+51 987 654 321")
                        FilaPerfil("Rol en Sistema", rol.etiqueta)
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (rol == RolUsuario.CONDUCTOR) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Información de la Unidad", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(10.dp))

                            FilaPerfil("Vehículo", "Toyota Yaris 2024")
                            FilaPerfil("Placa", "A2B-431")
                            FilaPerfil("Calificación Promedio", "4.9 ★")
                            FilaPerfil("Estado de Unidad", "Operativo & Habilitado")
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Estadísticas del Sistema Room", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(10.dp))

                        FilaPerfil("Base de Datos", "moveperu_database")
                        FilaPerfil("Viajes Registrados", "${viajeState.resumen.totalViajes}")
                        FilaPerfil("Ingresos Registrados", "S/ %.2f".format(Locale.US, viajeState.resumen.totalIngresos))
                        FilaPerfil("Usuarios Registrados", "${viajeState.listaUsuarios.size}")
                    }
                }

                Spacer(Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { confirmarSalida = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("¿Cerrar Sesión?") },
            text = { Text("Regresarás a la pantalla de inicio de sesión.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarSalida = false
                        authViewModel.cerrarSesion()
                        onCerrarSesion()
                    }
                ) { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FilaPerfil(etiqueta: String, valor: String) {
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
