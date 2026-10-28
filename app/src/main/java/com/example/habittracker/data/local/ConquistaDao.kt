package com.example.habittracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habittracker.data.local.Conquista
import kotlinx.coroutines.flow.Flow

@Dao
interface ConquistaDao {

    @Insert
    suspend fun inserir(conquista: Conquista)

    @Update
    suspend fun atualizar(conquista: Conquista)

    @Delete
    suspend fun deletar(conquista: Conquista)

    // Agora retorna Flow
    @Query("SELECT * FROM conquistas ORDER BY id ASC")
    fun buscarTodos(): Flow<List<Conquista>>

    // Consulta extra filtrada por ID
    @Query("SELECT * FROM conquistas WHERE id = :id")
    suspend fun buscarPorId(id: Int): Conquista?
}