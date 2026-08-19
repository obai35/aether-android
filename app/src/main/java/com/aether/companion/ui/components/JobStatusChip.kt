package com.aether.companion.ui.components

import androidx.compose.material3.Chip
import androidx.compose.material3.ChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.companion.data.model.FreelancerJob
import androidx.annotation.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobStatusChip(status: FreelancerJob.JobStatus) {
    val (label, color) = when (status) {
        FreelancerJob.JobStatus.PENDING -> "Pending" to MaterialTheme.colorScheme.secondary
        FreelancerJob.JobStatus.SEARCHING -> "Searching" to MaterialTheme.colorScheme.primary
        FreelancerJob.JobStatus.WORKING -> "Working" to MaterialTheme.colorScheme.tertiary
        FreelancerJob.JobStatus.AWAITING_APPROVAL -> "Awaiting Approval" to MaterialTheme.colorScheme.secondary
        FreelancerJob.JobStatus.IMPLEMENTED -> "Implemented" to MaterialTheme.colorScheme.primary
        FreelancerJob.JobStatus.DELIVERED -> "Delivered" to MaterialTheme.colorScheme.tertiary
        FreelancerJob.JobStatus.FAILED -> "Failed" to MaterialTheme.colorScheme.error
        FreelancerJob.JobStatus.NEEDS_FIXES -> "Needs Fixes" to MaterialTheme.colorScheme.secondary
        FreelancerJob.JobStatus.QUALITY_GATE_FAILED -> "Quality Gate Failed" to MaterialTheme.colorScheme.error
        FreelancerJob.JobStatus.REQUIRES_HUMAN -> "Requires Human" to MaterialTheme.colorScheme.error
        else -> status.name to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Chip(
        onClick = {},
        modifier = Modifier.wrapContentSize(),
        enabled = false,
        colors = ChipDefaults.chipColors(
            containerColor = color.copy(alpha = 0.2f)
        )
    ) {
        Text(label, fontSize = 12.sp, color = color)
    }
}