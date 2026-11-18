package com.laura.eventpro.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY name")
    fun observeAll(): Flow<List<ServiceEntity>>

    @Insert
    suspend fun insert(service: ServiceEntity): Long

    @Update
    suspend fun update(service: ServiceEntity)

    @Delete
    suspend fun delete(service: ServiceEntity)
}

@Dao
abstract class ReservationDao {
    @Transaction
    @Query("SELECT * FROM reservations ORDER BY createdAt DESC")
    abstract fun observeAll(): Flow<List<ReservationWithServices>>

    @Insert
    abstract suspend fun insert(reservation: ReservationEntity): Long

    @Update
    abstract suspend fun update(reservation: ReservationEntity)

    @Delete
    abstract suspend fun delete(reservation: ReservationEntity)

    @Insert
    abstract suspend fun insertRefs(refs: List<ReservationServiceCrossRef>)

    @Query("DELETE FROM reservation_services WHERE reservationId = :id")
    abstract suspend fun deleteRefs(id: Long)

    @Query("UPDATE reservations SET status = :status WHERE id = :id")
    abstract suspend fun updateStatus(id: Long, status: String)

    /** Crea o actualiza la reserva y sus servicios en una sola transacción. */
    @Transaction
    open suspend fun saveWithServices(reservation: ReservationEntity, serviceIds: List<Long>) {
        val id = if (reservation.id == 0L) {
            insert(reservation)
        } else {
            update(reservation)
            deleteRefs(reservation.id)
            reservation.id
        }
        insertRefs(serviceIds.map { ReservationServiceCrossRef(id, it) })
    }
}
