package com.example.simplenotes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.simplenotes.R
import com.example.simplenotes.database.NoteDatabase
import com.example.simplenotes.databinding.ActivityMainBinding
import com.example.simplenotes.repository.NoteRepository
import com.example.simplenotes.viewmodel.NoteViewModel
import com.example.simplenotes.viewmodel.ViewModelProviderFactory
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = "My notes"

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


        NavigationUI.setupActionBarWithNavController(
            this, (supportFragmentManager
                .findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController
        )

    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp()
    }
}