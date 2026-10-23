package com.example.habittracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habittracker.data.model.Conquista

/**
 * DAO para operações de CRUD na tabela "conquistas".
 * Todas as funções são suspend para rodar fora da thread principal.
 */
@Dao
interface ConquistaDao {

    @Insert
    suspend fun inserir(conquista: Conquista)

    @Update
    suspend fun atualizar(conquista: Conquista)

    @Delete
    suspend fun deletar(conquista: Conquista)

    @Query("SELECT * FROM conquistas ORDER BY id ASC")
    suspend fun buscarTodos(): List<Conquista>

    // Consulta extra filtrada por ID
    @Query("SELECT * FROM conquistas WHERE id = :id")
    suspend fun buscarPorId(id: Int): Conquista?
}