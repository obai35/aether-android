package com.aether.companion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.navArgument
import androidx.navigation.NavType
import com.aether.companion.R
import com.aether.companion.data.model.AutomationEvent
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.ui.viewmodel.FreelancerViewModel
import com.aether.companion.ui.screens.DashboardScreen
import com.aether.companion.ui.screens.JobDetailScreen
import com.aether.companion.ui.screens.JobsScreen
import com.aether.companion.ui.screens.AutomationScreen
import com.aether.companion.ui.screens.AssistantScreen
import com.aether.companion.ui.screens.SettingsScreen
import kotlinx.coroutines.launch
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    private val viewModel: FreelancerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "dashboard") {
                        composable("dashboard") {
                            DashboardScreen(
                                onNavigateToJobs = { navController.navigate("jobs") },
                                onNavigateToAssistant = { navController.navigate("assistant") },
                                onNavigateToAutomation = { navController.navigate("automation") }
                            )
                        }
                        composable(
                            route = "job_detail/{jobId}",
                            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val jobId = backStackEntry.getString()!!
                            JobDetailScreen(jobId = jobId)
                        }
                        composable("jobs") {
                            JobsScreen(
                                onNavigateToJob = { jobId ->
                                    navController.navigate("job_detail/$jobId")
                                }
                            )
                        }
                        composable("automation") {
                            AutomationScreen()
                        }
                        composable("assistant") {
                            AssistantScreen()
                        }
                        composable("settings") {
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}