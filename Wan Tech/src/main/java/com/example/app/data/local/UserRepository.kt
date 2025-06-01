package com.example.app.data.local

import java.security.MessageDigest

class UserRepository (private val dao: UserDao) {

    suspend fun register(email: String, plainPwd: String, role: String) {
        val hash = hashPassword(plainPwd)
        val user = UserEntity(
            email = email,
            passwordHash = hash,
            role=role)
        dao.insertUser(user)
    }

    suspend fun login(email: String, plainPwd: String): Boolean {
        val user = dao.getUserByEmail(email) ?: return false
        return verifyPassword(plainPwd, user.passwordHash)
    }

    suspend fun resetPassword(email: String, newPwd: String): Boolean {
        val newHash = hashPassword(newPwd)
        return dao.updatePassword(email, newHash) > 0
    }

    /* ---------- Hash helpers ---------- */
    private fun hashPassword(pwd: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(pwd.toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun verifyPassword(plain: String, hash: String): Boolean =
        hashPassword(plain) == hash

    suspend fun loginAndGetUser(email: String, plainPwd: String): UserEntity? {
        val user = dao.getUserByEmail(email) ?: return null
        return if (verifyPassword(plainPwd, user.passwordHash)) user else null
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return dao.getUserByEmail(email)
    }

    suspend fun insertUser(user: UserEntity) {
        dao.insertUser(user)
    }

}