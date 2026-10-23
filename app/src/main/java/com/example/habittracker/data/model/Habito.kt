package com.example.habittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room representando um hábito.
 * Vira a tabela "habitos" no banco SQLite.
 */
@Entity(tableName = "habitos")
data class Habito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nome: String,
    val descricao: String = "",
    val diasSemana: String = "", // Room não salva Set<Int> direto — usamos String "1,2,3"
    val concluidoHoje: Boolean = false
)