package com.laura.eventpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.laura.eventpro.data.EventDatabase
import com.laura.eventpro.data.EventRepository
import com.laura.eventpro.data.ReservationEntity
import com.laura.eventpro.data.ReservationWithServices
import com.laura.eventpro.data.ServiceEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EventStats(
    val totalReservations: Int = 0,
    val pending: Int = 0,
    val confirmed: Int = 0,
    val cancelled: Int = 0,
    val revenueConfirmed: Int = 0,
    val byType: Map<String, Int> = emptyMap()
)

class EventViewModel(app: Application) : AndroidViewModel(app) {
    private val db = EventDatabase.get(app)
    private val repository = EventRepository(db.serviceDao(), db.reservationDao())

    val services: StateFlow<List<ServiceEntity>> = repository.services
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reservations: StateFlow<List<ReservationWithServices>> = repository.reservations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtros de la pantalla de reservas
    val query = MutableStateFlow("")
    val statusFilter = MutableStateFlow("Todas")

    val filteredReservations: StateFlow<List<ReservationWithServices>> =
        combine(reservations, query, statusFilter) { list, q, status ->
            list.filter {
                (status == "Todas" || it.reservation.status == status) &&
                    (q.isBlank() ||
                        it.reservation.client.contains(q, ignoreCase = true) ||
                        it.reservation.eventType.contains(q, ignoreCase = true))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<EventStats> = reservations.map { list ->
        EventStats(
            totalReservations = list.size,
            pending = list.count { it.reservation.status == "Pendiente" },
            confirmed = list.count { it.reservation.status == "Confirmada" },
            cancelled = list.count { it.reservation.status == "Cancelada" },
            revenueConfirmed = list.filter { it.reservation.status == "Confirmada" }.sumOf { it.reservation.total },
            byType = list.groupingBy { it.reservation.eventType }.eachCount()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EventStats())

    /** Total = suma de servicios; los marcados "por invitado" se multiplican por la cantidad de invitados. */
    fun calculateTotal(selected: List<ServiceEntity>, guests: Int): Int =
        selected.sumOf { if (it.perGuest) it.price * guests else it.price }

    // ---- Servicios (CRUD) ----
    fun saveService(service: ServiceEntity) = viewModelScope.launch { repository.saveService(service) }
    fun deleteService(service: ServiceEntity) = viewModelScope.launch { repository.deleteService(service) }

    // ---- Reservas ----
    fun saveReservation(
        id: Long, client: String, eventType: String, date: String, guests: Int, serviceIds: List<Long>
    ) = viewModelScope.launch {
        val selected = services.value.filter { it.id in serviceIds }
        val total = calculateTotal(selected, guests)
        val existing = reservations.value.firstOrNull { it.reservation.id == id }?.reservation
        val reservation = ReservationEntity(
            id = id, client = client.trim(), eventType = eventType, date = date.trim(), guests = guests,
            status = existing?.status ?: "Pendiente", total = total,
            createdAt = existing?.createdAt ?: System.currentTimeMillis()
        )
        repository.saveReservation(reservation, serviceIds)
    }

    fun updateStatus(id: Long, status: String) = viewModelScope.launch { repository.updateStatus(id, status) }
    fun deleteReservation(reservation: ReservationEntity) = viewModelScope.launch { repository.deleteReservation(reservation) }
}
