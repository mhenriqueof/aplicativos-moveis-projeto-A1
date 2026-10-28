package com.example.habittracker.ui.conquistas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.data.repository.ConquistaRepository

class ConquistaViewModelFactory(
    private val repository: ConquistaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConquistaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConquistaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}