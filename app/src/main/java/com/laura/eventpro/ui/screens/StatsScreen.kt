package com.laura.eventpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.laura.eventpro.ui.components.money
import com.laura.eventpro.viewmodel.EventViewModel

/** Estadísticas calculadas en vivo a partir de las reservas guardadas en Room. */
@Composable
fun StatsScreen(vm: EventViewModel) {
    val stats by vm.stats.collectAsState()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Estadísticas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Reservas totales", "${stats.totalReservations}", Modifier.weight(1f))
            StatCard("Pendientes", "${stats.pending}", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Confirmadas", "${stats.confirmed}", Modifier.weight(1f))
            StatCard("Canceladas", "${stats.cancelled}", Modifier.weight(1f))
        }
        StatCard("Ingresos de reservas confirmadas", money(stats.revenueConfirmed), Modifier.fillMaxWidth())
        Text("Reservas por tipo de evento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (stats.byType.isEmpty()) Text("Aún no hay datos.")
        stats.byType.forEach { (type, count) ->
            Text("$type: $count")
            LinearProgressIndicator(progress = count.toFloat() / stats.totalReservations.coerceAtLeast(1), modifier = Modifier.fillMaxWidth())
        }
    }
}
