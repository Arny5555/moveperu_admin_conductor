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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.moveperu_admin_conductor.model.RolUsuario
import com.example.moveperu_admin_conductor.ui.components.CampoTextoForm
import com.example.moveperu_admin_conductor.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    authViewModel: AuthViewModel,
    onRegistroExitoso: (RolUsuario) -> Unit,
    onVolver: () -> Unit
) {
    val state by authViewModel.state.collectAsState()

    LaunchedEffect(state.exitoRegistro) {
        if (state.exitoRegistro) {
            onRegistroExitoso(state.rolActual)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Personal y Conductor") },
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
            Text(
                text = "Selecciona el Rol a Registrar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(RolUsuario.CONDUCTOR, RolUsuario.ADMINISTRADOR, RolUsuario.EMPLEADO).forEach { rol ->
                    FilterChip(
                        selected = state.rolSeleccionado == rol,
                        onClick = { authViewModel.onRolChange(rol) },
                        label = { Text(rol.etiqueta) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            CampoTextoForm(
                valor = state.nombre,
                onValorChange = authViewModel::onNombreChange,
                etiqueta = "Nombre completo",
                placeholder = "Carlos Mamani",
                icono = Icons.Default.Person,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(12.dp))

            CampoTextoForm(
                valor = state.correo,
                onValorChange = authViewModel::onCorreoChange,
                etiqueta = "Correo electrónico",
                placeholder = "conductor@moveperu.pe",
                icono = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(Modifier.height(12.dp))

            CampoTextoForm(
                valor = state.celular,
                onValorChange = authViewModel::onCelularChange,
                etiqueta = "Celular",
                placeholder = "+51 987 654 321",
                icono = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(Modifier.height(12.dp))

            CampoTextoForm(
                valor = state.clave,
                onValorChange = authViewModel::onClaveChange,
                etiqueta = "Contraseña (mínimo 6 caracteres)",
                icono = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (state.rolSeleccionado == RolUsuario.CONDUCTOR) ImeAction.Next else ImeAction.Done
                )
            )

            if (state.rolSeleccionado == RolUsuario.CONDUCTOR) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Información del Vehículo",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))

                CampoTextoForm(
                    valor = state.vehiculo,
                    onValorChange = authViewModel::onVehiculoChange,
                    etiqueta = "Modelo de Vehículo",
                    placeholder = "Toyota Yaris 2024",
                    icono = Icons.Default.DirectionsCar,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(Modifier.height(12.dp))

                CampoTextoForm(
                    valor = state.placa,
                    onValorChange = authViewModel::onPlacaChange,
                    etiqueta = "Placa del Vehículo",
                    placeholder = "A2B-431",
                    icono = Icons.Default.DirectionsCar,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }

            state.error?.let { error ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = authViewModel::registrar,
                enabled = !state.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (state.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Registrar Cuenta de ${state.rolSeleccionado.etiqueta}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        text = "Nota importante:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "El registro guardará el usuario en la tabla 'usuarios' de Room. Se podrá iniciar sesión inmediatamente.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
