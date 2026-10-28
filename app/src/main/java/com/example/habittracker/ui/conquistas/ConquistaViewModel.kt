package com.example.habittracker.ui.conquistas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.local.Conquista
import com.example.habittracker.data.repository.ConquistaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConquistaViewModel(
    private val repository: ConquistaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConquistaUiState())
    val uiState: StateFlow<ConquistaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllConquistas().collect { conquistas ->
                _uiState.update { it.copy(listaDeConquistas = conquistas) }
            }
        }
    }

    fun onTituloChange(novoTitulo: String) {
        _uiState.update { it.copy(titulo = novoTitulo) }
    }

    fun onDescricaoChange(novaDescricao: String) {
        _uiState.update { it.copy(descricao = novaDescricao) }
    }

    fun onEdit(conquista: Conquista) {
        _uiState.update {
            it.copy(
                conquistaEmEdicao = conquista,
                titulo = conquista.titulo,
                descricao = conquista.descricao
            )
        }
    }

    fun onDelete(conquista: Conquista) {
        viewModelScope.launch {
            repository.deletar(conquista)
        }
    }

    fun onToggleDesbloqueada(conquistaId: Int) {
        viewModelScope.launch {
            val conquista = repository.buscarPorId(conquistaId) ?: return@launch
            val atualizada = conquista.copy(desbloqueada = !conquista.desbloqueada)
            repository.atualizar(atualizada)
        }
    }

    fun onSave() {
        val state = _uiState.value
        if (state.titulo.isBlank()) return

        val conquistaParaSalvar = state.conquistaEmEdicao?.copy(
            titulo = state.titulo,
            descricao = state.descricao
        ) ?: Conquista(
            titulo = state.titulo,
            descricao = state.descricao
        )

        viewModelScope.launch {
            if (state.conquistaEmEdicao == null) {
                repository.inserir(conquistaParaSalvar)
            } else {
                repository.atualizar(conquistaParaSalvar)
            }
            limparCampos()
        }
    }

    fun limparCampos() {
        _uiState.update {
            it.copy(titulo = "", descricao = "", conquistaEmEdicao = null)
        }
    }

    suspend fun buscarPorId(id: Int): Conquista? {
        return repository.buscarPorId(id)
    }

    fun totalDesbloqueadas(): Int {
        return _uiState.value.listaDeConquistas.count { it.desbloqueada }
    }
}