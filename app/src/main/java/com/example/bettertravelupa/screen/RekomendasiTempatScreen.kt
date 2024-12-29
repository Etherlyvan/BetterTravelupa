package com.example.bettertravelupa.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.bettertravelupa.component.TambahTempatWisataDialog
import com.example.bettertravelupa.component.TempatItemEditable
import com.example.bettertravelupa.model.AppDatabase
import com.example.bettertravelupa.model.TempatWisata
import com.example.bettertravelupa.util.deleteTempatWisata
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.bettertravelupa.BuildConfig
import com.example.bettertravelupa.util.uploadImageToCloudinary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekomendasiTempatScreen(
    firestore: FirebaseFirestore,
    appDatabase: AppDatabase,
    onBackToLogin: (() -> Unit)? = null,
    onGallerySelected: () -> Unit
) {
    var daftarTempatWisata by remember { mutableStateOf(listOf<TempatWisata>()) }
    var showTambahDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val context = LocalContext.current

    // Mengambil data tempat wisata dari Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("tempat_wisata")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        error.printStackTrace()
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        daftarTempatWisata = snapshot.documents.map { document ->
                            document.toObject(TempatWisata::class.java)?.copy(id = document.id) ?: TempatWisata()
                        }
                    }
                }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .background(Color(0xFFBBDEFB))
                    .fillMaxSize()
            ) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()
                Text(
                    text = "Gallery",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onGallerySelected()
                            coroutineScope.launch { drawerState.close() }
                        }
                        .padding(16.dp)
                )
                Text(
                    text = "Logout",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                FirebaseAuth.getInstance().signOut()
                                onBackToLogin?.invoke()
                            }
                        }
                        .padding(16.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Rekomendasi Tempat Wisata",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color(0xFF2196F3)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showTambahDialog = true },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Tambah Tempat Wisata")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                LazyColumn {
                    items(daftarTempatWisata) { tempat ->
                        TempatItemEditable(
                            tempat = tempat,
                            onDelete = {
                                coroutineScope.launch {
                                    try {
                                        deleteTempatWisata(
                                            tempat = tempat,
                                            firestore = firestore,
                                            appDatabase = appDatabase,
                                            onUpdateList = { updatedList ->
                                                daftarTempatWisata = updatedList
                                            },
                                            apiKey = BuildConfig.CLOUDINARY_API_KEY,
                                            apiSecret = BuildConfig.CLOUDINARY_API_SECRET
                                        )
                                    } catch (exception: Exception) {
                                        exception.printStackTrace()
                                    }
                                }
                            }
                        )
                    }
                }

                if (showTambahDialog) {
                    if (userId != null) {
                        TambahTempatWisataDialog(
                            firestore = firestore,
                            context = context,
                            userId = userId,
                            onDismiss = { showTambahDialog = false },
                            onTambah = { nama, deskripsi, gambarUriString ->
                                val gambarUri = Uri.parse(gambarUriString)

                                uploadImageToCloudinary(
                                    context = context,
                                    imageUri = gambarUri,
                                    uploadPreset = "preset_travelupa",
                                    onSuccess = { imageUrl, publicId ->
                                        val newTempat = TempatWisata(
                                            nama = nama,
                                            deskripsi = deskripsi,
                                            userId = userId,
                                            gambarUriString = imageUrl,
                                            gambarResId = publicId
                                        )
                                        daftarTempatWisata = daftarTempatWisata + newTempat
                                        showTambahDialog = false
                                    },
                                    onFailure = { exception ->
                                        exception.printStackTrace()
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

