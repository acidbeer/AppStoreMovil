package com.example.app


import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.app.databinding.ActivityRegisterBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivityRegisterBinding

    // Repositorio (Room)
    private val repo by lazy {
        UserRepository(
            AppDatabase.getDatabase(this).userDao()   // ← usa getDatabase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.RegisterButton.setOnClickListener { registerUser() }
    }

    /** Registro con validaciones y guardado en Room */
    private fun registerUser() = lifecycleScope.launch {
        val email    = binding.etEmailRegister.text.toString().trim()
        val pwd      = binding.etPasswordRegister.text.toString()
        val pwd2     = binding.etConfirmPwd.text.toString()

        // --- Validaciones ---
        when {
            email.isBlank() || pwd.isBlank() || pwd2.isBlank() ->
                toast("Completa todos los campos")

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                toast("Correo inválido")

            pwd.length < 8 ->
                toast("Contraseña mínimo 8 caracteres")

            pwd != pwd2 ->
                toast("Las contraseñas no coinciden")

            else -> {
                val inserted = try {
                    /* Ejecuta inserción en hilo IO */
                    withContext(Dispatchers.IO) {
                        repo.register(email, pwd)
                    }
                    true
                } catch (e: SQLiteConstraintException) {
                    toast("El correo ya está registrado")
                    false
                }

                if (inserted) {
                    toast("¡Registro exitoso! Inicia sesión")

                    /* Navega a LoginActivity y pasa el correo */
                    val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        .putExtra("EXTRA_EMAIL", email)
                    startActivity(loginIntent)

                    finish() // cierra RegisterActivity
                }
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
