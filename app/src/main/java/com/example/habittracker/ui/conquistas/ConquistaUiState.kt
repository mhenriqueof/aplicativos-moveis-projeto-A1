package com.example.habittracker.ui.conquistas

import com.example.habittracker.data.local.Conquista

/**
 * Estado da tela de Conquistas.
 */
data class ConquistaUiState(
    val listaDeConquistas: List<Conquista> = emptyList(),
    val titulo: String = "",
    val descricao: String = "",
    val conquistaEmEdicao: Conquista? = null
) {
    val textoBotao: String
        get() = if (conquistaEmEdicao == null) "Salvar Conquista" else "Atualizar Conquista"
}