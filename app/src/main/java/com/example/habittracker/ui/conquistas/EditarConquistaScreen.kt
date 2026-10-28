package com.example.habittracker.ui.conquistas

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
import com.example.habittracker.data.repository.ConquistaRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarConquistaScreen(
    navController: NavController,
    conquistaId: Int,
    viewModel: ConquistaViewModel = viewModel(
        factory = ConquistaViewModelFactory(
            ConquistaRepository(AppDatabase.getDatabase(LocalContext.current).conquistaDao())
        )
    )
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(conquistaId) {
        val conquista = viewModel.buscarPorId(conquistaId)
        if (conquista != null) {
            viewModel.onEdit(conquista)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Conquista", fontWeight = FontWeight.Bold) },
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
        if (uiState.conquistaEmEdicao == null) {
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
                    text = "Título da conquista",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B6B6B),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = uiState.titulo,
                    onValueChange = { viewModel.onTituloChange(it) },
                    placeholder = { Text("Ex: 7 dias seguidos") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "Descrição",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B6B6B),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = uiState.descricao,
                    onValueChange = { viewModel.onDescricaoChange(it) },
                    placeholder = { Text("Ex: Complete hábitos por 7 dias consecutivos") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (uiState.titulo.isBlank()) {
                            Toast.makeText(context, "Digite um título para a conquista", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.onSave()
                            Toast.makeText(context, "Conquista atualizada!", Toast.LENGTH_SHORT).show()
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