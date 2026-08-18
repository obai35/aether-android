package com.aether.companion.data.repository

import com.aether.companion.data.api.AetherApiService
import com.aether.companion.data.model.AutomationEvent
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.data.model.FreelancerJob.JobStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import retrofit2.Response

class FreelancerRepository(
    private val apiService: AetherApiService
) {
    private val _jobs = MutableStateFlow<List<FreelancerJob>>(emptyList())
    val jobs = _jobs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _automationEvents = MutableStateFlow<List<AutomationEvent>>(emptyList())
    val automationEvents = _automationEvents.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _currentEvent = MutableStateFlow<AutomationEvent?>(null)
    val currentEvent = _currentEvent.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _stats = MutableStateFlow<FreelancerStats?>(null)
    val stats = _stats.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob())

    suspend fun refreshJobs(status: JobStatus? = null) {
        try {
            val response = apiService.getJobs(status = status)
            if (response.isSuccessful && response.body() != null) {
                _jobs.value = response.body()!!
            }
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun getJob(jobId: String): FreelancerJob? {
        try {
            val response = apiService.getJob(jobId)
            return if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun approveProposal(jobId: String): Boolean {
        try {
            val response = apiService.approveProposal(jobId)
            return response.isSuccessful
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun confirmDelivery(jobId: String): Boolean {
        try {
            val response = apiService.confirmDelivery(jobId)
            return response.isSuccessful
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun exportPackage(jobId: String): String? {
        try {
            val response = apiService.exportPackage(jobId)
            return if (response.isSuccessful) response.body()?.downloadUrl else null
        } catch (e: Exception) {
            return null
        }
    }

    data class FreelancerStats(
        val totalJobs: Int,
        val completedJobs: Int,
        val pendingJobs: Int,
        val failedJobs: Int,
        val totalEarnings: Double
    )

    data class ExportResponse(
        val success: Boolean,
        val downloadUrl: String?,
        val expiresAt: Long?
    )

    data class AutoMissionResult(
        val success: Boolean,
        val missionId: String?,
        val message: String?
    )
}