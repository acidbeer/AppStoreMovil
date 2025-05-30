package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductActivity : AppCompatActivity() {

    private lateinit var featuredImage: ImageView
    private lateinit var featuredTitle: TextView
    private lateinit var featuredPrice: TextView
    private lateinit var popularRecycler: RecyclerView


    private lateinit var adapter: PopularAdapterRoom
    private var allProducts: List<ProductEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        featuredImage = findViewById(R.id.featuredImage)
        featuredTitle = findViewById(R.id.featuredTitle)
        featuredPrice = findViewById(R.id.featuredPrice)
        popularRecycler = findViewById(R.id.popularRecycler)

        adapter = PopularAdapterRoom { product ->
            goToDetail(product)
        }

        popularRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        popularRecycler.adapter = adapter

        handleBackPress()

        loadProductsFromDatabase()

    }

    private fun loadProductsFromDatabase() = lifecycleScope.launch {
        val dao = AppDatabase.getDatabase(this@ProductActivity).productDao()

        allProducts = withContext(Dispatchers.IO) {
            dao.getAll()
        }

        if (allProducts.isNotEmpty()) {
            val featuredProduct = allProducts.first()
            featuredTitle.text = featuredProduct.name
            featuredPrice.text = featuredProduct.price
            Glide.with(this@ProductActivity).load(featuredProduct.imageUrl).into(featuredImage)

            featuredImage.setOnClickListener {
                goToDetail(featuredProduct)
            }

            val remaining = allProducts.drop(1)
            adapter.submitList(remaining)
        }
    }

    private fun goToDetail(product: ProductEntity) {
        val intent = Intent(this, ProductDetailActivity::class.java).apply {
            putExtra("id", product.id)
            putExtra("name", product.name)
            putExtra("imageUrl", product.imageUrl)
            putExtra("price", product.price)
            putExtra("description", product.description)
        }
        startActivity(intent)
    }

    private fun handleBackPress() {
        val fromAdmin = intent.getBooleanExtra("fromAdmin", false)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (fromAdmin) {
                    Toast.makeText(
                        this@ProductActivity,
                        "Regresando a vista administrador",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Esto te devolverá a AdminDashboardActivity porque no la cerraste
                } else {
                    finish() // Comportamiento normal
                }
            }
        })
    }

}