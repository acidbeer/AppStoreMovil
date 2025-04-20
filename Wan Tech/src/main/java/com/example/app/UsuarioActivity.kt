package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class UsuarioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        // Al hacer click en el btnSignup se ejecuta lo siguiente
        btnSignUp.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()

            // si el campo esta vacio se le pide que ingrese usario mediante el metodo Toast
            if (usuario.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa un nombre de usuario", Toast.LENGTH_SHORT).show()
            } else {
                // Si el agrego agrego ususario se muestra un mensaje de registro completo
                Toast.makeText(this, "Registro completado", Toast.LENGTH_SHORT).show()

                // Redirigir al Login o HomeActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
