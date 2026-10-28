package com.example.habittracker.ui.habitos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.data.repository.HabitoRepository

class HabitoViewModelFactory(
    private val repository: HabitoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}