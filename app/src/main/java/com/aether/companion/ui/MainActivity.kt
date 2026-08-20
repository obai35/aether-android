package com.aether.companion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aether.companion.R
import com.aether.companion.data.api.NetworkModule
import com.aether.companion.data.model.AutomationEvent
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.data.repository.FreelancerRepository
import com.aether.companion.ui.viewmodel.FreelancerViewModel
import com.aether.companion.ui.screens.DashboardScreen
import com.aether.companion.ui.screens.JobDetailScreen
import com.aether.companion.ui.screens.JobsScreen
import com.aether.companion.ui.screens.AutomationScreen
import com.aether.companion.ui.screens.AssistantScreen
import com.aether.companion.ui.screens.SettingsScreen

class MainActivity : ComponentActivity() {
    private val viewModel: FreelancerViewModel by viewModels {
        ViewModelProvider.Factory { modelClass ->
            val repository = FreelancerRepository(this@MainActivity, lifecycleScope)
            FreelancerViewModel(repository)
        }
    }
    private var isConnected = false
    private var shouldAutoConnect = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "dashboard") {
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToJobs = { navController.navigate("jobs") },
                                onNavigateToAssistant = { navController.navigate("assistant") },
                                onNavigateToAutomation = { navController.navigate("automation") }
                            )
                        }
                        composable("jobs") {
                            JobsScreen(
                                viewModel = viewModel,
                                onNavigateToJob = { jobId -> navController.navigate("job_detail/$jobId") }
                            )
                        }
                        composable(
                            route = "job_detail/{jobId}",
                        ) { backStackEntry ->
                            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                            JobDetailScreen(
                                viewModel = viewModel,
                                jobId = jobId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("automation") {
                            AutomationScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("assistant") {
                            AssistantScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}