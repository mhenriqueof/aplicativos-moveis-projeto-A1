package com.example.habittracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database Room do app. Singleton.
 * Contém as 2 entities (Habito, Conquista) e os 2 DAOs.
 */
@Database(entities = [Habito::class, Conquista::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitoDao(): HabitoDao
    abstract fun conquistaDao(): ConquistaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habittracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}