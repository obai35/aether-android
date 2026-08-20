package com.aether.companion

import android.app.Application
import com.aether.companion.data.api.NetworkModule
import com.aether.companion.data.api.getNetworkModule
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class AetherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Initialize NetworkModule early
        getNetworkModule().initialize(this)
        // Subscribe to general topics
        FirebaseMessaging.getInstance().subscribeToTopic("freelancer_updates")
        FirebaseMessaging.getInstance().subscribeToTopic("quality_gates")
    }
}