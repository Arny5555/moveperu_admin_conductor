package com.example.moveperu_admin_conductor.model

import java.util.Calendar
import kotlin.math.abs
import kotlin.math.roundToInt

enum class TipoServicio(
    val etiqueta: String,
    val tarifaBase: Double,
    val porKm: Double,
    val porMin: Double,
    val capacidad: String
) {
    ECONOMICO("Económico", 4.50, 1.10, 0.25, "4 pasajeros"),
    CONFORT("Confort", 6.50, 1.65, 0.35, "4 pasajeros"),
    VAN("Van", 9.00, 2.30, 0.45, "8 pasajeros");

    companion object {
        fun fromString(value: String): TipoServicio = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true) || it.etiqueta.equals(value, ignoreCase = true)
        } ?: ECONOMICO
    }
}

enum class MetodoPago(val etiqueta: String) {
    EFECTIVO("Efectivo"),
    YAPE("Yape"),
    TARJETA("Tarjeta");

    companion object {
        fun fromString(value: String): MetodoPago = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true) || it.etiqueta.equals(value, ignoreCase = true)
        } ?: YAPE
    }
}

enum class EstadoViaje(val etiqueta: String, val detalle: String) {
    SOLICITADO("Solicitado", "Esperando asignación de conductor"),
    ACEPTADO("Aceptado", "El conductor aceptó el servicio"),
    EN_CAMINO("En camino", "Conductor en ruta al punto de recojo"),
    INICIADO("Iniciado", "Viaje en curso hacia el destino"),
    FINALIZADO("Finalizado", "Viaje completado exitosamente"),
    CANCELADO("Cancelado", "El viaje fue cancelado");

    val siguiente: EstadoViaje?
        get() = when (this) {
            SOLICITADO -> ACEPTADO
            ACEPTADO -> EN_CAMINO
            EN_CAMINO -> INICIADO
            INICIADO -> FINALIZADO
            FINALIZADO, CANCELADO -> null
        }

    val activo: Boolean get() = this != FINALIZADO && this != CANCELADO

    companion object {
        val flujo = listOf(SOLICITADO, ACEPTADO, EN_CAMINO, INICIADO, FINALIZADO)

        fun fromString(value: String): EstadoViaje = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true) || it.etiqueta.equals(value, ignoreCase = true)
        } ?: SOLICITADO
    }
}

data class TarifaEstimada(
    val distanciaKm: Double,
    val duracionMin: Int,
    val recargo: Double,
    val subtotal: Double,
    val descuento: Double,
    val total: Double
)

data class ConductorInfo(
    val nombre: String,
    val vehiculo: String,
    val placa: String,
    val celular: String,
    val calificacion: Double = 4.8
)

class EstimarTarifaUseCase {
    operator fun invoke(
        origen: String,
        destino: String,
        tipo: TipoServicio,
        descuentoPorcentaje: Double = 0.0
    ): TarifaEstimada {
        val clave = origen.trim().lowercase() + ">" + destino.trim().lowercase()
        val semilla = abs(clave.hashCode())
        val distanciaKm = ((2.5 + (semilla % 215) / 10.0) * 10).roundToInt() / 10.0
        val duracionMin = (6 + distanciaKm * 2.4).roundToInt()
        val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val recargo = if (hora in 7..9 || hora in 18..21) 1.25 else 1.0
        val subtotal = (tipo.tarifaBase + distanciaKm * tipo.porKm + duracionMin * tipo.porMin) * recargo
        val descuento = subtotal * descuentoPorcentaje
        return TarifaEstimada(
            distanciaKm = distanciaKm,
            duracionMin = duracionMin,
            recargo = recargo,
            subtotal = (subtotal * 10).roundToInt() / 10.0,
            descuento = (descuento * 10).roundToInt() / 10.0,
            total = (((subtotal - descuento) * 10).roundToInt() / 10.0).coerceAtLeast(4.50)
        )
    }
}

object DatosDemo {
    val conductoresDemo = listOf(
        ConductorInfo("Carlos Mamani", "Toyota Yaris", "A2B-431", "+51 987 654 321", 4.9),
        ConductorInfo("Rosa Quispe", "Kia Rio", "B7K-902", "+51 954 118 220", 4.8),
        ConductorInfo("Luis Ccahuana", "Hyundai Accent", "C4M-118", "+51 921 447 508", 4.7),
        ConductorInfo("Miriam Huamán", "Toyota Corolla", "D9P-556", "+51 967 330 914", 4.9),
        ConductorInfo("Jorge Ayala", "Chevrolet Sail", "F1T-370", "+51 998 201 776", 4.6),
        ConductorInfo("Nancy Paredes", "Hyundai H1", "G6V-284", "+51 943 662 185", 5.0)
    )
}
