package com.example.bettertravelupa.util

import java.security.MessageDigest

fun generateCloudinarySignature(publicId: String, timestamp: Long, apiSecret: String): String {
    val dataToSign = "public_id=$publicId&timestamp=$timestamp$apiSecret"
    return MessageDigest.getInstance("SHA-1")
        .digest(dataToSign.toByteArray())
        .joinToString("") { "%02x".format(it) }
}