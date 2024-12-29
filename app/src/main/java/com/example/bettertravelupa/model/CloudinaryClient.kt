package com.example.bettertravelupa.model


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CloudinaryClient {
    private const val BASE_URL = "https://api.cloudinary.com/v1_1/dbftqxgwn/"

    val instance: CloudinaryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApi::class.java)
    }
}
