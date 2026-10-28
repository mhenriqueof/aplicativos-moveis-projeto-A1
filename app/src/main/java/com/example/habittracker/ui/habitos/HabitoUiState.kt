package com.example.habittracker.ui.habitos

import com.example.habittracker.data.local.Habito

/**
 * Estado da tela de Hábitos.
 * Agrupa tudo que a UI precisa para se desenhar: lista, campos do formulário, item em edição.
 */
data class HabitoUiState(
    val listaDeHabitos: List<Habito> = emptyList(),
    val nome: String = "",
    val descricao: String = "",
    val diasSemana: Set<Int> = emptySet(),
    val habitoEmEdicao: Habito? = null
) {
    // Propriedade calculada: texto do botão deriva do habitoEmEdicao
    val textoBotao: String
        get() = if (habitoEmEdicao == null) "Salvar Hábito" else "Atualizar Hábito"
}