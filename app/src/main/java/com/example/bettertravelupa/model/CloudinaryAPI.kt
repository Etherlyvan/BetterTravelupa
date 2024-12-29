package com.example.bettertravelupa.model

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface CloudinaryApi {
    @Multipart
    @POST("image/upload")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Query("upload_preset") uploadPreset: String // Upload preset dari Cloudinary
    ): Call<ResponseBody>
    @POST("image/destroy")
    fun deleteImage(
        @Query("public_id") publicId: String, // ID unik gambar di Cloudinary
        @Query("api_key") apiKey: String,    // API Key dari akun Cloudinary Anda
        @Query("timestamp") timestamp: Long,
        @Query("signature") signature: String // Signature yang dihasilkan untuk keamanan
    ): Call<ResponseBody>

}
