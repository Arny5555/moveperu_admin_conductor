package com.example.moveperu_admin_conductor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "viajes")
data class ViajeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pasajeroNombre: String,
    val origen: String,
    val destino: String,
    val tipoServicio: String,
    val distanciaKm: Double,
    val tarifa: Double,
    val estado: String,
    val fechaMillis: Long = System.currentTimeMillis(),
    val conductorNombre: String = "Carlos Mamani",
    val conductorVehiculo: String = "Toyota Yaris",
    val conductorPlaca: String = "A2B-431",
    val conductorCelular: String = "+51 987 654 321",
    val metodoPago: String = "Yape",
    val duracionMin: Int = 15,
    val descuento: Double = 0.0,
    val estrellas: Int = 0,
    val comentario: String = "",
    val propina: Double = 0.0
)
