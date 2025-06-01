package com.example.app


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.result.IntentSenderRequest
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.UserEntity
import com.example.app.data.local.UserRepository
import com.example.app.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val repo by lazy {
        UserRepository(AppDatabase.getDatabase(this).userDao())
    }

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private lateinit var googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Rellenar email si viene del registro
        intent.getStringExtra("EXTRA_EMAIL")?.let { binding.etEmail.setText(it) }

        binding.btnLogin.setOnClickListener { loginUser() }

        binding.tvForgotPwd.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        // Inicializar One Tap client y configuración
        oneTapClient = Identity.getSignInClient(this)

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id)) // <- DEBE SER el web client ID
                    .setFilterByAuthorizedAccounts(false) // <- false permite mostrar todas las cuentas de Google
                    .build()
            )
            .setAutoSelectEnabled(false) // Opcional, solo si deseas que entre directo
            .build()

        // Configurar el launcher para manejar el IntentSender
        googleSignInLauncher = registerForActivityResult(StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                try {
                    val credential = Identity.getSignInClient(this)
                        .getSignInCredentialFromIntent(result.data)
                    val email = credential.id

                    lifecycleScope.launch {
                        val user = withContext(kotlinx.coroutines.Dispatchers.IO) {
                            repo.getUserByEmail(email)
                        }

                        if (user == null) {
                            val newUser = UserEntity(
                                email = email,
                                passwordHash = "google_auth",
                                role = "user"
                            )
                            withContext(kotlinx.coroutines.Dispatchers.IO) {
                                repo.insertUser(newUser)
                            }
                        }

                        startActivity(Intent(this@LoginActivity, ProductActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "Error al obtener credencial: ${e.message}", e)
                    toast("Fallo al recuperar credenciales")
                }
            } else {
                Log.d("LoginDebug", "Intent result: ${result.data}")
                toast("Inicio de sesión cancelado")
            }
        }

        // Botón de Google personalizado
        binding.btnGoogleLogin.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    val request = IntentSenderRequest.Builder(result.pendingIntent).build()
                    googleSignInLauncher.launch(request)
                }
                .addOnFailureListener {
                    toast("No se pudo iniciar sesión con Google")
                }
        }
    }

    private fun loginUser() = lifecycleScope.launch {
        val email = binding.etEmail.text.toString().trim()
        val pwd = binding.etPassword.text.toString()

        when {
            email.isBlank() || pwd.isBlank() -> toast("Completa todos los campos")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> toast("Correo inválido")
            else -> {
                val user = withContext(kotlinx.coroutines.Dispatchers.IO) {
                    repo.loginAndGetUser(email, pwd)
                }

                if (user != null && repo.verifyPassword(pwd, user.passwordHash)) {
                    Toast.makeText(this@LoginActivity, "Rol: ${user.role}", Toast.LENGTH_SHORT).show()
                    when (user.role) {
                        "admin" -> {
                            val intent = Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                            intent.putExtra("USER_ROLE", "admin")
                            startActivity(intent)
                        }
                        "user" -> {
                            startActivity(Intent(this@LoginActivity, ProductActivity::class.java))
                        }
                        else -> toast("Rol no reconocido: ${user.role}")
                    }
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