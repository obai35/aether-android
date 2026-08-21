package com.aether.companion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aether.companion.data.repository.FreelancerRepository
import com.aether.companion.ui.viewmodel.FreelancerViewModel
import com.aether.companion.ui.screens.DashboardScreen
import com.aether.companion.ui.screens.JobDetailScreen
import com.aether.companion.ui.screens.JobsScreen
import com.aether.companion.ui.screens.AutomationScreen
import com.aether.companion.ui.screens.AssistantScreen
import com.aether.companion.ui.screens.SettingsScreen

class MainActivity : ComponentActivity() {
    private val viewModel: FreelancerViewModel by viewModels(
        factoryProducer = {
            val repository = FreelancerRepository(this@MainActivity, lifecycleScope)
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FreelancerViewModel(repository) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(viewModel)
                }
            }
        }
    }

    @Composable
    fun AppNavHost(viewModel: FreelancerViewModel) {
        val navController = rememberNavController()
        val backStackEntry = navController.currentBackStackEntry

        NavHost(navController, startDestination = "dashboard") {
            composable("dashboard") {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToJobs = { navController.navigate("jobs") },
                    onNavigateToAutomation = { navController.navigate("automation") },
                    onNavigateToAssistant = { navController.navigate("assistant") }
                )
            }
            composable("jobs") {
                JobsScreen(
                    viewModel = viewModel,
                    onNavigateToJob = { jobId -> navController.navigate("job/$jobId") }
                )
            }
            composable(
                route = "job/{jobId}",
                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
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