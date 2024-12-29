package com.example.bettertravelupa.util

import com.example.bettertravelupa.model.CloudinaryClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun deleteImageFromCloudinary(publicId: String, apiKey: String, apiSecret: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val timestamp = System.currentTimeMillis() / 1000
    val signature = generateCloudinarySignature(publicId, timestamp, apiSecret)

    CloudinaryClient.instance.deleteImage(
        publicId = publicId,
        timestamp = timestamp,
        apiKey = apiKey,
        signature = signature
    ).enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                onSuccess()
            } else {
                onFailure(Exception("Gagal menghapus gambar: ${response.errorBody()?.string()}"))
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            onFailure(Exception(t))
        }
    })
}