package com.example.moveperu_admin_conductor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moveperu_admin_conductor.data.local.Usuario
import com.example.moveperu_admin_conductor.data.local.UsuarioDao
import com.example.moveperu_admin_conductor.data.local.ViajeDao
import com.example.moveperu_admin_conductor.data.local.ViajeEntity
import com.example.moveperu_admin_conductor.model.EstadoViaje
import com.example.moveperu_admin_conductor.model.TipoServicio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FiltroViajes(
    val busqueda: String = "",
    val estadoFiltro: EstadoViaje? = null,
    val tipoServicioFiltro: TipoServicio? = null
)

data class ResumenNegocio(
    val totalIngresos: Double = 0.0,
    val totalViajes: Int = 0,
    val solicitadosPendientes: Int = 0,
    val viajesActivos: Int = 0,
    val viajesFinalizados: Int = 0,
    val viajesCancelados: Int = 0,
    val conductoresDisponibles: Int = 0
)

data class ViajeUiState(
    val listaViajes: List<ViajeEntity> = emptyList(),
    val listaUsuarios: List<Usuario> = emptyList(),
    val filtro: FiltroViajes = FiltroViajes(),
    val resumen: ResumenNegocio = ResumenNegocio(),
    val viajeSeleccionado: ViajeEntity? = null,
    val cargando: Boolean = false,
    val mensajeExito: String? = null
)

class ViajeViewModel(
    private val viajeDao: ViajeDao,
    private val usuarioDao: UsuarioDao
) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroViajes())
    val filtro: StateFlow<FiltroViajes> = _filtro.asStateFlow()

    private val _viajeSeleccionadoId = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<ViajeUiState> = combine(
        viajeDao.obtenerTodos(),
        usuarioDao.obtenerTodos(),
        _filtro,
        _viajeSeleccionadoId
    ) { todosViajes, usuarios, filtroActual, seleccionadoId ->

        val filtrados = todosViajes.filter { viaje ->
            val coincideBusqueda = filtroActual.busqueda.isBlank() ||
                    viaje.pasajeroNombre.contains(filtroActual.busqueda, ignoreCase = true) ||
                    viaje.conductorNombre.contains(filtroActual.busqueda, ignoreCase = true) ||
                    viaje.origen.contains(filtroActual.busqueda, ignoreCase = true) ||
                    viaje.destino.contains(filtroActual.busqueda, ignoreCase = true) ||
                    "MP-${viaje.id}".contains(filtroActual.busqueda, ignoreCase = true)

            val coincideEstado = filtroActual.estadoFiltro == null ||
                    viaje.estado.equals(filtroActual.estadoFiltro.name, ignoreCase = true) ||
                    viaje.estado.equals(filtroActual.estadoFiltro.etiqueta, ignoreCase = true)

            val coincideTipo = filtroActual.tipoServicioFiltro == null ||
                    viaje.tipoServicio.equals(filtroActual.tipoServicioFiltro.name, ignoreCase = true) ||
                    viaje.tipoServicio.equals(filtroActual.tipoServicioFiltro.etiqueta, ignoreCase = true)

            coincideBusqueda && coincideEstado && coincideTipo
        }

        val finalizados = todosViajes.filter {
            it.estado.equals(EstadoViaje.FINALIZADO.name, ignoreCase = true) ||
                    it.estado.equals(EstadoViaje.FINALIZADO.etiqueta, ignoreCase = true)
        }

        val solicitados = todosViajes.filter {
            it.estado.equals(EstadoViaje.SOLICITADO.name, ignoreCase = true) ||
                    it.estado.equals(EstadoViaje.SOLICITADO.etiqueta, ignoreCase = true)
        }

        val activos = todosViajes.filter {
            val estado = EstadoViaje.fromString(it.estado)
            estado.activo && estado != EstadoViaje.SOLICITADO
        }

        val cancelados = todosViajes.filter {
            it.estado.equals(EstadoViaje.CANCELADO.name, ignoreCase = true) ||
                    it.estado.equals(EstadoViaje.CANCELADO.etiqueta, ignoreCase = true)
        }

        val conductores = usuarios.filter { it.rol.equals("CONDUCTOR", ignoreCase = true) }

        val resumen = ResumenNegocio(
            totalIngresos = finalizados.sumOf { it.tarifa + it.propina },
            totalViajes = todosViajes.size,
            solicitadosPendientes = solicitados.size,
            viajesActivos = activos.size,
            viajesFinalizados = finalizados.size,
            viajesCancelados = cancelados.size,
            conductoresDisponibles = conductores.size
        )

        val seleccionado = seleccionadoId?.let { id -> todosViajes.firstOrNull { it.id == id } }

        ViajeUiState(
            listaViajes = filtrados,
            listaUsuarios = usuarios,
            filtro = filtroActual,
            resumen = resumen,
            viajeSeleccionado = seleccionado
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ViajeUiState())

    fun onBusquedaChange(texto: String) {
        _filtro.update { it.copy(busqueda = texto) }
    }

    fun onEstadoFiltroChange(estado: EstadoViaje?) {
        _filtro.update { it.copy(estadoFiltro = estado) }
    }

    fun onTipoServicioFiltroChange(tipo: TipoServicio?) {
        _filtro.update { it.copy(tipoServicioFiltro = tipo) }
    }

    fun seleccionarViaje(id: Int?) {
        _viajeSeleccionadoId.value = id
    }

    fun crearViaje(
        pasajeroNombre: String,
        origen: String,
        destino: String,
        tipoServicio: TipoServicio,
        metodoPago: String,
        tarifa: Double,
        distanciaKm: Double,
        duracionMin: Int,
        conductorNombre: String = "Carlos Mamani",
        conductorVehiculo: String = "Toyota Yaris",
        conductorPlaca: String = "A2B-431",
        conductorCelular: String = "+51 987 654 321"
    ) {
        viewModelScope.launch {
            val nuevo = ViajeEntity(
                pasajeroNombre = pasajeroNombre,
                origen = origen,
                destino = destino,
                tipoServicio = tipoServicio.etiqueta,
                metodoPago = metodoPago,
                tarifa = tarifa,
                distanciaKm = distanciaKm,
                duracionMin = duracionMin,
                estado = EstadoViaje.SOLICITADO.etiqueta,
                conductorNombre = conductorNombre,
                conductorVehiculo = conductorVehiculo,
                conductorPlaca = conductorPlaca,
                conductorCelular = conductorCelular,
                fechaMillis = System.currentTimeMillis()
            )
            viajeDao.insertar(nuevo)
        }
    }

    fun actualizarViaje(viaje: ViajeEntity) {
        viewModelScope.launch {
            viajeDao.actualizar(viaje)
        }
    }

    fun eliminarViaje(viaje: ViajeEntity) {
        viewModelScope.launch {
            viajeDao.eliminar(viaje)
        }
    }

    fun avanzarEstado(viaje: ViajeEntity) {
        val estadoActual = EstadoViaje.fromString(viaje.estado)
        val siguiente = estadoActual.siguiente ?: return
        viewModelScope.launch {
            viajeDao.actualizarEstado(viaje.id, siguiente.etiqueta)
        }
    }

    fun cancelarViaje(viajeId: Int) {
        viewModelScope.launch {
            viajeDao.actualizarEstado(viajeId, EstadoViaje.CANCELADO.etiqueta)
        }
    }

    fun asignarConductor(
        viajeId: Int,
        conductorNombre: String,
        conductorVehiculo: String,
        conductorPlaca: String,
        conductorCelular: String
    ) {
        viewModelScope.launch {
            viajeDao.asignarConductor(
                id = viajeId,
                conductorNombre = conductorNombre,
                conductorVehiculo = conductorVehiculo,
                conductorPlaca = conductorPlaca,
                conductorCelular = conductorCelular,
                estado = EstadoViaje.ACEPTADO.etiqueta
            )
        }
    }

    fun eliminarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            usuarioDao.eliminar(usuario)
        }
    }

    companion object {
        fun factory(viajeDao: ViajeDao, usuarioDao: UsuarioDao) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ViajeViewModel(viajeDao, usuarioDao) as T
            }
        }
    }
}
