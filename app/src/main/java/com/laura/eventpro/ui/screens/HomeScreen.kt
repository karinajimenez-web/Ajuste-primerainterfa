package com.laura.eventpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.laura.eventpro.ui.theme.RoseLight
import com.laura.eventpro.viewmodel.EventViewModel

@Composable
fun HomeScreen(vm: EventViewModel, onServices: () -> Unit, onReservations: () -> Unit) {
    val services by vm.services.collectAsState()
    val reservations by vm.reservations.collectAsState()
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Todo evento empieza con una buena idea", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Planea, organiza y cotiza celebraciones y eventos empresariales desde un solo lugar.")
        Card(colors = CardDefaults.cardColors(containerColor = RoseLight), shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("EVENTOS CON ESTILO", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("Tu evento, organizado a tu manera", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Bodas · Cumpleaños · Eventos corporativos")
                Button(onClick = onServices, modifier = Modifier.fillMaxWidth()) { Text("Explorar servicios") }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Servicios", "${services.size}", Modifier.weight(1f))
            StatCard("Reservas creadas", "${reservations.size}", Modifier.weight(1f))
        }
        OutlinedButton(onClick = onReservations, modifier = Modifier.fillMaxWidth()) { Text("Crear o consultar reservas") }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
