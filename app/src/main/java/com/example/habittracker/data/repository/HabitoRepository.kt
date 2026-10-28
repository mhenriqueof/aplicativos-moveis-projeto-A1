package com.example.habittracker.data.repository

import com.example.habittracker.data.local.Habito
import com.example.habittracker.data.local.HabitoDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository de Hábitos — única porta de entrada para os dados de hábitos.
 * O ViewModel só fala com este Repository, nunca diretamente com o DAO.
 */
class HabitoRepository(private val habitoDao: HabitoDao) {

    fun getAllHabitos(): Flow<List<Habito>> = habitoDao.buscarTodos()

    suspend fun inserir(habito: Habito) {
        habitoDao.inserir(habito)
    }

    suspend fun atualizar(habito: Habito) {
        habitoDao.atualizar(habito)
    }

    suspend fun deletar(habito: Habito) {
        habitoDao.deletar(habito)
    }

    suspend fun buscarPorId(id: Int): Habito? {
        return habitoDao.buscarPorId(id)
    }
}