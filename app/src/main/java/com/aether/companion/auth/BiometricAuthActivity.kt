package com.aether.companion.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.aether.companion.R
import java.util.concurrent.Executor

class BiometricAuthActivity : ComponentActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric_auth)

        setupBiometricPrompt()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun setupBiometricPrompt() {
        val executor: Executor = ContextCompat.getMainExecutor(this)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
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
        }
        biometricPrompt = BiometricPrompt(this, executor, callback)

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Aether Freelancer")
            .setSubtitle("Authenticate to access your freelance dashboard")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
    }

    private fun finishWithSuccess() {
        val intent = Intent()
        intent.putExtra("auth_success", true)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun finishWithError(error: String) {
        val intent = Intent()
        intent.putExtra("auth_success", false)
        intent.putExtra("error", error)
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}