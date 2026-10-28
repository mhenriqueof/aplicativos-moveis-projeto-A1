package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.habittracker.navigation.AppNavigation
import com.example.habittracker.navigation.Rotas
import com.example.habittracker.ui.theme.HabitTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitTrackerTheme {
                MainScreenWithBottomBar()
            }
        }
    }
}

@Composable
fun MainScreenWithBottomBar() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Itens da BottomBar: Home, Conquistas (antes Recompensas), Perfil
    val bottomBarItems = listOf(
        BottomBarItem(Rotas.HOME, "Home", Icons.Default.Home),
        BottomBarItem(Rotas.RECOMPENSAS, "Conquistas", Icons.Default.EmojiEvents),
        BottomBarItem(Rotas.PERFIL, "Perfil", Icons.Default.Person)
    )

    // Telas que NÃO devem mostrar a BottomBar (telas secundárias)
    val telasSemBottomBar = listOf(
        Rotas.NOVO_HABITO,
        Rotas.EDITAR_HABITO,
        Rotas.DETALHE_HABITO,
        Rotas.NOVA_CONQUISTA,
        Rotas.EDITAR_CONQUISTA,
        Rotas.DETALHE_CONQUISTA,
        Rotas.CONFIGURACOES
    )

    val mostrarBottomBar = currentDestination?.route !in telasSemBottomBar

    Scaffold(
        bottomBar = {
            if (mostrarBottomBar) {
                NavigationBar {
                    bottomBarItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.rota } == true
                        NavigationBarItem(
                            icon = { Icon(item.icone, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.rota) {
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
        }
    ) { padding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}

data class BottomBarItem(val rota: String, val label: String, val icone: androidx.compose.ui.graphics.vector.ImageVector)