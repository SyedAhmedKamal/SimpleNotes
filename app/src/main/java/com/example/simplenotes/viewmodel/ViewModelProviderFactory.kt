package com.example.simplenotes.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplenotes.repository.NoteRepository

class ViewModelProviderFactory(
    val app: Application,
    private val repository: NoteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(app, repository) as T
        }
        throw IllegalArgumentException("Unknown view model")
    }
}