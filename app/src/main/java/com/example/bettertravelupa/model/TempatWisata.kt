package com.example.bettertravelupa.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tempat_wisata")
data class TempatWisata(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val nama: String = "",
    val deskripsi: String = "",
    val gambarUriString: String? = null,
    val gambarResId: String = ""
)
