package com.example.habittracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room representando uma conquista.
 * Vira a tabela "conquistas" no banco SQLite.
 */
@Entity(tableName = "conquistas")
data class Conquista(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val titulo: String,
    val descricao: String = "",
    val desbloqueada: Boolean = false,
    val icone: String = "🏅"
)