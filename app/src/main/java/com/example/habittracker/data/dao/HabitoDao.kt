package com.example.habittracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habittracker.data.model.Habito

/**
 * DAO para operações de CRUD na tabela "habitos".
 * Todas as funções são suspend para rodar fora da thread principal.
 */
@Dao
interface HabitoDao {

    @Insert
    suspend fun inserir(habito: Habito)

    @Update
    suspend fun atualizar(habito: Habito)

    @Delete
    suspend fun deletar(habito: Habito)

    @Query("SELECT * FROM habitos ORDER BY id ASC")
    suspend fun buscarTodos(): List<Habito>

    // Consulta extra filtrada por ID
    @Query("SELECT * FROM habitos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Habito?
}