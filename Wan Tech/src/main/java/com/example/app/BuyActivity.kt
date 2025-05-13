package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app.data.local.ProductEntity
import com.example.app.data.local.PurchaseEntity
import com.example.app.utils.toSafePriceDouble

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
        val totalTextView = findViewById<TextView>(R.id.totalPriceTextView)


        // ViewModel
        purchaseViewModel = ViewModelProvider(this)[PurchaseViewModel::class.java]

        // Obtener productos
        @Suppress("DEPRECATION")
        val productList = intent.getParcelableArrayListExtra<ProductEntity>("selectedProducts")

        var productName: String? = null
        var productPrice: String? = null
        var productImageUrl: String? = null
        var totalQuantity = 1

        if (!productList.isNullOrEmpty()) {
            if (productList.size == 1) {
                val product = productList[0]

                nameView.visibility = View.VISIBLE
                priceView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                totalTextView.visibility = View.GONE

                productName = product.name
                val subtotal = product.price.toSafePriceDouble() * product.quantity
                productPrice = "Subtotal: $${"%.2f".format(subtotal)} (${product.quantity} x $${"%.2f".format(product.price.toSafePriceDouble())})"
                productImageUrl = product.imageUrl
                totalQuantity = product.quantity

                nameView.text = productName
                priceView.text = productPrice
                Glide.with(this).load(productImageUrl).into(imageView)

            } else {
                recyclerView.visibility = View.VISIBLE
                nameView.visibility = View.GONE
                priceView.visibility = View.GONE
                imageView.visibility = View.GONE
                totalTextView.visibility = View.VISIBLE

                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = BuyProductAdapter(productList)

                val total = productList.sumOf {
                    it.price.toSafePriceDouble() * it.quantity
                }

                productName = productList.joinToString("\n") { "${it.name} x${it.quantity}" }
                productPrice = "Total: $${"%.2f".format(total)}"
                productImageUrl = null
                totalQuantity = productList.sumOf { it.quantity }

                totalTextView.text = productPrice

            }

        } else {
            // En caso de que venga desde ProductActivity individual
            nameView.visibility = View.VISIBLE
            priceView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            totalTextView.visibility = View.GONE

            productName = intent.getStringExtra("name")
            productPrice = intent.getStringExtra("price")
            productImageUrl = intent.getStringExtra("imageUrl")

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

            if (userName.isEmpty() || userEmail.isEmpty() || userAddress.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val purchase = PurchaseEntity(
                name = productName ?: "Nombre no disponible",
                price = productPrice ?: "$0.00",
                imageUrl = productImageUrl ?: "",
                quantity = totalQuantity
            )

            purchaseViewModel.savePurchase(purchase)

            Toast.makeText(this, "¡Compra confirmada!", Toast.LENGTH_LONG).show()

            startActivity(Intent(this, ProductActivity::class.java))
            finish()
        }

    }

}