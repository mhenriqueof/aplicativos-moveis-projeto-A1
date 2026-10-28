package com.example.habittracker.ui.habitos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.habittracker.data.local.AppDatabase
import com.example.habittracker.data.local.Habito
import com.example.habittracker.data.repository.HabitoRepository
import com.example.habittracker.navigation.Rotas
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheHabitoScreen(
    navController: NavController,
    habitoId: Int,
    viewModel: HabitoViewModel = viewModel(
        factory = HabitoViewModelFactory(
            HabitoRepository(AppDatabase.getDatabase(LocalContext.current).habitoDao())
        )
    )
) {
    var habito by remember { mutableStateOf<Habito?>(null) }
    var carregando by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(habitoId) {
        habito = viewModel.buscarPorId(habitoId)
        carregando = false
    }

    val taxaConclusao = viewModel.calcularTaxaConclusao()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Hábito", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Rotas.editarHabito(habitoId))
                    }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (carregando) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (habito == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Hábito não encontrado", color = Color.Red)
            }
        } else {
            val h = habito!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (h.concluidoHoje) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = h.nome, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (h.concluidoHoje) "✅ Concluído hoje" else "⏳ Pendente hoje",
                            fontSize = 14.sp,
                            color = if (h.concluidoHoje) Color(0xFF2E7D32) else Color(0xFF6B6B6B),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (h.descricao.isNotBlank()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Descrição", fontSize = 12.sp, color = Color(0xFF6B6B6B), fontWeight = FontWeight.Medium)
                            Text(h.descricao, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Dias de repetição", fontSize = 12.sp, color = Color(0xFF6B6B6B), fontWeight = FontWeight.Medium)
                        Text(
                            text = formatarDiasSemana(h.diasSemana),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📊 Estatística Global", fontSize = 12.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Medium)
                        Text(
                            text = "Taxa de conclusão hoje: ${(taxaConclusao * 100).toInt()}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "${uiState.listaDeHabitos.count { it.concluidoHoje }} de ${uiState.listaDeHabitos.size} hábitos concluídos",
                            fontSize = 12.sp,
                            color = Color(0xFF6B6B6B),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Progresso do dia", fontSize = 12.sp, color = Color(0xFF6B6B6B), fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { taxaConclusao },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = Color(0xFF4C6EF5),
                            trackColor = Color(0xFFE0E0E0)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.onToggleConcluido(h.id)
                            habito = viewModel.buscarPorId(h.id)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(
                        if (h.concluidoHoje) "Desmarcar Conclusão" else "Marcar como Concluído",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun formatarDiasSemana(diasString: String): String {
    if (diasString.isBlank()) return "Não definido"
    val nomes = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
    return diasString.split(",")
        .mapNotNull { it.trim().toIntOrNull() }
        .sorted()
        .mapNotNull { nomes.getOrNull(it) }
        .joinToString(", ")
}