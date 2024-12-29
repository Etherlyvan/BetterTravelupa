package com.example.bettertravelupa.util

import com.example.bettertravelupa.BuildConfig
import com.example.bettertravelupa.model.AppDatabase
import com.example.bettertravelupa.model.TempatWisata
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun deleteTempatWisata(
    tempat: TempatWisata,
    firestore: FirebaseFirestore,
    appDatabase: AppDatabase,
    onUpdateList: (List<TempatWisata>) -> Unit,
    apiKey: String,
    apiSecret: String
) {
    try {
        val publicId = tempat.gambarUriString?.split("/")?.last()?.split(".")?.first()
        if (publicId != null) {
            // Gunakan fungsi suspend untuk menghapus gambar
            deleteImageFromCloudinary(
                publicId = tempat.gambarResId.toString(),
                apiKey = BuildConfig.CLOUDINARY_API_KEY,
                apiSecret = BuildConfig.CLOUDINARY_API_SECRET,
                onSuccess = {
                    // Hapus data tempat wisata dari database
                    firestore.collection("tempat_wisata").document(tempat.id).delete()
                },
                onFailure = { exception ->
                    exception.printStackTrace()
                    // Tampilkan pesan error ke pengguna
                }
            )
        }

        // Hapus tempat wisata dari Firestore
        firestore.collection("tempat_wisata").document(tempat.id).delete().await()

        // Ambil daftar tempat wisata yang diperbarui
        val updatedList = firestore.collection("tempat_wisata")
            .whereEqualTo("userId", tempat.userId)
            .get()
            .await()
            .map { document ->
                document.toObject(TempatWisata::class.java).copy(id = document.id)
            }

        // Panggil callback untuk memperbarui daftar
        onUpdateList(updatedList)
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}
