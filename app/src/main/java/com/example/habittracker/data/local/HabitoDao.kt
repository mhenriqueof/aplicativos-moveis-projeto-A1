package com.example.habittracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habittracker.data.local.Habito
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitoDao {

    @Insert
    suspend fun inserir(habito: Habito)

    @Update
    suspend fun atualizar(habito: Habito)

    @Delete
    suspend fun deletar(habito: Habito)

    // Agora retorna Flow — a lista se atualiza sozinha quando o banco muda
    @Query("SELECT * FROM habitos ORDER BY id ASC")
    fun buscarTodos(): Flow<List<Habito>>

    // Consulta extra filtrada por ID (requisito do Trabalho 3)
    @Query("SELECT * FROM habitos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Habito?
}