package com.example.app


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.UserRepository
import com.example.app.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val repo by lazy {
        UserRepository(AppDatabase.getDatabase(this).userDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el layout y crea el binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Rellenar email si viene del registro
        intent.getStringExtra("EXTRA_EMAIL")?.let { binding.etEmail.setText(it) }

        binding.btnLogin.setOnClickListener { loginUser() }
    }

    /** Valida los datos y redirige a ProductActivity si son correctos */
    private fun loginUser() = lifecycleScope.launch {
        val email = binding.etEmail.text.toString().trim()
        val pwd   = binding.etPassword.text.toString()

        when {
            email.isBlank() || pwd.isBlank() ->
                toast("Completa todos los campos")

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                toast("Correo inválido")

            else -> {
                val ok = repo.login(email, pwd)
                if (ok) {
                    // Redirige a ProductActivity
                    startActivity(Intent(this@LoginActivity, ProductActivity::class.java))

                    finish()
                } else {
                    toast("Credenciales inválidas")
                }
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

}