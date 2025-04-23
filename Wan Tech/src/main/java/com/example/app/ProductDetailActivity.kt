package com.example.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.ProductEntity
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalle del producto"

        toolbar.setNavigationOnClickListener {
            finish() // o onBackPressedDispatcher.onBackPressed() para API 33+
        }

        val id = intent.getIntExtra("id", 0)
        val name = intent.getStringExtra("name") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val price = intent.getStringExtra("price") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        findViewById<TextView>(R.id.productName).text = name
        findViewById<TextView>(R.id.productPrice).text = price
        findViewById<TextView>(R.id.productDescription).text = description
        Glide.with(this).load(imageUrl).into(findViewById(R.id.productImage))

        db = AppDatabase.getDatabase(this)

        findViewById<Button>(R.id.buyButton).setOnClickListener {
            lifecycleScope.launch {
                db.productDao().insert(ProductEntity(id, name, imageUrl, price, description, isInCart = false))
                Toast.makeText(this@ProductDetailActivity, "¡Mas Cerca De Tu Producto!", Toast.LENGTH_SHORT).show()


                // Ir a la pantalla de compra
                val intent = Intent(this@ProductDetailActivity, BuyActivity::class.java).apply {
                    putExtra("name", name)
                    putExtra("price", price)
                    putExtra("imageUrl", imageUrl)
                }
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.cartButton).setOnClickListener {
            lifecycleScope.launch {
                val existingProduct = db.productDao().getById(id)
                if (existingProduct?.isInCart == true) {
                    Toast.makeText(this@ProductDetailActivity, "Este producto ya está en tu carrito.", Toast.LENGTH_SHORT).show()
                } else {
                    db.productDao().insert(ProductEntity(id, name, imageUrl, price, description, isInCart = true))
                    Toast.makeText(this@ProductDetailActivity, "Producto agregado al carrito.", Toast.LENGTH_SHORT).show()
                }

                // Redirigir al carrito, sin importar si ya estaba o se acaba de agregar
                Log.d("ProductDetailActivity", "Redirigiendo al carrito...")
                val intent = Intent(this@ProductDetailActivity, CartActivity::class.java)
                startActivity(intent)

            }
        }
    }
}