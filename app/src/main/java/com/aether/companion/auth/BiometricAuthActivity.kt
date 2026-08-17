package com.aether.companion.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.aether.companion.R
import java.util.concurrent.Executor

class BiometricAuthActivity : ComponentActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val executor: Executor = ContextCompat.getMainExecutor(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric_auth)

        setupBiometricPrompt()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun setupBiometricPrompt() {
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                finishWithError("Biometric authentication failed: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                finishWithSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Don't finish, let user retry
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Aether Freelancer")
            .setSubtitle("Authenticate to access your freelance dashboard")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BiometricPrompt.Authenticators.BIOMETRIC_STRONG or BiometricPrompt.Authenticators.DEVICE_CREDENTIAL)
            .build()
    }

    private fun finishWithSuccess() {
        val result = Intent().apply {
            putExtra("auth_success", true)
        }
        setResult(RESULT_OK, result)
        finish()
    }

    private fun finishWithError(message: String) {
        val result = Intent().apply {
            putExtra("auth_success", false)
            putExtra("error_message", message)
        }
        setResult(RESULT_CANCELED, result)
        finish()
    }
}