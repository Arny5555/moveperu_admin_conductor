package com.example.moveperu_admin_conductor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.moveperu_admin_conductor.data.local.AppDatabase
import com.example.moveperu_admin_conductor.data.local.Usuario
import com.example.moveperu_admin_conductor.data.local.UsuarioDao
import com.example.moveperu_admin_conductor.data.local.ViajeDao
import com.example.moveperu_admin_conductor.data.local.ViajeEntity
import com.example.moveperu_admin_conductor.model.DatosDemo
import com.example.moveperu_admin_conductor.model.EstadoViaje
import com.example.moveperu_admin_conductor.model.EstimarTarifaUseCase
import com.example.moveperu_admin_conductor.model.RolUsuario
import com.example.moveperu_admin_conductor.model.TipoServicio
import com.example.moveperu_admin_conductor.ui.navigation.AppNavigation
import com.example.moveperu_admin_conductor.ui.theme.Moveperu_admin_conductorTheme
import com.example.moveperu_admin_conductor.viewmodel.AuthViewModel
import com.example.moveperu_admin_conductor.viewmodel.ViajeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDB(applicationContext)
        val usuarioDao = db.usuarioDao()
        val viajeDao = db.viajeDao()

        lifecycleScope.launch(Dispatchers.IO) {
            poblarBaseDeDatosInicial(usuarioDao, viajeDao)
        }

        val authViewModel = ViewModelProvider(
            this,
            AuthViewModel.factory(usuarioDao)
        )[AuthViewModel::class.java]

        val viajeViewModel = ViewModelProvider(
            this,
            ViajeViewModel.factory(viajeDao, usuarioDao)
        )[ViajeViewModel::class.java]

        setContent {
            Moveperu_admin_conductorTheme {
                AppNavigation(
                    authViewModel = authViewModel,
                    viajeViewModel = viajeViewModel
                )
            }
        }
    }

    private suspend fun poblarBaseDeDatosInicial(
        usuarioDao: UsuarioDao,
        viajeDao: ViajeDao
    ) {
        if (usuarioDao.contarUsuarios() == 0) {
            usuarioDao.registrar(
                Usuario(
                    nombre = "Carlos Mamani",
                    correo = "carlos.mamani@moveperu.pe",
                    clave = "moveperu2026",
                    celular = "+51 987 654 321",
                    rol = RolUsuario.CONDUCTOR.name
                )
            )
            usuarioDao.registrar(
                Usuario(
                    nombre = "Administrador MovePerú",
                    correo = "admin@moveperu.pe",
                    clave = "admin2026",
                    celular = "+51 999 888 777",
                    rol = RolUsuario.ADMINISTRADOR.name
                )
            )
            usuarioDao.registrar(
                Usuario(
                    nombre = "Atención al Cliente",
                    correo = "atencion@moveperu.pe",
                    clave = "atencion2026",
                    celular = "+51 955 444 333",
                    rol = RolUsuario.EMPLEADO.name
                )
            )
        }

        if (viajeDao.contarViajes() == 0) {
            val estimador = EstimarTarifaUseCase()
            val origenes = listOf(
                "Av. Larco 1301, Miraflores",
                "Av. Camino Real 1075, San Isidro",
                "Av. Javier Prado Este 2050, San Isidro",
                "Jr. de la Unión 700, Cercado de Lima",
                "Av. Primavera 1352, Santiago de Surco",
                "Av. La Marina 2355, San Miguel"
            )
            val destinos = listOf(
                "Aeropuerto Jorge Chávez, Callao",
                "Jockey Plaza, Santiago de Surco",
                "Parque Kennedy, Miraflores",
                "Plaza San Miguel, San Miguel",
                "Real Plaza Salaverry, Jesús María"
            )
            val pasajeros = listOf(
                "Arny Saravia", "Diana Flores", "Kevin Ríos", "Patricia Vega",
                "Renzo Alarcón", "Lucía Bravo", "Marco Tello", "Gabriela Nieto"
            )

            // 33 viajes iniciales
            val listaInicial = List(33) { i ->
                val origen = origenes[i % origenes.size]
                val destino = destinos[(i * 2) % destinos.size]
                val tipo = TipoServicio.entries[i % TipoServicio.entries.size]
                val calc = estimador(origen, destino, tipo)
                val conductor = DatosDemo.conductoresDemo[i % DatosDemo.conductoresDemo.size]

                val estado = when {
                    i == 0 -> EstadoViaje.SOLICITADO
                    i == 1 -> EstadoViaje.ACEPTADO
                    i == 2 -> EstadoViaje.EN_CAMINO
                    i == 3 -> EstadoViaje.INICIADO
                    i % 8 == 0 -> EstadoViaje.CANCELADO
                    else -> EstadoViaje.FINALIZADO
                }

                val cal = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -(i / 2))
                }

                ViajeEntity(
                    pasajeroNombre = pasajeros[i % pasajeros.size],
                    origen = origen,
                    destino = destino,
                    tipoServicio = tipo.etiqueta,
                    distanciaKm = calc.distanciaKm,
                    duracionMin = calc.duracionMin,
                    tarifa = calc.total,
                    estado = estado.etiqueta,
                    conductorNombre = conductor.nombre,
                    conductorVehiculo = conductor.vehiculo,
                    conductorPlaca = conductor.placa,
                    conductorCelular = conductor.celular,
                    metodoPago = if (i % 2 == 0) "Yape" else "Efectivo",
                    fechaMillis = cal.timeInMillis,
                    estrellas = if (estado == EstadoViaje.FINALIZADO) 4 + (i % 2) else 0
                )
            }

            listaInicial.forEach { viaje ->
                viajeDao.insertar(viaje)
            }
        }
    }
}
