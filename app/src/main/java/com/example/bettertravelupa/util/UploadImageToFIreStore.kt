package com.example.bettertravelupa.util

import android.content.Context
import android.net.Uri
import com.example.bettertravelupa.model.TempatWisata
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

fun uploadImageToFirestore(
    firestore: FirebaseFirestore,
    context: Context,
    imageUri: Uri,
    tempatWisata: TempatWisata,
    onSuccess: (TempatWisata) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val storageReference = FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}.jpg")

    storageReference.putFile(imageUri)
        .addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                val newTempatWisata = tempatWisata.copy(gambarUriString = downloadUrl.toString())
                val documentReference = firestore.collection("tempat_wisata").document() // Membuat document baru
                documentReference.set(newTempatWisata.copy(id = documentReference.id)) // Set ID sesuai dengan ID Firestore
                    .addOnSuccessListener {
                        onSuccess(newTempatWisata.copy(id = documentReference.id)) // Panggil callback dengan objek yang baru
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }.addOnFailureListener { e ->
                onFailure(e)
            }
        }
        .addOnFailureListener { e ->
            onFailure(e)
        }
}
