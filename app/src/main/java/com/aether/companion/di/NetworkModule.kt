package com.aether.companion.di

import com.aether.companion.data.api.AetherApiService
import com.aether.companion.data.api.AetherWebSocketClient
import com.aether.companion.data.repository.FreelancerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        val baseUrl = "https://your-aether-backend.com/" // TODO: Make configurable
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): AetherApiService {
        return retrofit.create(AetherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWebSocketClient(
        apiService: AetherApiService,
        // In real app, get from secure storage
        apiKey: String = "YOUR_API_KEY"
    ): AetherWebSocketClient {
        val baseUrl = "wss://your-aether-backend.com" // TODO: Make configurable
        return AetherWebSocketClient(baseUrl, apiKey) { event ->
            // Event handling will be done in repository
        }
    }

    @Provides
    @Singleton
    fun provideFreelancerRepository(
        apiService: AetherApiService,
        webSocketClient: AetherWebSocketClient
    ): FreelancerRepository {
        return FreelancerRepository(apiService, webSocketClient)
    }
}