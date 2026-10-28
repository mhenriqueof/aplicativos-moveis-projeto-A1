package com.example.habittracker.ui.comum

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.habittracker.data.local.AppDatabase
import com.example.habittracker.data.repository.ConquistaRepository
import com.example.habittracker.data.repository.HabitoRepository
import com.example.habittracker.navigation.Rotas
import com.example.habittracker.ui.conquistas.ConquistaViewModel
import com.example.habittracker.ui.conquistas.ConquistaViewModelFactory
import com.example.habittracker.ui.habitos.HabitoViewModel
import com.example.habittracker.ui.habitos.HabitoViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    habitoViewModel: HabitoViewModel = viewModel(
        factory = HabitoViewModelFactory(
            HabitoRepository(AppDatabase.getDatabase(LocalContext.current).habitoDao())
        )
    ),
    conquistaViewModel: ConquistaViewModel = viewModel(
        factory = ConquistaViewModelFactory(
            ConquistaRepository(AppDatabase.getDatabase(LocalContext.current).conquistaDao())
        )
    )
) {
    val habitoUiState by habitoViewModel.uiState.collectAsStateWithLifecycle()
    val conquistaUiState by conquistaViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("👤 Ana Silva", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("ana@email.com", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("📊 Suas Estatísticas", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatItem("Hábitos", "${habitoUiState.listaDeHabitos.size}")
                        StatItem("Concluídos", "${habitoUiState.listaDeHabitos.count { it.concluidoHoje }}")
                        StatItem("Conquistas", "${conquistaViewModel.totalDesbloqueadas()}/${conquistaUiState.listaDeConquistas.size}")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate(Rotas.CONFIGURACOES) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⚙️ Configurações")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}