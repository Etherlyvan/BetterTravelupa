package com.example.bettertravelupa.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.bettertravelupa.model.CloudinaryClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private fun extractPublicId(responseBody: String?): String? {
    // Parsing JSON respons untuk mendapatkan public_id gambar
    return try {
        val jsonObject = JSONObject(responseBody ?: return null)
        jsonObject.getString("public_id")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun uploadImageToCloudinary(
    context: Context,
    imageUri: Uri,
    uploadPreset: String,
    onSuccess: (String, String) -> Unit, // Tambahkan public_id sebagai parameter sukses
    onFailure: (Exception) -> Unit
) {
    val file = getFileFromUri(context, imageUri) ?: return
    val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
    val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

    CloudinaryClient.instance.uploadImage(filePart, uploadPreset).enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                val responseBody = response.body()?.string()
                val imageUrl = extractImageUrl(responseBody)
                val publicId = extractPublicId(responseBody)
                if (imageUrl != null && publicId != null) {
                    onSuccess(imageUrl, publicId)
                } else {
                    onFailure(Exception("Gagal mendapatkan URL atau public_id gambar"))
                }
            } else {
                onFailure(Exception("Gagal mengunggah gambar: ${response.errorBody()?.string()}"))
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            onFailure(Exception(t))
        }
    })
}

private fun extractImageUrl(responseBody: String?): String? {
    // Parsing JSON respons untuk mendapatkan URL gambar
    return try {
        val jsonObject = JSONObject(responseBody ?: return null)
        jsonObject.getString("secure_url")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getFileFromUri(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val fileName = getFileName(contentResolver, uri) ?: return null
    val tempFile = File(context.cacheDir, fileName)

    contentResolver.openInputStream(uri)?.use { inputStream ->
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return tempFile
}

private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
    var name: String? = null
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        }
    }
    return name
}
