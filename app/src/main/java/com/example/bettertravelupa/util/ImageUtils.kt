package com.example.bettertravelupa.util

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


fun saveImageToLocal(context: Context, bitmap: Bitmap, imageName: String): String? {
    val directory = context.getExternalFilesDir(null) // Atau gunakan context.filesDir untuk penyimpanan internal
    val file = File(directory, "$imageName.jpg")

    return try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        file.absolutePath // Kembalikan path file yang disimpan
    } catch (e: IOException) {
        e.printStackTrace()
        null // Kembalikan null jika terjadi kesalahan
    }
}
