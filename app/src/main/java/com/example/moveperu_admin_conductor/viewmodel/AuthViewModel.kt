package com.example.moveperu_admin_conductor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moveperu_admin_conductor.data.local.Usuario
import com.example.moveperu_admin_conductor.data.local.UsuarioDao
import com.example.moveperu_admin_conductor.model.RolUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val usuarioActual: Usuario? = null,
    val rolSeleccionado: RolUsuario = RolUsuario.CONDUCTOR,
    val correo: String = "",
    val clave: String = "",
    val nombre: String = "",
    val celular: String = "",
    val vehiculo: String = "",
    val placa: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val exitoRegistro: Boolean = false
) {
    val estaAutenticado: Boolean get() = usuarioActual != null
    val rolActual: RolUsuario get() = usuarioActual?.let { RolUsuario.fromString(it.rol) } ?: rolSeleccionado
}

class AuthViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onRolChange(rol: RolUsuario) {
        _state.update { it.copy(rolSeleccionado = rol, error = null) }
    }

    fun onCorreoChange(valor: String) {
        _state.update { it.copy(correo = valor, error = null) }
    }

    fun onClaveChange(valor: String) {
        _state.update { it.copy(clave = valor, error = null) }
    }

    fun onNombreChange(valor: String) {
        _state.update { it.copy(nombre = valor, error = null) }
    }

    fun onCelularChange(valor: String) {
        _state.update { it.copy(celular = valor, error = null) }
    }

    fun onVehiculoChange(valor: String) {
        _state.update { it.copy(vehiculo = valor, error = null) }
    }

    fun onPlacaChange(valor: String) {
        _state.update { it.copy(placa = valor, error = null) }
    }

    fun cargarDemo(rol: RolUsuario) {
        when (rol) {
            RolUsuario.CONDUCTOR -> {
                _state.update {
                    it.copy(
                        rolSeleccionado = RolUsuario.CONDUCTOR,
                        correo = "carlos.mamani@moveperu.pe",
                        clave = "moveperu2026",
                        error = null
                    )
                }
            }
            RolUsuario.ADMINISTRADOR -> {
                _state.update {
                    it.copy(
                        rolSeleccionado = RolUsuario.ADMINISTRADOR,
                        correo = "admin@moveperu.pe",
                        clave = "admin2026",
                        error = null
                    )
                }
            }
            RolUsuario.EMPLEADO -> {
                _state.update {
                    it.copy(
                        rolSeleccionado = RolUsuario.EMPLEADO,
                        correo = "atencion@moveperu.pe",
                        clave = "atencion2026",
                        error = null
                    )
                }
            }
            else -> {}
        }
    }

    fun login() {
        val correo = _state.value.correo.trim()
        val clave = _state.value.clave.trim()

        if (correo.isBlank() || clave.isBlank()) {
            _state.update { it.copy(error = "Por favor ingresa tu correo y contraseña") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }
            val usuario = usuarioDao.login(correo, clave)
            if (usuario != null) {
                _state.update {
                    it.copy(
                        usuarioActual = usuario,
                        cargando = false,
                        error = null
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        cargando = false,
                        error = "Credenciales incorrectas para el rol seleccionado"
                    )
                }
            }
        }
    }

    fun registrar() {
        val nombre = _state.value.nombre.trim()
        val correo = _state.value.correo.trim()
        val clave = _state.value.clave.trim()
        val celular = _state.value.celular.trim()
        val rol = _state.value.rolSeleccionado

        if (nombre.isBlank() || correo.isBlank() || clave.isBlank()) {
            _state.update { it.copy(error = "Nombre, correo y contraseña son obligatorios") }
            return
        }

        if (clave.length < 6) {
            _state.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }
            val nuevo = Usuario(
                nombre = nombre,
                correo = correo,
                clave = clave,
                celular = celular,
                rol = rol.name
            )
            val id = usuarioDao.registrar(nuevo)
            val usuarioCreado = nuevo.copy(id = id.toInt())
            _state.update {
                it.copy(
                    usuarioActual = usuarioCreado,
                    cargando = false,
                    exitoRegistro = true
                )
            }
        }
    }

    fun cerrarSesion() {
        _state.value = AuthUiState()
    }

    companion object {
        fun factory(usuarioDao: UsuarioDao) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(usuarioDao) as T
            }
        }
    }
}
