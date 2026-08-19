package com.aether.companion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aether.companion.R
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.ui.viewmodel.FreelancerViewModel
import kotlinx.coroutines.launch
import androidx.annotation.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api

@file:OptIn(ExperimentalMaterial3Api::class)

@Composable
fun DashboardScreen(
    viewModel: FreelancerViewModel = viewModel(),
    onNavigateToJobs: () -> Unit = {},
    onNavigateToAssistant: () -> Unit = {},
    onNavigateToAutomation: () -> Unit = {}
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val jobsList by viewModel.jobs.collectAsStateWithLifecycle()
    val recentJobs = jobsList.take(5)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text("Aether Freelancer") },
            actions = {
                IconButton(onClick = onNavigateToAssistant) {
                    Icon(Icons.Default.SmartToy, contentDescription = "AI Assistant")
                }
                IconButton(onClick = onNavigateToAutomation) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Auto Mission")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )

        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stats Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Jobs",
                        value = stats?.totalJobs.toString() ?: "0",
                        icon = Icons.Default.Work,
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatCard(
                        title = "Completed",
                        value = stats?.completedJobs.toString() ?: "0",
                        icon = Icons.Default.CheckCircle,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    StatCard(
                        title = "Pending",
                        value = stats?.pendingJobs.toString() ?: "0",
                        icon = Icons.Default.HourglassTop,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Failed",
                        value = stats?.failedJobs.toString() ?: "0",
                        icon = Icons.Default.Error,
                        color = MaterialTheme.colorScheme.error
                    )
                    StatCard(
                        title = "Earnings",
                        value = "\$${String.format("%.0f", stats?.totalEarnings ?: 0.0)}",
                        icon = Icons.Default.AttachMoney,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Recent Jobs Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recent Jobs", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TextButton(onClick = onNavigateToJobs) {
                        Text("View All")
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentJobs) { job ->
                        JobSummaryCard(job = job)
                    }
                }

                if (recentJobs.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Work, contentDescription = "No jobs", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), size = 48.dp)
                            Spacer(modifier = Modifier.padding(top = 16.dp))
                            Text("No jobs yet", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.padding(top = 8.dp))
                            Text("Start an auto mission or check back later", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                size = 28.dp
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.padding(top = 4.dp))
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun JobSummaryCard(job: FreelancerJob) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(job.title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                JobStatusChip(status = job.status)
            }
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(job.platform, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Text("\$" + String.format("%.0f", job.minBudget.toDouble()), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun JobStatusChip(status: FreelancerJob.JobStatus) {
    val (label, color) = when (status) {
        FreelancerJob.JobStatus.PENDING -> "Pending" to MaterialTheme.colorScheme.secondary
        FreelancerJob.JobStatus.SEARCHING -> "Searching" to MaterialTheme.colorScheme.primary
        FreelancerJob.JobStatus.WORKING -> "Working" to MaterialTheme.colorScheme.tertiary
        FreelancerJob.JobStatus.AWAITING_APPROVAL -> "Awaiting Approval" to MaterialTheme.colorScheme.warning
        FreelancerJob.JobStatus.IMPLEMENTED -> "Implemented" to MaterialTheme.colorScheme.primary
        FreelancerJob.JobStatus.DELIVERED -> "Delivered" to MaterialTheme.colorScheme.tertiary
        FreelancerJob.JobStatus.FAILED -> "Failed" to MaterialTheme.colorScheme.error
        FreelancerJob.JobStatus.NEEDS_FIXES -> "Needs Fixes" to MaterialTheme.colorScheme.warning
        FreelancerJob.JobStatus.QUALITY_GATE_FAILED -> "Quality Gate Failed" to MaterialTheme.colorScheme.error
        FreelancerJob.JobStatus.REQUIRES_HUMAN -> "Requires Human" to MaterialTheme.colorScheme.error
        else -> status.name to MaterialTheme.colorScheme.onSurfaceVariant
    }

    androidx.compose.material3.Chip(
        onClick = {},
        colors = androidx.compose.material3.ChipDefaults.chipColors(
            containerColor = color.copy(alpha = 0.2f)
        )
    ) {
        Text(label, fontSize = 12.sp, color = color)
    }
}