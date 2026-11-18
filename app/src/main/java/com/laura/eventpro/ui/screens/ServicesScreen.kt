package com.laura.eventpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.laura.eventpro.data.ServiceEntity
import com.laura.eventpro.ui.components.ConfirmDialog
import com.laura.eventpro.ui.components.money
import com.laura.eventpro.ui.theme.RoseLight
import com.laura.eventpro.viewmodel.EventViewModel

/** CRUD del catálogo de servicios (guardado en Room). */
@Composable
fun ServicesScreen(vm: EventViewModel) {
    val services by vm.services.collectAsState()
    var query by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf<ServiceEntity?>(null) }
    var creating by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<ServiceEntity?>(null) }

    val filtered = services.filter { it.name.contains(query, true) || it.category.contains(query, true) }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Nuestros servicios", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("${services.size} servicios registrados", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { creating = true }) { Text("+ Nuevo") }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = query, onValueChange = { query = it }, label = { Text("Buscar servicio o categoría") },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        if (filtered.isEmpty()) Text("No hay servicios que coincidan.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered, key = { it.id }) { service ->
                ServiceCard(service, onEdit = { editing = service }, onDelete = { toDelete = service })
            }
        }
    }

    if (creating || editing != null) {
        ServiceFormDialog(
            initial = editing,
            onDismiss = { creating = false; editing = null },
            onSave = { vm.saveService(it); creating = false; editing = null }
        )
    }
    toDelete?.let { s ->
        ConfirmDialog("Eliminar servicio", "¿Eliminar \"${s.name}\"? Se quitará de las reservas que lo incluyan.",
            onConfirm = { vm.deleteService(s); toDelete = null }, onDismiss = { toDelete = null })
    }
}

@Composable
private fun ServiceCard(service: ServiceEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = RoseLight)) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(service.name, fontWeight = FontWeight.Bold)
            Text(service.category, style = MaterialTheme.typography.labelMedium)
            Text(service.description, style = MaterialTheme.typography.bodySmall)
            Text(
                if (service.perGuest) "${money(service.price)} por invitado" else money(service.price),
                color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold
            )
            Row {
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete) { Text("Eliminar") }
            }
        }
    }
}

@Composable
private fun ServiceFormDialog(initial: ServiceEntity?, onDismiss: () -> Unit, onSave: (ServiceEntity) -> Unit) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "") }
    var priceText by remember { mutableStateOf(initial?.price?.toString() ?: "") }
    var perGuest by remember { mutableStateOf(initial?.perGuest ?: false) }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nuevo servicio" else "Editar servicio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(category, { category = it }, label = { Text("Categoría") }, singleLine = true)
                OutlinedTextField(description, { description = it }, label = { Text("Descripción") })
                OutlinedTextField(
                    priceText, { priceText = it.filter(Char::isDigit) }, label = { Text("Precio (COP)") },
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(perGuest, { perGuest = it })
                    Text("Se cobra por invitado")
                }
                if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val price = priceText.toIntOrNull()
                if (name.isBlank() || category.isBlank() || price == null || price <= 0) {
                    error = "Nombre, categoría y un precio mayor a 0 son obligatorios."
                } else {
                    onSave(ServiceEntity(initial?.id ?: 0, name.trim(), description.trim(), price, category.trim(), perGuest))
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
