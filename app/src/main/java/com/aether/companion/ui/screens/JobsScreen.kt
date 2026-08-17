package com.aether.companion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aether.companion.R
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.ui.viewmodel.FreelancerViewModel

@Composable
fun JobsScreen(
    viewModel: FreelancerViewModel = viewModel(),
    onNavigateToJob: (String) -> Unit = {}
) {
    val jobs by viewModel.jobs.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var filterStatus by remember { mutableStateOf<FreelancerJob.JobStatus?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text("All Jobs") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Filter Chips
        FilterChips(
            selectedStatus = filterStatus,
            onStatusClick = { status ->
                filterStatus = status
                viewModel.refreshJobs()
            }
        )

        // Jobs List
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(jobs.filter { filterStatus == null || it.status == filterStatus }) { job ->
                JobCard(job = job, onClick = { onNavigateToJob(job.id) })
            }
        }

        if (jobs.filter { filterStatus == null || it.status == filterStatus }.isEmpty()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_search_off), null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("No jobs found", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Text("Try changing the filter or start a new mission")
                }
            }
        }
    }
}

@Composable
fun FilterChips(
    selectedStatus: FreelancerJob.JobStatus?,
    onStatusClick: (FreelancerJob.JobStatus?) -> Unit
) {
    val statuses = listOf<FreelancerJob.JobStatus?>(
        null,
        FreelancerJob.JobStatus.PENDING,
        FreelancerJob.JobStatus.SEARCHING,
        FreelancerJob.JobStatus.WORKING,
        FreelancerJob.JobStatus.AWAITING_APPROVAL,
        FreelancerJob.JobStatus.IMPLEMENTED,
        FreelancerJob.JobStatus.DELIVERED,
        FreelancerJob.JobStatus.FAILED
    )

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        statuses.forEach { status ->
            androidx.material3.FilterChip(
                selected = (selectedStatus == status),
                onClick = { onStatusClick(status) },
                label = { Text(status?.value?.capitalize() ?: "All") },
                colors = androidx.material3.FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    unselectedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
        }
    }
}

@Composable
fun JobCard(
    job: FreelancerJob,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when (job.status) {
                FreelancerJob.JobStatus.IMPLEMENTED -> Color.Green.copy(alpha = 0.1f)
                FreelancerJob.JobStatus.FAILED -> Color.Red.copy(alpha = 0.1f)
                FreelancerJob.JobStatus.AWAITING_APPROVAL -> Color.Orange.copy(alpha = 0.1f)
                FreelancerJob.JobStatus.REQUIRES_HUMAN -> Color.Red.copy(alpha = 0.1f)
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
            Text(job.requirements, fontSize = 12.sp, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Lang: ${job.language}", fontSize = 12.sp)
                Text("Score: ${job.skillScore?.let { "%.0f%%".format(it * 100) } ?: "N/A"}", fontSize = 12.sp)
                Text("Attempts: ${job.attempts}", fontSize = 12.sp)
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
    androidx.compose.material3.Chip(
        onClick = {},
        colors = androidx.material3.ChipDefaults.chipColors(containerColor = color),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 10.sp)
    }
}