package com.aether.companion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aether.companion.R
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.ui.viewmodel.FreelancerViewModel
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    viewModel: FreelancerViewModel = viewModel(),
    onNavigateToJobs: () -> Unit = {},
    onNavigateToAssistant: () -> Unit = {},
    onNavigateToAutomation: () -> Unit = {}
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val jobs by viewModel.jobs.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Recent jobs (last 5)
    val recentJobs = jobs.take(5)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Aether Freelancer") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            actions = {
                IconButton(onClick = { onNavigateToAssistant() }) {
                    Icon(painterResource(R.drawable.ic_chat), "AI Assistant")
                }
                IconButton(onClick = { onNavigateToAutomation() }) {
                    Icon(painterResource(R.drawable.ic_auto), "Automation")
                }
            }
        )

        // Connection Status
        ConnectionStatusCard(isConnected)

        // Stats Cards
        if (stats != null) {
            StatsGrid(stats)
        }

        // Quick Actions
        QuickActionsCard(
            onViewJobs = onNavigateToJobs,
            onStartMission = { /* Trigger auto mission */ }
        )

        // Recent Jobs
        if (recentJobs.isNotEmpty()) {
            RecentJobsSection(recentJobs)
        } else {
            EmptyStateCard()
        }

        // Pending Human Action Banner
        uiState.let { state ->
            if (state is com.aether.companion.ui.viewmodel.FreelancerViewModel.UIState.HumanActionRequired) {
                HumanActionBanner(state.event) {
                    viewModel.clearPendingHumanAction()
                }
            }
        }
    }
}

@Composable
fun ConnectionStatusCard(isConnected: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) Color.Green.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f)
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            androidx.compose.ui.graphics.vector.ImageVector.vectorResource(id = if (isConnected) R.drawable.ic_check_circle else R.drawable.ic_error).let { icon ->
                Icon(icon, null, tint = if (isConnected) Color.Green else Color.Red)
            }
            Text(
                text = if (isConnected) "Connected to Aether Backend" else "Disconnected - Retrying...",
                color = if (isConnected) Color.Green else Color.Red,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatsGrid(stats: com.aether.companion.data.api.FreelancerStats) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("Total Jobs", stats.totalJobs.toString(), R.drawable.ic_work)
            StatCard("Completed", stats.completedJobs.toString(), R.drawable.ic_check)
        }
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("Pending", stats.pendingJobs.toString(), R.drawable.ic_clock)
            StatCard("Earnings", "\$${stats.totalEarnings}", R.drawable.ic_money)
        }
    }
}

@Composable
fun StatCard(title: String, value: String, iconRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(iconRes), null, tint = MaterialTheme.colorScheme.primary)
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun QuickActionsCard(
    onViewJobs: () -> Unit,
    onStartMission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Quick Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onViewJobs
                ) {
                    Text("View All Jobs")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onStartMission
                ) {
                    Text("Start Auto Mission")
                }
            }
        }
    }
}

@Composable
fun RecentJobsSection(jobs: List<FreelancerJob>) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Recent Jobs", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(jobs) { job ->
                JobCard(job)
            }
        }
    }
}

@Composable
fun JobCard(job: FreelancerJob) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (job.status) {
                FreelancerJob.JobStatus.IMPLEMENTED -> Color.Green.copy(alpha = 0.1f)
                FreelancerJob.JobStatus.FAILED -> Color.Red.copy(alpha = 0.1f)
                FreelancerJob.JobStatus.AWAITING_APPROVAL -> Color.Orange.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surfaceContainerHigh
            }
        )
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(job.title, fontSize = 16.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                JobStatusChip(job.status)
            }
            Text(job.platform, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Lang: ${job.language}", fontSize = 12.sp)
                Text("Score: ${job.skillScore?.let { "%.0f%%".format(it * 100) } ?: "N/A"}", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun JobStatusChip(status: FreelancerJob.JobStatus) {
    val (color, text) = when (status) {
        FreelancerJob.JobStatus.PENDING -> Color.Gray to "Pending"
        FreelancerJob.JobStatus.SEARCHING -> Color.Blue to "Searching"
        FreelancerJob.JobStatus.WORKING -> Color.Blue to "Working"
        FreelancerJob.JobStatus.AWAITING_APPROVAL -> Color.Orange to "Awaiting Approval"
        FreelancerJob.JobStatus.IMPLEMENTED -> Color.Green to "Implemented"
        FreelancerJob.JobStatus.DELIVERED -> Color.Purple to "Delivered"
        FreelancerJob.JobStatus.FAILED -> Color.Red to "Failed"
        FreelancerJob.JobStatus.NEEDS_FIXES -> Color.Red to "Needs Fixes"
        FreelancerJob.JobStatus.QUALITY_GATE_FAILED -> Color.Red to "Quality Failed"
        FreelancerJob.JobStatus.REQUIRES_HUMAN -> Color.Red to "Action Required"
    }
    androidx.compose.material3.Text(
        text = text,
        color = Color.White,
        fontSize = 10.sp,
        modifier = androidx.compose.material3.Chip(
            onClick = {},
            colors = androidx.material3.ChipDefaults.chipColors(containerColor = color),
            modifier = Modifier.padding(4.dp)
        ).modifier
    )
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(painterResource(R.drawable.ic_work_off), null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("No jobs yet", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text("Start an auto mission to find and execute freelance jobs", textAlign = TextAlign.Center)
            Button(onClick = { /* start mission */ }) {
                Text("Start Auto Mission")
            }
        }
    }
}

@Composable
fun HumanActionBanner(
    event: AutomationEvent,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Orange.copy(alpha = 0.1f))
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_warning), null, tint = Color.Orange)
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Action Required", fontWeight = FontWeight.Bold)
                Text((event.data as? AutomationEvent.EventData.HumanRequiredData)?.reason ?: "Human interaction needed")
            }
            Button(onClick = { /* navigate to job */ }) {
                Text("Review")
            }
            IconButton(onClick = onDismiss) {
                Icon(painterResource(R.drawable.ic_close), "Dismiss")
            }
        }
    }
}