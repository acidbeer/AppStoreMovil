package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("id", featuredProduct.id)
                putExtra("name", featuredProduct.name)
                putExtra("imageUrl", featuredProduct.imageUrl)
                putExtra("price", featuredProduct.price)
                putExtra("description", featuredProduct.description)
            }
            startActivity(intent)
        }
    }
}