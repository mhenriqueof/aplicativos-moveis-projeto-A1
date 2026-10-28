package com.example.habittracker.data.repository

import com.example.habittracker.data.local.Conquista
import com.example.habittracker.data.local.ConquistaDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository de Conquistas — única porta de entrada para os dados de conquistas.
 */
class ConquistaRepository(private val conquistaDao: ConquistaDao) {

    fun getAllConquistas(): Flow<List<Conquista>> = conquistaDao.buscarTodos()

    suspend fun inserir(conquista: Conquista) {
        conquistaDao.inserir(conquista)
    }

    suspend fun atualizar(conquista: Conquista) {
        conquistaDao.atualizar(conquista)
    }

    suspend fun deletar(conquista: Conquista) {
        conquistaDao.deletar(conquista)
    }

    suspend fun buscarPorId(id: Int): Conquista? {
        return conquistaDao.buscarPorId(id)
    }
}