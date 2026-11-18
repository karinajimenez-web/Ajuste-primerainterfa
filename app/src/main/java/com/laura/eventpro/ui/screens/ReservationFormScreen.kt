package com.laura.eventpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.laura.eventpro.ui.components.money
import com.laura.eventpro.viewmodel.EventViewModel

private val DATE_REGEX = Regex("""^\d{2}/\d{2}/\d{4}$""")

/** Formulario para registrar (reservationId == -1) o editar una reserva. */
@Composable
fun ReservationFormScreen(vm: EventViewModel, reservationId: Long, onDone: () -> Unit) {
    val services by vm.services.collectAsState()
    val reservations by vm.reservations.collectAsState()
    val existing = reservations.firstOrNull { it.reservation.id == reservationId }
    val key = existing?.reservation?.id ?: -1L

    var client by remember(key) { mutableStateOf(existing?.reservation?.client ?: "") }
    var eventType by remember(key) { mutableStateOf(existing?.reservation?.eventType ?: "Cumpleaños") }
    var date by remember(key) { mutableStateOf(existing?.reservation?.date ?: "") }
    var guestsText by remember(key) { mutableStateOf(existing?.reservation?.guests?.toString() ?: "30") }
    val selectedIds: SnapshotStateList<Long> = remember(key) {
        mutableStateListOf<Long>().apply { existing?.services?.forEach { add(it.id) } }
    }
    var error by remember { mutableStateOf("") }

    val guests = guestsText.toIntOrNull() ?: 0
    val total = vm.calculateTotal(services.filter { it.id in selectedIds }, guests)

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(if (existing == null) "Nueva reserva" else "Editar reserva", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        OutlinedTextField(client, { client = it }, label = { Text("Nombre del cliente") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Text("Tipo de evento", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("Boda", "Cumpleaños", "Empresarial").forEach { option ->
                FilterChip(selected = eventType == option, onClick = { eventType = option }, label = { Text(option) })
            }
        }
        OutlinedTextField(date, { date = it }, label = { Text("Fecha (DD/MM/AAAA)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(
            guestsText, { guestsText = it.filter(Char::isDigit) }, label = { Text("Cantidad de invitados") },
            modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text("Servicios", fontWeight = FontWeight.SemiBold)
        services.forEach { s ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = s.id in selectedIds,
                    onCheckedChange = { if (it) selectedIds.add(s.id) else selectedIds.remove(s.id) }
                )
                Column {
                    Text(s.name)
                    Text(if (s.perGuest) "${money(s.price)} por invitado" else money(s.price), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Text("Total estimado: ${money(total)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                when {
                    client.isBlank() -> error = "Ingresa el nombre del cliente."
                    !DATE_REGEX.matches(date.trim()) -> error = "La fecha debe tener el formato DD/MM/AAAA."
                    guests < 1 -> error = "Ingresa una cantidad válida de invitados."
                    selectedIds.isEmpty() -> error = "Selecciona al menos un servicio."
                    else -> {
                        vm.saveReservation(existing?.reservation?.id ?: 0L, client, eventType, date, guests, selectedIds.toList())
                        onDone()
                    }
                }
            }
        ) { Text(if (existing == null) "Registrar reserva" else "Guardar cambios") }
        OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}
