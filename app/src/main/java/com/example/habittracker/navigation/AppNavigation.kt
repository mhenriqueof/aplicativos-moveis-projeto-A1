package com.example.habittracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.habittracker.ui.habitos.DetalheHabitoScreen
import com.example.habittracker.ui.habitos.EditarHabitoScreen
import com.example.habittracker.ui.habitos.HomeScreen
import com.example.habittracker.ui.habitos.NovoHabitoScreen
import com.example.habittracker.ui.conquistas.DetalheConquistaScreen
import com.example.habittracker.ui.conquistas.EditarConquistaScreen
import com.example.habittracker.ui.conquistas.NovaConquistaScreen
import com.example.habittracker.ui.conquistas.RecompensasScreen
import com.example.habittracker.ui.comum.ConfiguracoesScreen
import com.example.habittracker.ui.comum.PerfilScreen
import androidx.compose.ui.Modifier

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Rotas.HOME,
        modifier = modifier  // <-- ADICIONE ESTA LINHA
    ) {
        composable(Rotas.HOME) { HomeScreen(navController) }
        composable(Rotas.NOVO_HABITO) { NovoHabitoScreen(navController) }
        composable(Rotas.EDITAR_HABITO) { backStackEntry ->
            val habitoId = backStackEntry.arguments?.getString("habitoId")?.toIntOrNull() ?: 0
            EditarHabitoScreen(navController, habitoId)
        }
        composable(Rotas.DETALHE_HABITO) { backStackEntry ->
            val habitoId = backStackEntry.arguments?.getString("habitoId")?.toIntOrNull() ?: 0
            DetalheHabitoScreen(navController, habitoId)
        }
        composable(Rotas.RECOMPENSAS) { RecompensasScreen(navController) }
        composable(Rotas.NOVA_CONQUISTA) { NovaConquistaScreen(navController) }
        composable(Rotas.EDITAR_CONQUISTA) { backStackEntry ->
            val conquistaId = backStackEntry.arguments?.getString("conquistaId")?.toIntOrNull() ?: 0
            EditarConquistaScreen(navController, conquistaId)
        }
        composable(Rotas.DETALHE_CONQUISTA) { backStackEntry ->
            val conquistaId = backStackEntry.arguments?.getString("conquistaId")?.toIntOrNull() ?: 0
            DetalheConquistaScreen(navController, conquistaId)
        }
        composable(Rotas.PERFIL) { PerfilScreen(navController) }
        composable(Rotas.CONFIGURACOES) { ConfiguracoesScreen(navController) }
    }
}