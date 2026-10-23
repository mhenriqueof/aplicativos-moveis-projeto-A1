package com.example.habittracker.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.AppDatabase
import com.example.habittracker.data.dao.ConquistaDao
import com.example.habittracker.data.dao.HabitoDao
import com.example.habittracker.data.model.Conquista
import com.example.habittracker.data.model.Habito
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel compartilhado entre todas as telas.
 * Agora usa Room (banco SQLite) em vez de mutableStateListOf em memória.
 * Os dados sobrevivem ao fechar o app.
 */
class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val habitoDao: HabitoDao = AppDatabase.getDatabase(application).habitoDao()
    private val conquistaDao: ConquistaDao = AppDatabase.getDatabase(application).conquistaDao()

    // Estado reativo das listas - atualizado quando o banco muda
    val habitos = mutableStateOf<List<Habito>>(emptyList())
    val conquistas = mutableStateOf<List<Conquista>>(emptyList())

    init {
        // Carrega os dados do banco assim que o ViewModel é criado
        carregarHabitos()
        carregarConquistas()
    }

    // --- Funções para Hábitos ---

    fun carregarHabitos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lista = habitoDao.buscarTodos()
                withContext(Dispatchers.Main) {
                    habitos.value = lista
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun adicionarHabito(nome: String, diasSemana: Set<Int>, descricao: String) {
        if (nome.isBlank()) return
        // Converte Set<Int> para String "1,2,3" para salvar no Room
        val diasString = diasSemana.sorted().joinToString(",")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                habitoDao.inserir(
                    Habito(
                        nome = nome,
                        descricao = descricao,
                        diasSemana = diasString
                    )
                )
                carregarHabitos() // Recarrega a lista após inserir
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun atualizarHabito(habito: Habito) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                habitoDao.atualizar(habito)
                carregarHabitos()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removerHabito(habito: Habito) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                habitoDao.deletar(habito)
                carregarHabitos()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleHabitoConcluido(habitoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val habito = habitoDao.buscarPorId(habitoId) ?: return@launch
                val atualizado = habito.copy(concluidoHoje = !habito.concluidoHoje)
                habitoDao.atualizar(atualizado)
                carregarHabitos()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun buscarHabitoPorId(id: Int): Habito? {
        return withContext(Dispatchers.IO) {
            try {
                habitoDao.buscarPorId(id)
            } catch (e: Exception) {
                null
            }
        }
    }

    // --- Funções para Conquistas ---

    fun carregarConquistas() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lista = conquistaDao.buscarTodos()
                withContext(Dispatchers.Main) {
                    conquistas.value = lista
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleConquistaDesbloqueada(conquistaId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val conquista = conquistaDao.buscarPorId(conquistaId) ?: return@launch
                val atualizada = conquista.copy(desbloqueada = !conquista.desbloqueada)
                conquistaDao.atualizar(atualizada)
                carregarConquistas()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun buscarConquistaPorId(id: Int): Conquista? {
        return withContext(Dispatchers.IO) {
            try {
                conquistaDao.buscarPorId(id)
            } catch (e: Exception) {
                null
            }
        }
    }

    // --- Estatísticas calculadas (complexidade extra para tela de detalhes) ---

    fun calcularTaxaConclusao(): Float {
        val lista = habitos.value
        if (lista.isEmpty()) return 0f
        val concluidos = lista.count { it.concluidoHoje }
        return concluidos.toFloat() / lista.size.toFloat()
    }

    fun totalConquistasDesbloqueadas(): Int {
        return conquistas.value.count { it.desbloqueada }
    }

    fun adicionarConquista(titulo: String, descricao: String) {
        if (titulo.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                conquistaDao.inserir(
                    Conquista(
                        titulo = titulo,
                        descricao = descricao
                    )
                )
                carregarConquistas() // Recarrega a lista após inserir
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removerConquista(conquista: Conquista) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                conquistaDao.deletar(conquista)
                carregarConquistas()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun atualizarConquista(conquista: Conquista) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                conquistaDao.atualizar(conquista)
                carregarConquistas()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}