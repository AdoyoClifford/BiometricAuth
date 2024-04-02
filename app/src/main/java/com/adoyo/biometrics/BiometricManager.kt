package com.adoyo.biometrics

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt.PromptInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricManager(
    private val activity: AppCompatActivity
) {
    private val resultChannel = Channel<BiometricResult>()
    val resultPrompt = resultChannel.receiveAsFlow()

    fun showBiometricPrompt(
        title: String,
        description: String
    ) {
        val manager = BiometricManager.from(activity)
        val authenticator = if (Build.VERSION.SDK_INT >= 30) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else {
            BIOMETRIC_STRONG
        }

        val promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticator)



        if (Build.VERSION.SDK_INT < 30) {
            promptInfo.setNegativeButtonText("Cancel")
        }

        when(manager.canAuthenticate(authenticator)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                resultChannel.trySend(BiometricResult.FeatureUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BiometricResult.AuthenticationNoteSet)
                return
            }
            else -> Unit
        }

    }

    sealed interface BiometricResult {
        data object HardwareUnavailable : BiometricResult
        data object FeatureUnavailable : BiometricResult
        data class AuthenticationError(val error: String) : BiometricResult
        data object AuthenticationFailed : BiometricResult
        data object AuthenticationSucceeded : BiometricResult
        data object AuthenticationCancelled : BiometricResult
        data object AuthenticationNoteSet: BiometricResult

    }
}