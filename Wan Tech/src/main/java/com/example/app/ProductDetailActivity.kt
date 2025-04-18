package com.example.app

import android.os.Bundle
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
                Toast.makeText(this@ProductDetailActivity, "¡Producto comprado!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.cartButton).setOnClickListener {
            lifecycleScope.launch {
                db.productDao().insert(ProductEntity(id, name, imageUrl, price, description, isInCart = true))
                Toast.makeText(this@ProductDetailActivity, "Agregado al carrito", Toast.LENGTH_SHORT).show()
            }
        }
    }
}