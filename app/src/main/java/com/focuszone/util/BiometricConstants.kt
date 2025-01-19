package com.focuszone.util

object BiometricConstants {
    const val BIOMETRIC_AUTH_TYPE = androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
            androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
}