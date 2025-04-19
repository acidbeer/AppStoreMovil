package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.model.Product
import com.bumptech.glide.Glide

class ProductActivity : AppCompatActivity() {

    private lateinit var featuredProduct: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        featuredProduct = Product(
            id = 1,
            name = "Asus TUF Gaming",
            imageUrl = "https://i.imgur.com/ULv2p2Z.png",
            price = "$2.899.000",
            description = "Laptop gamer con 16GB RAM, SSD 512GB, NVIDIA RTX 3050..."
        )

        val imageView = findViewById<ImageView>(R.id.featuredImage)
        val titleView = findViewById<TextView>(R.id.featuredTitle)
        val priceView = findViewById<TextView>(R.id.featuredPrice)

        titleView.text = featuredProduct.name
        priceView.text = featuredProduct.price
        Glide.with(this).load(featuredProduct.imageUrl).into(imageView)

        imageView.setOnClickListener {
            goToDetail(featuredProduct)
        }

        // Lista horizontal
        val popularRecycler = findViewById<RecyclerView>(R.id.popularRecycler)
        popularRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        popularRecycler.adapter = PopularAdapter(getPopularProducts()) { product ->
            goToDetail(product)
        }
    }

    private fun getPopularProducts(): List<Product> {
        return listOf(
            Product(2, "iPhone 14", "https://i.imgur.com/dR9lEqC.png", "$4.599.000", "Nuevo iPhone con chip A15, pantalla OLED"),
            Product(3, "Smartwatch Xiaomi", "https://i.imgur.com/8N2D8MN.png", "$349.000", "Resistente al agua, con monitoreo cardíaco"),
            Product(4, "Samsung Galaxy S22", "https://i.imgur.com/WBh1Icy.png", "$3.999.000", "Pantalla AMOLED, cámara de 50MP")
        )
    }

    private fun goToDetail(product: Product) {
        val intent = Intent(this, ProductDetailActivity::class.java).apply {
            putExtra("id", product.id)
            putExtra("name", product.name)
            putExtra("imageUrl", product.imageUrl)
            putExtra("price", product.price)
            putExtra("description", product.description)
        }
        startActivity(intent)
    }

}