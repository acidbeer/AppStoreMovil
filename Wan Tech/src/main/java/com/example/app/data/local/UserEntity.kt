package com.example.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(@PrimaryKey val email: String,
                      val passwordHash: String,
                      val role: String)
