package com.example.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProductEntity::class, PurchaseEntity::class,UserEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase(){
    abstract fun productDao(): ProductDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                ).build()
                    .also{ INSTANCE = it }
            }
        }
    }
}