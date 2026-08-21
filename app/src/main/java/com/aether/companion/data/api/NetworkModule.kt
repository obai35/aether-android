package com.aether.companion.data.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
private const val DEFAULT_API_URL = "https://aether-backend.onrender.com"
private const val DEFAULT_API_KEY = ""

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aether_settings")

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

    private var currentApiUrl = DEFAULT_API_URL
    private var currentApiKey = DEFAULT_API_KEY

    private var apiService: AetherApiService? = null

    fun initialize(context: Context) {
        // Load saved settings synchronously on init
        runBlocking(Dispatchers.IO) {
            loadSettings(context)
        }

        // Create initial API service
        createApiService()
    }

    private suspend fun loadSettings(context: Context) {
        context.dataStore.data.first().let { prefs: Preferences ->
            currentApiUrl = prefs[KEY_API_URL] ?: DEFAULT_API_URL
            currentApiKey = prefs[KEY_API_KEY] ?: DEFAULT_API_KEY
        }
    }

    suspend fun saveSettings(apiUrl: String, apiKey: String) {
        currentApiUrl = apiUrl
        currentApiKey = apiKey

        // Need a context to access dataStore - use application context
        // We'll store a reference to context at initialize time
        dataStoreRef?.edit { prefs: MutablePreferences ->
            prefs[KEY_API_URL] = apiUrl
            prefs[KEY_API_KEY] = apiKey
        }

        // Recreate API service with new URL
        createApiService()
    }

    private var dataStoreRef: DataStore<Preferences>? = null

    fun setDataStore(dataStore: DataStore<Preferences>) {
        dataStoreRef = dataStore
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
        if (apiService == null) {
            createApiService()
        }
        return apiService!!
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