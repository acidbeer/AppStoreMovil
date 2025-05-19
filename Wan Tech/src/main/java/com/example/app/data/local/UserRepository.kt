package com.example.app.data.local

import java.security.MessageDigest

class UserRepository (private val dao: UserDao) {

    suspend fun register(email: String, plainPwd: String) {
        val hash = hashPassword(plainPwd)
        val user = UserEntity(email, hash)
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

    private fun verifyPassword(plain: String, hash: String): Boolean =
        hashPassword(plain) == hash
}