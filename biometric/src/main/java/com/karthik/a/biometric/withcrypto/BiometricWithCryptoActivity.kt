package com.karthik.a.biometric.withcrypto

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.karthik.a.biometric.BiometricPromptUtils
import com.karthik.a.biometric.R
import com.karthik.a.biometric.util.showToast
import com.karthik.a.biometric.withcrypto.crypto.CryptographyManager
import com.karthik.a.biometric.withcrypto.crypto.CryptographyManagerImpl

class BiometricWithCryptoActivity : AppCompatActivity() {

    private lateinit var cryptographyManager: CryptographyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cryptographyManager = CryptographyManagerImpl()
        showBiometricPromptForEncryption()
    }

    private fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                showToast(getString(R.string.message_no_support_biometrics))
                false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                showToast(getString(R.string.message_no_hardware_available))
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e("Biometric", "No biometrics enrolled.")
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                        )
                    }
                } else {
                    TODO("VERSION.SDK_INT < R")
                }
                // startActivityForResult(enrollIntent, REQUEST_CODE)
                false
            }
            else -> false
        }
    }

    private fun showBiometricPromptForEncryption() {
        if (isBiometricAvailable()) {
            val secretKeyName = getString(R.string.secret_key_name)
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            //Create the Biometric Prompt
            val biometricPrompt = BiometricPromptUtils.createBiometricPrompt(this, ::encryptSecretInformation)
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            //showBiometricPrompt with crypto
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun encryptSecretInformation(authResult: BiometricPrompt.AuthenticationResult) {
        authResult.cryptoObject?.cipher?.apply {
            val encryptedData =  cryptographyManager.encryptData("token", this)
        }
    }
}