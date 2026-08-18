package com.aether.companion.data.api

import com.aether.companion.data.model.AIAssistantRequest
import com.aether.companion.data.model.AIAssistantResponse
import com.aether.companion.data.model.FreelancerJob
import com.aether.companion.data.model.FreelancerJob.JobStatus
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
}

data class HealthResponse(
    val status: String,
    val timestamp: Long
)

data class ApproveResponse(
    val success: Boolean,
    val jobId: String,
    val message: String?
)

data class DeliveryResponse(
    val success: Boolean,
    val deliveryId: String?,
    val message: String?
)

data class ExportResponse(
    val success: Boolean,
    val downloadUrl: String?,
    val expiresAt: Long?
)

class AetherApiClient private constructor(private val service: AetherApiService) {
    companion object {
        private var INSTANCE: AetherApiClient? = null
        private val moshi = com.squareup.moshi.Moshi.Builder()
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()

        fun getInstance(baseUrl: String = "https://your-aether-backend.com/"): AetherApiClient {
            if (INSTANCE == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                INSTANCE = AetherApiClient(retrofit.create(AetherApiService::class.java))
            }
            return INSTANCE!!
        }
    }

    fun getService(): AetherApiService = service

    // WebSocket connection for real-time updates
    fun connectWebSocket(
        apiKey: String,
        listener: WebSocketListener
    ): WebSocket {
        val client = OkHttpClient.Builder().build()
        val request = OkHttpRequest.Builder()
            .url("wss://your-aether-backend.com/ws/agents?api_key=$apiKey")
            .build()
        return client.newWebSocket(request, listener)
    }
}