package com.example.habittracker.ui.habitos

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.habittracker.data.local.AppDatabase
import com.example.habittracker.data.repository.HabitoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovoHabitoScreen(
    navController: NavController,
    viewModel: HabitoViewModel = viewModel(
        factory = HabitoViewModelFactory(
            HabitoRepository(AppDatabase.getDatabase(LocalContext.current).habitoDao())
        )
    )
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Hábito", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.limparCampos()
                        navController.popBackStack()
                    }) {
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
                value = uiState.nome,
                onValueChange = { viewModel.onNomeChange(it) },
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
                value = uiState.descricao,
                onValueChange = { viewModel.onDescricaoChange(it) },
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
                diasSelecionados = uiState.diasSemana,
                onDiaClicado = { viewModel.onDiaSemanaToggle(it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (uiState.nome.isBlank()) {
                        Toast.makeText(context, "Digite um nome para o hábito", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.onSave()
                        Toast.makeText(context, "Hábito salvo!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(50.dp)
            ) {
                Text(uiState.textoBotao, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SeletorDiasDaSemana(diasSelecionados: Set<Int>, onDiaClicado: (Int) -> Unit) {
    val letrasDosDias = listOf("D", "S", "T", "Q", "Q", "S", "S")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        letrasDosDias.forEachIndexed { indice, letra ->
            val selecionado = diasSelecionados.contains(indice)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (selecionado) Color(0xFF4C6EF5) else Color(0xFFF0F0F0))
                    .clickable { onDiaClicado(indice) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letra,
                    color = if (selecionado) Color.White else Color(0xFF444444),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}