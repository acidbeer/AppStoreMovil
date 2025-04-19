package com.example.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PurchaseDao {
    @Insert
    suspend fun insertPurchase(purchase: PurchaseEntity)

    @Query("SELECT * FROM purchases")
    suspend fun getAllPurchases(): List<PurchaseEntity>
}