package com.aether.companion.data.api

import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.data.model.FreelancerJob.JobStatus
import com.aether.companion.data.model.QualityGateResult
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Response as RetrofitResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AetherApiService {
    @GET("/api/health")
    suspend fun healthCheck(): RetrofitResponse<HealthResponse>

    @GET("/api/freelancer/jobs")
    suspend fun getJobs(
        @Query("status") status: JobStatus? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): RetrofitResponse<List<FreelancerJob>>

    @GET("/api/freelancer/jobs/{jobId}")
    suspend fun getJob(@Path("jobId") jobId: String): RetrofitResponse<FreelancerJob>

    @POST("/api/freelancer/jobs/{jobId}/approve")
    suspend fun approveProposal(@Path("jobId") jobId: String): RetrofitResponse<ApproveResponse>

    @POST("/api/freelancer/jobs/{jobId}/deliver")
    suspend fun confirmDelivery(@Path("jobId") jobId: String): RetrofitResponse<DeliveryResponse>

    @GET("/api/freelancer/export/{jobId}")
    suspend fun exportPackage(@Path("jobId") jobId: String): RetrofitResponse<ExportResponse>

    @GET("/api/freelancer/export_arabic/{jobId}")
    suspend fun exportArabicPackage(@Path("jobId") jobId: String): RetrofitResponse<ExportResponse>

    @POST("/api/assistant/chat")
    suspend fun chatWithAssistant(@Body request: AIAssistantRequest): RetrofitResponse<AIAssistantResponse>

    @POST("/api/freelancer/auto_mission")
    suspend fun startAutoMission(@Body request: AutoMissionRequest): RetrofitResponse<AutoMissionResponse>

    @POST("/api/freelancer/jobs/{jobId}/quality_gate")
    suspend fun runQualityGate(@Path("jobId") jobId: String): RetrofitResponse<QualityGateResult>

    @POST("/api/settings")
    suspend fun updateSettings(@Body request: SettingsRequest): RetrofitResponse<SettingsResponse>
}

data class HealthResponse(
    val status: String
)

data class ApproveResponse(
    val success: Boolean,
    val message: String?
)

data class DeliveryResponse(
    val success: Boolean,
    val message: String?
)

data class ExportResponse(
    val success: Boolean,
    val downloadUrl: String?,
    val expiresAt: Long?
)

data class AutoMissionRequest(
    val query: String,
    val platforms: List<String>,
    val minBudget: Int,
    val maxBudget: Int,
    val qualityThreshold: Float,
    val autoDeliver: Boolean,
    val autoApprove: Boolean
)

data class AutoMissionResponse(
    val success: Boolean,
    val missionId: String?,
    val message: String?
)

data class SettingsRequest(
    val apiUrl: String,
    val apiKey: String
)

data class SettingsResponse(
    val success: Boolean,
    val message: String?
)

data class AIAssistantRequest(
    val message: String,
    val context: Map<String, Any>? = null
)

data class AIAssistantResponse(
    val response: String,
    val toolCalls: List<ToolCall>? = null
)

data class ToolCall(
    val name: String,
    val arguments: Map<String, Any>
)