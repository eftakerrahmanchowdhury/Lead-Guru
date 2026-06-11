package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.database.AppDatabase
import com.example.data.repository.GeminiRepository
import com.example.data.repository.LeadRepository
import com.example.ui.LeadViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainDashboard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Room Database
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "leads_intelligence_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // 2. Initialize Repositories
        val leadDao = database.leadDao()
        val leadRepository = LeadRepository(leadDao)
        val geminiRepository = GeminiRepository()

        // 3. Create ViewModel instance using robust provider factory
        val viewModel = ViewModelProvider(
            this,
            LeadViewModel.Factory(leadRepository, geminiRepository)
        )[LeadViewModel::class.java]

        setContent {
            MyApplicationTheme {
                var isLoggedIn by rememberSaveable { mutableStateOf(false) }

                if (isLoggedIn) {
                    MainDashboard(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize(),
                        onLogout = { isLoggedIn = false }
                    )
                } else {
                    LoginScreen(
                        onLoginSuccess = { username ->
                            isLoggedIn = true
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
