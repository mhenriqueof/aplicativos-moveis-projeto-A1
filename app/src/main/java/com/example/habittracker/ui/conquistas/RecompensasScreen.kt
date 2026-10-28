package com.example.habittracker.ui.conquistas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.habittracker.data.local.Conquista
import com.example.habittracker.data.repository.ConquistaRepository
import com.example.habittracker.navigation.Rotas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompensasScreen(
    navController: NavController,
    viewModel: ConquistaViewModel = viewModel(
        factory = ConquistaViewModelFactory(
            ConquistaRepository(AppDatabase.getDatabase(LocalContext.current).conquistaDao())
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conquistas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Rotas.NOVA_CONQUISTA) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Conquista")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "${viewModel.totalDesbloqueadas()} de ${uiState.listaDeConquistas.size} desbloqueadas",
                fontSize = 14.sp,
                color = Color(0xFF6B6B6B),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.listaDeConquistas, key = { it.id }) { conquista ->
                    ConquistaCard(
                        conquista = conquista,
                        onToggleClick = { viewModel.onToggleDesbloqueada(conquista.id) },
                        onDeleteClick = { viewModel.onDelete(conquista) },
                        onCardClick = { navController.navigate(Rotas.detalheConquista(conquista.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun ConquistaCard(
    conquista: Conquista,
    onToggleClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (conquista.desbloqueada) Color(0xFFFFF8E1) else Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (conquista.desbloqueada) Color(0xFFFFD43B) else Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (conquista.desbloqueada) "🏅" else "🔒",
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conquista.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (conquista.desbloqueada) Color(0xFF1F1F1F) else Color(0xFF888888)
                )
                Text(
                    text = conquista.descricao,
                    fontSize = 12.sp,
                    color = Color(0xFF6B6B6B),
                    maxLines = 2
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Deletar Conquista",
                    tint = Color(0xFFE53935)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            TextButton(onClick = onToggleClick) {
                Text(if (conquista.desbloqueada) "Desfazer" else "Marcar")
            }
        }
    }
}