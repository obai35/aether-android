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

@Composable
fun DashboardScreen(
    viewModel: FreelancerViewModel = viewModel(),
    onNavigateToJobs: () -> Unit = {},
    onNavigateToAssistant: () -> Unit = {},
    onNavigateToAutomation: () -> Unit = {}
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recentJobs by viewModel.jobs.collectAsStateWithLifecycle()
        .map { it.take(5) }

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
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Jobs",
                        value = stats.totalJobs.toString(),
                        icon = Icons.Default.Work,
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatCard(
                        title = "Active",
                        value = stats.activeJobs.toString(),
                        icon = Icons.Default.PlayCircle,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    StatCard(
                        title = "Delivered",
                        value = stats.deliveredJobs.toString(),
                        icon = Icons.Default.CheckCircle,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                // Quick Actions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ActionButton(
                                text = "Browse Jobs",
                                icon = Icons.Default.Search,
                                onClick = onNavigateToJobs,
                                modifier = Modifier.weight(1f)
                            )
                            ActionButton(
                                text = "Auto Mission",
                                icon = Icons.Default.AutoAwesome,
                                onClick = onNavigateToAutomation,
                                modifier = Modifier.weight(1f)
                            )
                            ActionButton(
                                text = "AI Assistant",
                                icon = Icons.Default.SmartToy,
                                onClick = onNavigateToAssistant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Recent Jobs
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Recent Jobs", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            TextButton(onClick = onNavigateToJobs) {
                                Text("View All")
                            }
                        }
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

                        if (recentJobs.isEmpty()) {
                            Text(
                                "No jobs yet. Start an auto mission or browse jobs!",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 8.dp)
                            ) {
                                items(recentJobs) { job ->
                                    JobSummaryCard(job = job) {
                                        // Navigation handled by parent
                                    }
                                }
                            }
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
    icon: androidx.compose.material.icons.filled.Icon?,
    color: Color
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon!!, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.material.icons.filled.Icon?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon!!, contentDescription = text, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
            Text(text, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun JobSummaryCard(
    job: FreelancerJob,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(job.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
                JobStatusChip(status = job.status)
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(job.platform, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
            Text("\$${job.minBudget} - \$${job.maxBudget}", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun JobStatusChip(status: FreelancerJob.JobStatus) {
    val (color, text) = when (status) {
        FreelancerJob.JobStatus.PENDING -> MaterialTheme.colorScheme.outline to "Pending"
        FreelancerJob.JobStatus.SEARCHING -> MaterialTheme.colorScheme.primary to "Searching"
        FreelancerJob.JobStatus.WORKING -> MaterialTheme.colorScheme.secondary to "Working"
        FreelancerJob.JobStatus.AWAITING_APPROVAL -> MaterialTheme.colorScheme.tertiary to "Awaiting Approval"
        FreelancerJob.JobStatus.IMPLEMENTED -> MaterialTheme.colorScheme.primary to "Implemented"
        FreelancerJob.JobStatus.DELIVERED -> MaterialTheme.colorScheme.tertiary to "Delivered"
        FreelancerJob.JobStatus.FAILED -> MaterialTheme.colorScheme.error to "Failed"
        FreelancerJob.JobStatus.COMPLETED -> MaterialTheme.colorScheme.primary to "Completed"
    }

    androidx.compose.material3.Chip(
        modifier = Modifier.wrapContentSize(),
        onClick = {},
        label = { Text(text, color = MaterialTheme.colorScheme.onSurface, fontSize = 10.sp) },
        colors = androidx.compose.material3.ChipDefaults.colors(
            containerColor = color.copy(alpha = 0.2f)
        )
    )
}