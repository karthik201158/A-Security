package com.karthik.a.biometric.withcrypto.crypto

data class CiphertextWrapper(
    val ciphertext: ByteArray, val initializationVector: ByteArray
)
