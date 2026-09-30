package com.example.moveperu_admin_conductor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalificacionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun calificar(calificacion: CalificacionEntity): Long

    @Query("SELECT * FROM calificaciones ORDER BY id DESC")
    fun observarCalificaciones(): Flow<List<CalificacionEntity>>
}
