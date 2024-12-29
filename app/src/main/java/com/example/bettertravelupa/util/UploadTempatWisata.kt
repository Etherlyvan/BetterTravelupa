package com.example.bettertravelupa.util

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import com.example.bettertravelupa.model.TempatWisata
import com.google.firebase.firestore.FirebaseFirestore

fun uploadTempatWisata(
    firestore: FirebaseFirestore,
    context: Context,
    tempatWisata: TempatWisata,
    onSuccess: (TempatWisata) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val documentReference = firestore.collection("tempat_wisata").document() // Membuat document baru
    val newTempatWisata = tempatWisata.copy(id = documentReference.id) // Set ID sesuai dengan ID Firestore

    documentReference.set(newTempatWisata) // Simpan objek tempat wisata ke Firestore
        .addOnSuccessListener {
            onSuccess(newTempatWisata) // Panggil callback dengan objek yang baru
        }
        .addOnFailureListener { e ->
            onFailure(e)
        }
}
