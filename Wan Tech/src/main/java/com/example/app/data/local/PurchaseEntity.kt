package com.example.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: String,
    val imageUrl: String,
    val quantity: Int // Nueva propiedad para la cantidad de unidades
)
