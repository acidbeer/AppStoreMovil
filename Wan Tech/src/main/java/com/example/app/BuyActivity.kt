package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.app.data.local.PurchaseEntity

class BuyActivity: AppCompatActivity() {

    private lateinit var purchaseViewModel: PurchaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)

        // Referencias a los views
        val imageView = findViewById<ImageView>(R.id.productImageBuy)
        val nameView = findViewById<TextView>(R.id.productNameBuy)
        val priceView = findViewById<TextView>(R.id.productPriceBuy)
        val confirmButton = findViewById<android.widget.Button>(R.id.confirmBuyButton)

        // Nuevas referencias a los campos de entrada
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val unitEditText = findViewById<EditText>(R.id.unitEditText)

        // ViewModel
        purchaseViewModel = ViewModelProvider(this)[PurchaseViewModel::class.java]

        val productName = intent.getStringExtra("name")
        val productPrice = intent.getStringExtra("price")
        val productImageUrl = intent.getStringExtra("imageUrl")

        nameView.text = productName ?: "Nombre no disponible"
        priceView.text = productPrice ?: "$0.00"
        if (!productImageUrl.isNullOrEmpty()) {
            Glide.with(this).load(productImageUrl).into(imageView)
        }

        confirmButton.setOnClickListener {
            val userName = nameEditText.text.toString().trim()
            val userEmail = emailEditText.text.toString().trim()
            val userAddress = addressEditText.text.toString().trim()
            val userUnit = unitEditText.text.toString().trim()

            // Validar que los campos no estén vacíos
            if (userName.isEmpty() || userEmail.isEmpty() || userAddress.isEmpty()|| userUnit.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar el formato del email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar que la cantidad sea un número válido
            val quantity = userUnit.toIntOrNull()
            if (quantity == null || quantity <= 0) {
                Toast.makeText(this, "Por favor, ingresa una cantidad válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Crear objeto de compra con la cantidad
            val purchase = PurchaseEntity(
                name = productName ?: "Nombre no disponible",
                price = productPrice ?: "$0.00",
                imageUrl = productImageUrl ?: "",
                quantity = quantity // Incluir la cantidad en el objeto
            )

            // Guardar compra en la base de datos
            purchaseViewModel.savePurchase(purchase)

            Toast.makeText(this, "¡Compra confirmada!", Toast.LENGTH_LONG).show()

            // Redirigir a ProductActivity
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}