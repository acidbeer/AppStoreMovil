package com.example.app

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.UserRepository
import com.example.app.databinding.ActivityResetPasswordBinding
import kotlinx.coroutines.launch

class ResetPasswordActivity :  AppCompatActivity(){

    /* ---------- View Binding ---------- */
    private lateinit var binding: ActivityResetPasswordBinding

    /* ---------- Repositorio ---------- */
    private val repo by lazy {
        // usa el mismo AppDatabase donde añadiste UserDao
        UserRepository(AppDatabase.getDatabase(this).userDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReset.setOnClickListener { resetPassword() }
    }

    /** Lógica de restablecer contraseña */
    private fun resetPassword() = lifecycleScope.launch {
        val email = binding.etEmail.text.toString().trim()
        val newPwd = binding.etNewPwd.text.toString()
        val newPwd2 = binding.etNewPwd2.text.toString()

        when {
            email.isBlank() || newPwd.isBlank() || newPwd2.isBlank() ->
                toast("Completa todos los campos")

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                toast("Correo inválido")

            newPwd.length < 8 ->
                toast("La nueva contraseña debe tener al menos 8 caracteres")

            newPwd != newPwd2 ->
                toast("Las contraseñas no coinciden")

            else -> {
                val updated = repo.resetPassword(email, newPwd)
                if (updated) {
                    toast("Contraseña actualizada")
                    finish()                    // vuelves atrás al login
                } else {
                    toast("Usuario no encontrado")
                }
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}