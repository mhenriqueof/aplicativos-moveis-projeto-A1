package com.example.habittracker.ui.habitos

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.habittracker.data.local.AppDatabase
import com.example.habittracker.data.repository.HabitoRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarHabitoScreen(
    navController: NavController,
    habitoId: Int,
    viewModel: HabitoViewModel = viewModel(
        factory = HabitoViewModelFactory(
            HabitoRepository(AppDatabase.getDatabase(LocalContext.current).habitoDao())
        )
    )
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Carrega o hábito em edição ao abrir a tela
    LaunchedEffect(habitoId) {
        val habito = viewModel.buscarPorId(habitoId)
        if (habito != null) {
            viewModel.onEdit(habito)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Hábito", fontWeight = FontWeight.Bold) },
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
        if (uiState.habitoEmEdicao == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
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
                            Toast.makeText(context, "Hábito atualizado!", Toast.LENGTH_SHORT).show()
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
}