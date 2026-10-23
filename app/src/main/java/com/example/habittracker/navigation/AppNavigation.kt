package com.example.habittracker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.habittracker.ui.screens.*
import com.example.habittracker.ui.viewmodel.HabitViewModel
import com.example.habittracker.ui.screens.EditarHabitoScreen
import com.example.habittracker.ui.screens.NovaConquistaScreen
import com.example.habittracker.ui.screens.EditarConquistaScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Cria o ViewModel compartilhado - sobrevive à navegação entre telas
    val viewModel: HabitViewModel = viewModel()

    val bottomNavItems = listOf(
        BottomNavItem(Rotas.HOME, "Home", Icons.Default.Home),
        BottomNavItem(Rotas.RECOMPENSAS, "Recompensas", Icons.Default.Star),
        BottomNavItem(Rotas.PERFIL, "Perfil", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, items = bottomNavItems)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Rotas.HOME,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Rotas.HOME) { HomeScreen(navController, viewModel) }
            composable(Rotas.NOVO_HABITO) { NovoHabitoScreen(navController, viewModel) }
            composable(Rotas.RECOMPENSAS) { RecompensasScreen(navController, viewModel) }
            composable(Rotas.PERFIL) { PerfilScreen(navController, viewModel) }
            composable(Rotas.CONFIGURACOES) { ConfiguracoesScreen(navController) }
            composable(Rotas.DETALHE_HABITO) { backStackEntry ->
                val habitoId = backStackEntry.arguments?.getString("habitoId")?.toIntOrNull() ?: 0
                DetalheHabitoScreen(navController, viewModel, habitoId)
            }
            composable(Rotas.DETALHE_CONQUISTA) { backStackEntry ->
                val conquistaId = backStackEntry.arguments?.getString("conquistaId")?.toIntOrNull() ?: 0
                DetalheConquistaScreen(navController, viewModel, conquistaId)
            }
            composable(Rotas.EDITAR_HABITO) { backStackEntry ->
                val habitoId = backStackEntry.arguments?.getString("habitoId")?.toIntOrNull() ?: 0
                EditarHabitoScreen(navController, viewModel, habitoId)
            }
            composable(Rotas.NOVA_CONQUISTA) { NovaConquistaScreen(navController, viewModel) }
            composable(Rotas.EDITAR_CONQUISTA) { backStackEntry ->
                val conquistaId = backStackEntry.arguments?.getString("conquistaId")?.toIntOrNull() ?: 0
                EditarConquistaScreen(navController, viewModel, conquistaId)
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun BottomNavigationBar(
    navController: androidx.navigation.NavHostController,
    items: List<BottomNavItem>
) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}