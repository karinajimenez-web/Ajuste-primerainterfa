package com.laura.eventpro.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.laura.eventpro.data.ReservationEntity
import com.laura.eventpro.data.ReservationWithServices
import com.laura.eventpro.ui.components.ConfirmDialog
import com.laura.eventpro.ui.components.StatusChip
import com.laura.eventpro.ui.components.money
import com.laura.eventpro.ui.theme.RoseLight
import com.laura.eventpro.viewmodel.EventViewModel

/** Consulta de reservas guardadas en Room: búsqueda, filtro por estado, cambio de estado, edición y eliminación. */
@Composable
fun ReservationsScreen(vm: EventViewModel, onNew: () -> Unit, onEdit: (Long) -> Unit) {
    val list by vm.filteredReservations.collectAsState()
    val query by vm.query.collectAsState()
    val filter by vm.statusFilter.collectAsState()
    var toDelete by remember { mutableStateOf<ReservationEntity?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Reservas", Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Button(onClick = onNew) { Text("+ Nueva") }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = query, onValueChange = { vm.query.value = it }, label = { Text("Buscar por cliente o tipo de evento") },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Todas", "Pendiente", "Confirmada", "Cancelada").forEach { option ->
                FilterChip(selected = filter == option, onClick = { vm.statusFilter.value = option }, label = { Text(option) })
            }
        }
        if (list.isEmpty()) Text("No hay reservas para mostrar.", style = MaterialTheme.typography.bodyMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(list, key = { it.reservation.id }) { item ->
                ReservationCard(
                    item,
                    onEdit = { onEdit(item.reservation.id) },
                    onStatus = { vm.updateStatus(item.reservation.id, it) },
                    onDelete = { toDelete = item.reservation }
                )
            }
        }
    }

    toDelete?.let { r ->
        ConfirmDialog("Eliminar reserva", "¿Eliminar la reserva de ${r.client}?",
            onConfirm = { vm.deleteReservation(r); toDelete = null }, onDismiss = { toDelete = null })
    }
}

@Composable
private fun ReservationCard(item: ReservationWithServices, onEdit: () -> Unit, onStatus: (String) -> Unit, onDelete: () -> Unit) {
    val r = item.reservation
    val tight = PaddingValues(horizontal = 6.dp)
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = RoseLight)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(r.client, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                StatusChip(r.status)
            }
            Text("${r.eventType} · ${r.date} · ${r.guests} invitados")
            Text("Servicios: " + item.services.joinToString { it.name }.ifEmpty { "Ninguno" }, style = MaterialTheme.typography.bodySmall)
            Text("Total: ${money(r.total)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Row {
                TextButton(onClick = onEdit, contentPadding = tight) { Text("Editar") }
                if (r.status != "Confirmada") TextButton(onClick = { onStatus("Confirmada") }, contentPadding = tight) { Text("Confirmar") }
                if (r.status != "Cancelada") TextButton(onClick = { onStatus("Cancelada") }, contentPadding = tight) { Text("Cancelar") }
                TextButton(onClick = onDelete, contentPadding = tight) { Text("Eliminar") }
            }
        }
    }
}
