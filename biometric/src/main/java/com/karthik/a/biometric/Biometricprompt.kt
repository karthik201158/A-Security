package com.karthik.a.biometric

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

object BiometricPromptUtils {
    const val BIOMETRIC_AUTHENTICATE_FAILED_ERROR_CODE: Int = -1
    const val BIOMETRIC_AUTHENTICATE_FAILED_ERROR_MESSAGE: String = "onAuthenticationFailed"

    fun createBiometricPrompt(
        activity: AppCompatActivity,
        processSuccess: (BiometricPrompt.AuthenticationResult) -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errCode, errString)
              //  processFailure?.invoke(errCode, errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                /*processFailure?.invoke(
                    BIOMETRIC_AUTHENTICATE_FAILED_ERROR_CODE,
                    BIOMETRIC_AUTHENTICATE_FAILED_ERROR_MESSAGE
                )*/
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                processSuccess(result)
            }
        }
        return BiometricPrompt(activity, executor, callback)
    }

     fun createPromptInfo(
        activity: AppCompatActivity
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            // e.g. "Sign in"
            .setTitle(activity.getString(R.string.prompt_info_title))
            // e.g. "Biometric for My App"
            .setSubtitle(activity.getString(R.string.prompt_info_subtitle))
            // e.g. "Confirm biometric to continue"
            .setDescription(activity.getString(R.string.prompt_info_description))
            .setConfirmationRequired(false)
            .setNegativeButtonText(activity.getString(R.string.prompt_info_use_app_password))
            // .setDeviceCredentialAllowed(true) // Allow PIN/pattern/password authentication.
            // Also note that setDeviceCredentialAllowed and setNegativeButtonText are
            // incompatible so that if you uncomment one you must comment out the other
            .build()
    }

}