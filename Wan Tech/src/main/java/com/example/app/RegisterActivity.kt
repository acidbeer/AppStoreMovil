package com.example.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Referencias a los campos de entrada
        val emailEditText = findViewById<EditText>(R.id.etEmailRegister)
        val passwordEditText = findViewById<EditText>(R.id.etPasswordRegister)
        val registerButton = findViewById<Button>(R.id.RegisterButton)

        // Listener para el botón de registro
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa correo y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                // Ir a UsuarioActivity si los campos están completos
                val intent = Intent(this, UsuarioActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
