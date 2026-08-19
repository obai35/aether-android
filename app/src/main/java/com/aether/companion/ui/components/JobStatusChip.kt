package com.aether.companion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.companion.data.model.FreelancerJob

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

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}