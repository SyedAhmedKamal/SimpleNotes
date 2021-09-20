package com.example.simplenotes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.simplenotes.R
import com.example.simplenotes.database.NoteDatabase
import com.example.simplenotes.databinding.ActivityMainBinding
import com.example.simplenotes.repository.NoteRepository
import com.example.simplenotes.viewmodel.NoteViewModel
import com.example.simplenotes.viewmodel.ViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        val repository = NoteRepository(
            NoteDatabase(this)
        )

        val viewModelProviderFactory = ViewModelProviderFactory(
            application,
            repository
        )

        noteViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        ).get(NoteViewModel::class.java)

    }
}