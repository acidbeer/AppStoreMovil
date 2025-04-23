package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BundleCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app.data.local.PurchaseEntity
import com.example.app.model.Product

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
        val recyclerView = findViewById<RecyclerView>(R.id.productsRecyclerView)


        // ViewModel
        purchaseViewModel = ViewModelProvider(this)[PurchaseViewModel::class.java]

        // Obtener productos
        @Suppress("DEPRECATION")
        val productList = intent.getParcelableArrayListExtra<Product>("selectedProducts")

        var productName: String? = null
        var productPrice: String? = null
        var productImageUrl: String? = null
        var totalQuantity = 1

        if (!productList.isNullOrEmpty()) {

            if (productList.size == 1) {
                // SOLO UN PRODUCTO → Mostrar como vista individual
                val product = productList[0]

                nameView.visibility = View.VISIBLE
                priceView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                imageView.visibility = View.VISIBLE

                productName = product.name
                productPrice = product.price
                productImageUrl = product.imageUrl
                totalQuantity = product.quantity

                val totalTextView = findViewById<TextView>(R.id.totalPriceTextView)
                totalTextView.visibility = View.GONE

                nameView.text = productName
                priceView.text = productPrice
                Glide.with(this).load(productImageUrl).into(imageView)


            } else {
                // MÚLTIPLES PRODUCTOS → Mostrar RecyclerView
                recyclerView.visibility = View.VISIBLE
                imageView.visibility = View.VISIBLE
                nameView.visibility = View.GONE
                priceView.visibility = View.GONE

                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = BuyProductAdapter(productList)

                val total = productList.sumOf { (it.price.toDoubleOrNull() ?: 0.0) * it.quantity }
                productName = productList.joinToString("\n") { "${it.name} x${it.quantity}" }
                productPrice = "Total: $${"%.2f".format(total)}"
                productImageUrl = productList[0].imageUrl
                totalQuantity = productList.sumOf { it.quantity }

                val totalTextView = findViewById<TextView>(R.id.totalPriceTextView)
                totalTextView.visibility = View.VISIBLE
                totalTextView.text = productPrice
            }

        }else{
            // NO LLEGÓ UNA LISTA → probablemente producto individual vía extras
            recyclerView.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            nameView.visibility = View.VISIBLE
            priceView.visibility = View.VISIBLE

            productName = intent.getStringExtra("name")
            productPrice = intent.getStringExtra("price")
            productImageUrl = intent.getStringExtra("imageUrl")
            totalQuantity = 1

            nameView.text = productName ?: "Nombre no disponible"
            priceView.text = productPrice ?: "$0.00"

            if (!productImageUrl.isNullOrEmpty()) {
                Glide.with(this).load(productImageUrl).into(imageView)
            }

        }


        confirmButton.setOnClickListener {
            val userName = nameEditText.text.toString().trim()
            val userEmail = emailEditText.text.toString().trim()
            val userAddress = addressEditText.text.toString().trim()


            // Validar que los campos no estén vacíos
            if (userName.isEmpty() || userEmail.isEmpty() || userAddress.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar el formato del email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Crear objeto de compra con la cantidad
            val purchase = PurchaseEntity(
                name = productName ?: "Nombre no disponible",
                price = productPrice ?: "$0.00",
                imageUrl = productImageUrl ?: "",
                quantity = totalQuantity
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