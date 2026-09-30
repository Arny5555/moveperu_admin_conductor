package com.example.moveperu_admin_conductor.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Usuario::class,
        ViajeEntity::class,
        CalificacionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun viajeDao(): ViajeDao
    abstract fun calificacionDao(): CalificacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDB(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "moveperu_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = INSTANCE ?: return@launch
                                val usuarioDao = database.usuarioDao()
                                usuarioDao.registrar(Usuario(nombre = "Carlos Mamani", correo = "carlos.mamani@moveperu.pe", clave = "moveperu2026", celular = "+51 987 654 321", rol = "CONDUCTOR"))
                                usuarioDao.registrar(Usuario(nombre = "Administrador Pro", correo = "admin@moveperu.pe", clave = "admin2026", celular = "+51 999 888 777", rol = "ADMINISTRADOR"))
                                usuarioDao.registrar(Usuario(nombre = "Atención al Cliente", correo = "atencion@moveperu.pe", clave = "atencion2026", celular = "+51 955 444 333", rol = "EMPLEADO"))

                                val viajeDao = database.viajeDao()
                                val now = System.currentTimeMillis()
                                viajeDao.insertar(
                                    ViajeEntity(
                                        pasajeroNombre = "Lucía Mendoza",
                                        origen = "Av. Larco 456, Miraflores",
                                        destino = "Aeropuerto Jorge Chávez",
                                        tipoServicio = "Confort",
                                        metodoPago = "Yape",
                                        tarifa = 45.00,
                                        distanciaKm = 18.5,
                                        duracionMin = 35,
                                        estado = "Solicitado",
                                        conductorNombre = "Carlos Mamani",
                                        conductorVehiculo = "Toyota Yaris",
                                        conductorPlaca = "A2B-431",
                                        conductorCelular = "+51 987 654 321",
                                        fechaMillis = now - 3600000
                                    )
                                )
                                viajeDao.insertar(
                                    ViajeEntity(
                                        pasajeroNombre = "Roberto Gómez",
                                        origen = "Jirón de la Unión 234, Lima",
                                        destino = "Jockey Plaza, Surco",
                                        tipoServicio = "Económico",
                                        metodoPago = "Efectivo",
                                        tarifa = 22.50,
                                        distanciaKm = 9.2,
                                        duracionMin = 20,
                                        estado = "En camino",
                                        conductorNombre = "Carlos Mamani",
                                        conductorVehiculo = "Toyota Yaris",
                                        conductorPlaca = "A2B-431",
                                        conductorCelular = "+51 987 654 321",
                                        fechaMillis = now - 7200000
                                    )
                                )
                                viajeDao.insertar(
                                    ViajeEntity(
                                        pasajeroNombre = "Sofía Vargas",
                                        origen = "Óvalo Gutierrez, Miraflores",
                                        destino = "San Isidro Financiero",
                                        tipoServicio = "Van",
                                        metodoPago = "Tarjeta",
                                        tarifa = 38.00,
                                        distanciaKm = 7.4,
                                        duracionMin = 18,
                                        estado = "Finalizado",
                                        conductorNombre = "Rosa Quispe",
                                        conductorVehiculo = "Kia Rio",
                                        conductorPlaca = "B7K-902",
                                        conductorCelular = "+51 954 118 220",
                                        fechaMillis = now - 14400000,
                                        propina = 5.00
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
