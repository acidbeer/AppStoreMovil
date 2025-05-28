package com.example.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Query("SELECT * FROM products")
    suspend fun getAll(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE isInCart = 1")
    suspend fun getCartItems(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Int): ProductEntity?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    suspend fun searchByName(query: String): List<ProductEntity>

    @Query("SELECT * FROM products WHERE price <= :maxPrice")
    suspend fun filterByPrice(maxPrice: String): List<ProductEntity>

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)
}