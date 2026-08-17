package com.aether.companion.data.api

import com.aether.companion.data.model.AIAssistant.AIAssistantRequest
import com.aether.companion.data.model.AIAssistant.AIAssistantResponse
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.data.model.FreelancerJob.JobStatus
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AetherApiService {
    @GET("/api/health")
    suspend fun healthCheck(): Response<HealthResponse>

    @GET("/api/freelancer/jobs")
    suspend fun getJobs(
        @Query("status") status: JobStatus? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<List<FreelancerJob>>

    @GET("/api/freelancer/jobs/{jobId}")
    suspend fun getJob(@Path("jobId") jobId: String): Response<FreelancerJob>

    @POST("/api/freelancer/jobs/{jobId}/approve")
    suspend fun approveProposal(@Path("jobId") jobId: String): Response<ApproveResponse>

    @POST("/api/freelancer/jobs/{jobId}/deliver")
    suspend fun confirmDelivery(@Path("jobId") jobId: String): Response<DeliveryResponse>

    @GET("/api/freelancer/export/{jobId}")
    suspend fun exportPackage(@Path("jobId") jobId: String): Response<ExportResponse>

    @GET("/api/freelancer/export_arabic/{jobId}")
    suspend fun exportArabicPackage(@Path("jobId") jobId: String): Response<ExportResponse>

    @POST("/api/assistant/chat")
    suspend fun chatWithAssistant(@Body request: AIAssistantRequest): Response<AIAssistantResponse>

    @GET("/api/freelancer/stats")
    suspend fun getStats(): Response<FreelancerStats>

    @POST("/api/freelancer/auto_mission")
    suspend fun triggerAutoMission(@Body request: AutoMissionRequest): Response<AutoMissionResponse>
}

data class HealthResponse(
    val status: String,
    val version: String,
    val timestamp: Long
)

data class ApproveResponse(
    val success: Boolean,
    val message: String
)

data class DeliveryResponse(
    val success: Boolean,
    val message: String,
    val deliverableUrl: String?
)

data class ExportResponse(
    val success: Boolean,
    val packagePath: String?,
    val packageName: String?,
    val sizeBytes: Long?
)

data class FreelancerStats(
    val totalJobs: Int,
    val completedJobs: Int,
    val pendingJobs: Int,
    val failedJobs: Int,
    val totalEarnings: Double,
    val avgQualityScore: Double
)

data class AutoMissionRequest(
    val query: String,
    val platforms: List<String>,
    val skills: List<String>,
    val maxJobs: Int,
    val maxProposals: Int,
    val language: String,
    val qualityThreshold: String,
    val autoDeliver: Boolean
)

data class AutoMissionResponse(
    val success: Boolean,
    val missionId: String,
    val message: String
)

// WebSocket Client
class AetherWebSocketClient(
    private val baseUrl: String,
    private val apiKey: String,
    private val eventCallback: (AutomationEvent) -> Unit
) {
    private var webSocket: WebSocket? = null
    private var isConnected = false
    private val client = OkHttpClient.Builder().build()
    private val moshi = Moshi.Builder().build()

    fun connect() {
        val request = Request.Builder()
            .url("$baseUrl/ws/agents?api_key=$apiKey")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                // Send subscription to all events
                webSocket.send("""{"action": "subscribe_all"}""")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val event = moshi.adapter(AutomationEvent::class.java).fromJson(text)
                    event?.let { eventCallback(it) }
                } catch (e: Exception) {
                    // Log parsing error
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                // Attempt reconnect after delay
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
            }
        })
    }

    fun sendPing() {
        webSocket?.send("ping")
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnecting")
    }

    fun isConnected(): Boolean = isConnected
}

// WebSocket event model (matches backend)
import com.aether.companion.data.model.AutomationEvent
import com.aether.companion.data.model.AutomationEvent.EventType
import com.aether.companion.data.model.AutomationEvent.EventData
import com.aether.companion.data.model.AutomationEvent.HumanAction