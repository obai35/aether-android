package com.aether.companion.data.api

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

// DataStore keys
private val KEY_API_URL = stringPreferencesKey("api_url")
private val KEY_API_KEY = stringPreferencesKey("api_key")

// Constants at top level
private const val DEFAULT_API_URL = "https://your-aether-backend.com"
private const val DEFAULT_API_KEY = ""

object NetworkModule {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private var dataStore: androidx.datastore.preferences.PreferencesDataStore? = null
    private var currentApiUrl = DEFAULT_API_URL
    private var currentApiKey = DEFAULT_API_KEY

    private var apiService: AetherApiService? = null

    fun initialize(context: Context) {
        // Build DataStore
        dataStore = context.preferencesDataStore(name = "aether_settings")

        // Load saved settings synchronously on init
        runBlocking(Dispatchers.IO) {
            loadSettings()
        }

        // Create initial API service
        createApiService()
    }

    private fun loadSettings() {
        dataStore?.data?.firstOrNull()?.let { prefs: Preferences ->
            currentApiUrl = prefs[KEY_API_URL] ?: DEFAULT_API_URL
            currentApiKey = prefs[KEY_API_KEY] ?: DEFAULT_API_KEY
        }
    }

    suspend fun saveSettings(apiUrl: String, apiKey: String) {
        currentApiUrl = apiUrl
        currentApiKey = apiKey

        dataStore?.edit { prefs: MutablePreferences ->
            prefs[KEY_API_URL] = apiUrl
            prefs[KEY_API_KEY] = apiKey
        }

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
    }
}

// Extension for easy access
fun Context.getNetworkModule(): NetworkModule {
    return NetworkModule
}