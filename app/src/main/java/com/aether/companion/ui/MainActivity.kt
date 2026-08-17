package com.aether.companion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aether.companion.R
import com.aether.companion.data.model.AutomationEvent
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.ui.viewmodel.FreelancerViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: FreelancerViewModel by hiltViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "dashboard") {
                        composable("dashboard") {
                            DashboardScreen(viewModel, navController)
                        }
                        composable("jobs") {
                            JobsScreen(viewModel, navController)
                        }
                        composable("job/{jobId}") {
                            val jobId = it.getString()!!
                            JobDetailScreen(viewModel, navController, jobId)
                        }
                        composable("assistant") {
                            AssistantScreen(viewModel, navController)
                        }
                        composable("automation") {
                            AutomationScreen(viewModel, navController)
                        }
                        composable("settings") {
                            SettingsScreen(viewModel, navController)
                        }
                    }
                }
            }
        }
    }

    // Navigation observer for human-required events
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.pendingHumanAction.collect { event ->
                event?.let {
                    // Show bottom sheet or dialog for human action
                    // This will be handled by the UI state
                }
            }
        }
    }
}