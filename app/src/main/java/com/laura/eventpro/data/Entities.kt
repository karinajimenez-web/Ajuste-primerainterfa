package com.laura.eventpro.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

/** Entidad 1: catálogo de servicios del negocio. */
@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val perGuest: Boolean = false // true = el precio se multiplica por invitado (ej. Catering)
)

/** Entidad 2: reservas de eventos. */
@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val client: String,
    val eventType: String,
    val date: String,
    val guests: Int,
    val status: String = "Pendiente", // Pendiente | Confirmada | Cancelada
    val total: Int,
    val createdAt: Long = System.currentTimeMillis()
)

/** Relación muchos-a-muchos: una reserva tiene varios servicios. */
@Entity(
    tableName = "reservation_services",
    primaryKeys = ["reservationId", "serviceId"],
    foreignKeys = [
        ForeignKey(ReservationEntity::class, parentColumns = ["id"], childColumns = ["reservationId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(ServiceEntity::class, parentColumns = ["id"], childColumns = ["serviceId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("serviceId")]
)
data class ReservationServiceCrossRef(val reservationId: Long, val serviceId: Long)

/** Reserva junto con sus servicios asociados. */
data class ReservationWithServices(
    @Embedded val reservation: ReservationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(ReservationServiceCrossRef::class, parentColumn = "reservationId", entityColumn = "serviceId")
    )
    val services: List<ServiceEntity>
)
