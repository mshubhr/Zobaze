package com.app.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.app.expensetracker.data.local.AppDatabase
import com.app.expensetracker.data.local.MIGRATION_1_2
import com.app.expensetracker.viewmodel.ExpenseRepository
import com.app.expensetracker.ui.AppNavGraph
import com.app.expensetracker.ui.theme.ExpenseTrackerTheme
import com.app.expensetracker.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "expense-db")
            .addMigrations(MIGRATION_1_2).build()
    }
    private val repo by lazy { ExpenseRepository(db.expenseDao()) }
    private val vm: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST") return MainViewModel(repo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val darkTheme by vm.darkTheme.collectAsState()
            ExpenseTrackerTheme(darkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(), contentWindowInsets = WindowInsets.statusBars
                ) { innerPadding ->
                    AppNavGraph(
                        vm, Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}