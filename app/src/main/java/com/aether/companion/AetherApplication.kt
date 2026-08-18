package com.aether.companion

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class AetherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Subscribe to general topics
        FirebaseMessaging.getInstance().subscribeToTopic("freelancer_updates")
        FirebaseMessaging.getInstance().subscribeToTopic("quality_gates")
    }
}