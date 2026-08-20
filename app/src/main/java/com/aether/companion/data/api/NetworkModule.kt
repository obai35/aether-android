package com.aether.companion.data.api

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.rx.RxDataStore
import androidx.datastore.rxjava3.RxDataStoreBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

// DataStore keys
private val KEY_API_URL = stringPreferencesKey("api_url")
private val KEY_API_KEY = stringPreferencesKey("api_key")

object NetworkModule {

    private const val DEFAULT_API_URL = "https://your-aether-backend.com"
    private const val DEFAULT_API_KEY = ""

    @Volatile
    private var INSTANCE: NetworkModule? = null

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private lateinit var rxDataStore: io.reactivex.rxjava3.core.Single<Preferences>
    private var currentApiUrl = DEFAULT_API_URL
    private var currentApiKey = DEFAULT_API_KEY

    private var apiService: AetherApiService? = null

    fun initialize(context: Context) {
        // Build RxDataStore
        val dataStoreBuilder = RxDataStoreBuilder(context, "aether_settings")
        rxDataStore = dataStoreBuilder.build()

        // Load saved settings
        loadSettings()

        // Create initial API service
        createApiService()
    }

    fun getInstance(): NetworkModule = INSTANCE ?: synchronized(this) {
        INSTANCE ?: NetworkModule().also { INSTANCE = it }
    }

    private fun loadSettings() {
        try {
            rxDataStore.subscribe { prefs ->
                currentApiUrl = prefs[KEY_API_URL] ?: DEFAULT_API_URL
                currentApiKey = prefs[KEY_API_KEY] ?: DEFAULT_API_KEY
            }.blockingGet()
        } catch (e: Exception) {
            // Use defaults
        }
    }

    suspend fun saveSettings(apiUrl: String, apiKey: String) {
        currentApiUrl = apiUrl
        currentApiKey = apiKey

        rxDataStore.subscribe { prefs ->
            prefs.toMutablePreferences().apply {
                this[KEY_API_URL] = apiUrl
                this[KEY_API_KEY] = apiKey
            }
        }.blockingGet()

        // Recreate API service with new URL
        createApiService()
    }

    private fun createApiService() {
        val retrofit = Retrofit.Builder()
            .baseUrl(currentApiUrl)
            .client(okHttpClient.newBuilder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $currentApiKey")
                        .addHeader("Content-Type", "application/json")
                        .build()
                    chain.proceed(request)
                }
                .build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        apiService = retrofit.create(AetherApiService::class.java)
    }

    fun getApiService(): AetherApiService {
        return apiService ?: createApiService().also { apiService = it }
    }

    fun getCurrentApiUrl(): String = currentApiUrl
    fun getCurrentApiKey(): String = currentApiKey

    // For testing/debugging
    fun reset() {
        apiService = null
        INSTANCE = null
    }
}

// Extension for easy access
fun Context.getNetworkModule(): NetworkModule {
    val module = NetworkModule.getInstance()
    // Initialize if not already
    return module
}