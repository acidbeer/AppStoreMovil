package com.example.app.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val price: String,
    val description: String,
    val quantity: Int = 1 // default para casos desde el detalle
) : Parcelable
