package com.laura.eventpro.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.laura.eventpro.ui.screens.*
import com.laura.eventpro.ui.theme.RoseLight
import com.laura.eventpro.viewmodel.EventViewModel

object Routes {
    const val HOME = "home"
    const val SERVICES = "services"
    const val RESERVATIONS = "reservations"
    const val STATS = "stats"
    const val FORM = "form?id={id}"
    fun form(id: Long = -1L) = "form?id=$id"
}

private data class NavItem(val route: String, val label: String, val icon: String)
private val navItems = listOf(
    NavItem(Routes.HOME, "Inicio", "⌂"),
    NavItem(Routes.SERVICES, "Servicios", "◇"),
    NavItem(Routes.RESERVATIONS, "Reservas", "▣"),
    NavItem(Routes.STATS, "Estadísticas", "▤")
)

@Composable
fun EventProApp(vm: EventViewModel = viewModel()) {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopBar(onHome = { navigateTo(navController, Routes.HOME) }) },
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        NavHost(navController, startDestination = Routes.HOME, modifier = Modifier.padding(padding)) {
            composable(Routes.HOME) {
                HomeScreen(vm, onServices = { navigateTo(navController, Routes.SERVICES) }, onReservations = { navigateTo(navController, Routes.RESERVATIONS) })
            }
            composable(Routes.SERVICES) { ServicesScreen(vm) }
            composable(Routes.RESERVATIONS) {
                ReservationsScreen(vm, onNew = { navController.navigate(Routes.form()) }, onEdit = { navController.navigate(Routes.form(it)) })
            }
            composable(Routes.FORM, arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })) { entry ->
                ReservationFormScreen(vm, entry.arguments?.getLong("id") ?: -1L, onDone = { navController.popBackStack() })
            }
            composable(Routes.STATS) { StatsScreen(vm) }
        }
    }
}

private fun navigateTo(nav: NavHostController, route: String) {
    nav.navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun TopBar(onHome: () -> Unit) {
    Surface(shadowElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("EventPro", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Gestión de eventos", style = MaterialTheme.typography.labelMedium)
            }
            TextButton(onClick = onHome) { Text("Inicio") }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val current by navController.currentBackStackEntryAsState()
    val route = current?.destination?.route
    NavigationBar(containerColor = RoseLight) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = route == item.route,
                onClick = { navigateTo(navController, item.route) },
                icon = { Text(item.icon) },
                label = { Text(item.label) }
            )
        }
    }
}
