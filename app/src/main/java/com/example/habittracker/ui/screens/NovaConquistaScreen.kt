package com.example.habittracker.ui.screens

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
import androidx.navigation.NavController
import com.example.habittracker.ui.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaConquistaScreen(navController: NavController, viewModel: HabitViewModel) {
    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Conquista", fontWeight = FontWeight.Bold) },
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
                value = titulo,
                onValueChange = { titulo = it },
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
                value = descricao,
                onValueChange = { descricao = it },
                placeholder = { Text("Ex: Complete hábitos por 7 dias consecutivos") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (titulo.isBlank()) {
                        Toast.makeText(context, "Digite um título para a conquista", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.adicionarConquista(titulo, descricao)
                        Toast.makeText(context, "Conquista \"$titulo\" salva!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(50.dp)
            ) {
                Text("Salvar Conquista", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}