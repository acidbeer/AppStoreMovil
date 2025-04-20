package com.example.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity



class RegisterActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Encuentra el botón por su ID
        val registerButton: Button = findViewById(R.id.registerButton)

        // Establece el OnClickListener para el botón
        registerButton.setOnClickListener {
            // Crea un Intent para abrir UsuarioActivity
            val intent = Intent(this, UsuarioActivity::class.java)
            // Inicia UsuarioActivity
            startActivity(intent)
        }
    }
}
