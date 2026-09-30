package com.example.moveperu_admin_conductor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calificaciones")
data class CalificacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val viajeId: Int,
    val estrellas: Int,
    val comentario: String = "",
    val propina: Double = 0.0
)
