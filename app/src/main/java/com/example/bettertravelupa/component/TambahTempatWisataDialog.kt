package com.example.bettertravelupa.component

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.bettertravelupa.model.TempatWisata
import com.example.bettertravelupa.util.saveImageToLocal
import com.example.bettertravelupa.util.uploadImageToCloudinary
import com.example.bettertravelupa.util.uploadImageToFirestore
import com.example.bettertravelupa.util.uploadTempatWisata
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun TambahTempatWisataDialog(
    firestore: FirebaseFirestore,
    context: Context,
    userId: String,
    onDismiss: () -> Unit,
    onTambah: (String, String, String?) -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var gambarUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        gambarUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Tempat Wisata Baru") },
        text = {
            Column {
                TextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Tempat") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Pilih Gambar")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nama.isNotBlank() && deskripsi.isNotBlank() && gambarUri != null) {
                        isUploading = true
                        uploadImageToCloudinary(
                            context = context,
                            imageUri = gambarUri!!,
                            uploadPreset = "preset_travelupa",
                            onSuccess = { imageUrl, publicId ->
                                val newTempat = TempatWisata(
                                    nama = nama,
                                    deskripsi = deskripsi,
                                    userId = userId,
                                    gambarUriString = imageUrl,
                                    gambarResId = publicId
                                )
                                uploadTempatWisata(
                                    firestore = firestore,
                                    context = context,
                                    tempatWisata = newTempat,
                                    onSuccess = {
                                        isUploading = false
                                        onTambah(it.nama, it.deskripsi, it.gambarUriString)
                                        onDismiss()
                                    },
                                    onFailure = { e ->
                                        isUploading = false
                                        e.printStackTrace()
                                    }
                                )
                            },
                            onFailure = { e ->
                                isUploading = false
                                e.printStackTrace()
                            }
                        )
                    }
                },
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Tambah")
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, enabled = !isUploading) {
                Text("Batal")
            }
        }
    )
}
