package com.example.moveperu_admin_conductor.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ViajeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(viaje: ViajeEntity): Long

    @Update
    suspend fun actualizar(viaje: ViajeEntity): Int

    @Delete
    suspend fun eliminar(viaje: ViajeEntity): Int

    @Query("SELECT * FROM viajes WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): ViajeEntity?

    @Query("SELECT * FROM viajes ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ViajeEntity>>

    @Query("SELECT * FROM viajes WHERE conductorNombre = :conductorNombre ORDER BY id DESC")
    fun obtenerPorConductor(conductorNombre: String): Flow<List<ViajeEntity>>

    @Query("UPDATE viajes SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String): Int

    @Query("UPDATE viajes SET conductorNombre = :conductorNombre, conductorVehiculo = :conductorVehiculo, conductorPlaca = :conductorPlaca, conductorCelular = :conductorCelular, estado = :estado WHERE id = :id")
    suspend fun asignarConductor(
        id: Int,
        conductorNombre: String,
        conductorVehiculo: String,
        conductorPlaca: String,
        conductorCelular: String,
        estado: String = "ACEPTADO"
    ): Int

    @Query("SELECT COUNT(*) FROM viajes")
    suspend fun contarViajes(): Int
}
