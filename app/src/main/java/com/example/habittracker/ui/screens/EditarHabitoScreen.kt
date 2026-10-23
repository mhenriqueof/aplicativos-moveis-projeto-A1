package com.example.habittracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.habittracker.data.model.Habito
import com.example.habittracker.ui.viewmodel.HabitViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarHabitoScreen(navController: NavController, viewModel: HabitViewModel, habitoId: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var diasSelecionados by remember { mutableStateOf(setOf<Int>()) }
    var carregando by remember { mutableStateOf(true) }
    var habitoOriginal by remember { mutableStateOf<Habito?>(null) }

    // Busca o hábito original do banco ao abrir a tela
    LaunchedEffect(habitoId) {
        val h = viewModel.buscarHabitoPorId(habitoId)
        if (h != null) {
            habitoOriginal = h
            nome = h.nome
            descricao = h.descricao
            // Converte String "1,2,3" de volta para Set<Int>
            diasSelecionados = h.diasSemana.split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .toSet()
        }
        carregando = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Hábito", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
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
        } else if (habitoOriginal == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Hábito não encontrado", color = Color.Red)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Nome do hábito",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B6B6B),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    placeholder = { Text("Ex: Beber água") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "Descrição (opcional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B6B6B),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    placeholder = { Text("Ex: Manter hidratação durante o dia") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "Repetir em quais dias?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B6B6B),
                    modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)
                )

                SeletorDiasDaSemana(
                    diasSelecionados = diasSelecionados,
                    onDiaClicado = { indiceDia ->
                        diasSelecionados = if (diasSelecionados.contains(indiceDia)) {
                            diasSelecionados - indiceDia
                        } else {
                            diasSelecionados + indiceDia
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (nome.isBlank()) {
                            Toast.makeText(context, "Digite um nome para o hábito", Toast.LENGTH_SHORT).show()
                        } else {
                            val h = habitoOriginal!!
                            // Cria uma cópia atualizada mantendo o mesmo ID e concluidoHoje
                            val atualizado = h.copy(
                                nome = nome,
                                descricao = descricao,
                                diasSemana = diasSelecionados.sorted().joinToString(",")
                            )
                            scope.launch {
                                viewModel.atualizarHabito(atualizado)
                                Toast.makeText(context, "Hábito atualizado!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .height(50.dp)
                ) {
                    Text("Salvar Alterações", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
