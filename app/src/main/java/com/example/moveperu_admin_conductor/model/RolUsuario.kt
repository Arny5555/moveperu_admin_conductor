package com.example.moveperu_admin_conductor.model

enum class RolUsuario(val etiqueta: String, val descripcion: String) {
    CONDUCTOR("Conductor", "Atiende y realiza los viajes asignados"),
    ADMINISTRADOR("Administrador", "Gestión total de usuarios, viajes y reportes"),
    EMPLEADO("Atención al Cliente", "Gestión de solicitudes y asignación de viajes"),
    CLIENTE("Cliente", "Pasajero final de la app MovePerú");

    companion object {
        fun fromString(value: String): RolUsuario = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true) || it.etiqueta.equals(value, ignoreCase = true)
        } ?: CONDUCTOR
    }
}
