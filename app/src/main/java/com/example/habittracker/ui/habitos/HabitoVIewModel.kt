package com.example.habittracker.ui.habitos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.local.Habito
import com.example.habittracker.data.repository.HabitoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de Hábitos — segue o padrão MVVM do professor.
 * Usa StateFlow + Repository. Não acessa DAO diretamente.
 */
class HabitoViewModel(
    private val repository: HabitoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitoUiState())
    val uiState: StateFlow<HabitoUiState> = _uiState.asStateFlow()

    init {
        // Se inscreve UMA vez no Flow do Repository — a lista se atualiza sozinha
        viewModelScope.launch {
            repository.getAllHabitos().collect { habitos ->
                _uiState.update { it.copy(listaDeHabitos = habitos) }
            }
        }
    }

    fun onNomeChange(novoNome: String) {
        _uiState.update { it.copy(nome = novoNome) }
    }

    fun onDescricaoChange(novaDescricao: String) {
        _uiState.update { it.copy(descricao = novaDescricao) }
    }

    fun onDiaSemanaToggle(dia: Int) {
        _uiState.update { state ->
            val novosDias = if (state.diasSemana.contains(dia)) {
                state.diasSemana - dia
            } else {
                state.diasSemana + dia
            }
            state.copy(diasSemana = novosDias)
        }
    }

    fun onEdit(habito: Habito) {
        _uiState.update {
            it.copy(
                habitoEmEdicao = habito,
                nome = habito.nome,
                descricao = habito.descricao,
                diasSemana = habito.diasSemana.split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .toSet()
            )
        }
    }

    fun onDelete(habito: Habito) {
        viewModelScope.launch {
            repository.deletar(habito)
        }
    }

    fun onToggleConcluido(habitoId: Int) {
        viewModelScope.launch {
            val habito = repository.buscarPorId(habitoId) ?: return@launch
            val atualizado = habito.copy(concluidoHoje = !habito.concluidoHoje)
            repository.atualizar(atualizado)
        }
    }

    fun onSave() {
        val state = _uiState.value
        if (state.nome.isBlank()) return

        val diasString = state.diasSemana.sorted().joinToString(",")

        val habitoParaSalvar = state.habitoEmEdicao?.copy(
            nome = state.nome,
            descricao = state.descricao,
            diasSemana = diasString
        ) ?: Habito(
            nome = state.nome,
            descricao = state.descricao,
            diasSemana = diasString
        )

        viewModelScope.launch {
            if (state.habitoEmEdicao == null) {
                repository.inserir(habitoParaSalvar)
            } else {
                repository.atualizar(habitoParaSalvar)
            }
            limparCampos()
        }
    }

    fun limparCampos() {
        _uiState.update {
            it.copy(nome = "", descricao = "", diasSemana = emptySet(), habitoEmEdicao = null)
        }
    }

    suspend fun buscarPorId(id: Int): Habito? {
        return repository.buscarPorId(id)
    }

    fun calcularTaxaConclusao(): Float {
        val lista = _uiState.value.listaDeHabitos
        if (lista.isEmpty()) return 0f
        val concluidos = lista.count { it.concluidoHoje }
        return concluidos.toFloat() / lista.size.toFloat()
    }
}