package com.example.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val price: String,
    val description: String,
    val isInCart: Boolean = false // para distinguir compra o carrito
)
