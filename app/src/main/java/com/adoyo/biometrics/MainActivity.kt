package com.adoyo.biometrics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.adoyo.biometrics.ui.theme.BiometricsTheme

class MainActivity : AppCompatActivity() {
    private val promptManager by lazy { BiometricManager(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BiometricsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val biometricResult by promptManager.resultPrompt.collectAsState(initial = null)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            promptManager.showBiometricPrompt(
                                title = "Biometric Authentication",
                                description = "Please authenticate to continue"
                            )
                        }) {
                            Text("Authenticate")
                        }
                        biometricResult?.let { result ->
                            Text(
                                text = when (result) {
                                    BiometricManager.BiometricResult.HardwareUnavailable -> "Biometric hardware is unavailable"
                                    BiometricManager.BiometricResult.FeatureUnavailable -> "Biometric feature is unavailable"
                                    BiometricManager.BiometricResult.AuthenticationNoteSet -> "Biometric authentication is not set up"
                                    is BiometricManager.BiometricResult.AuthenticationError -> "Authentication error: ${result.error}"
                                    BiometricManager.BiometricResult.AuthenticationSucceeded -> "Authentication successful"
                                    BiometricManager.BiometricResult.AuthenticationCancelled -> TODO()
                                    BiometricManager.BiometricResult.AuthenticationFailed -> TODO()
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}
