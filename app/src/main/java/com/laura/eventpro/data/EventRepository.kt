package com.laura.eventpro.data

import kotlinx.coroutines.flow.Flow

class EventRepository(
    private val serviceDao: ServiceDao,
    private val reservationDao: ReservationDao
) {
    val services: Flow<List<ServiceEntity>> = serviceDao.observeAll()
    val reservations: Flow<List<ReservationWithServices>> = reservationDao.observeAll()

    suspend fun saveService(service: ServiceEntity) {
        if (service.id == 0L) serviceDao.insert(service) else serviceDao.update(service)
    }

    suspend fun deleteService(service: ServiceEntity) = serviceDao.delete(service)

    suspend fun saveReservation(reservation: ReservationEntity, serviceIds: List<Long>) =
        reservationDao.saveWithServices(reservation, serviceIds)

    suspend fun updateStatus(id: Long, status: String) = reservationDao.updateStatus(id, status)

    suspend fun deleteReservation(reservation: ReservationEntity) = reservationDao.delete(reservation)
}
