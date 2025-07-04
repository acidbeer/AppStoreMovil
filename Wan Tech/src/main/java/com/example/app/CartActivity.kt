package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.ProductEntity
import kotlinx.coroutines.launch
import com.example.app.utils.toSafePriceDouble
import java.text.NumberFormat
import java.util.ArrayList
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var finalizePurchaseButton: Button
    private lateinit var cartAdapter: CartAdapter // Asegúrate de crear un Adapter para el RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        db = AppDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.recyclerViewCart)
        totalPriceTextView = findViewById(R.id.totalPrice)
        checkoutButton = findViewById(R.id.checkoutButton)
        finalizePurchaseButton = findViewById(R.id.finalizePurchaseButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(mutableListOf(), object : CartAdapter.OnProductClickListener {
            override fun onDeleteProduct(product: ProductEntity) {
                deleteProductFromCart(product)
            }

            override fun onProductQuantityChanged(product: ProductEntity) {
                updateProductQuantity(product)
            }
        })
        recyclerView.adapter = cartAdapter

        // Cargar los productos en el carrito
        lifecycleScope.launch {
            val cartProducts = db.productDao().getCartItems()
            cartAdapter.updateData(cartProducts)
            updateTotalPrice(cartProducts)
        }


        checkoutButton.setOnClickListener {
            val intent = Intent(this@CartActivity, ProductActivity::class.java)
            startActivity(intent)
        }

        // Redirigir a la actividad de compra
        finalizePurchaseButton.setOnClickListener {
            // Obtener los productos en el carrito
            val cartProducts = cartAdapter.getCurrentProducts()

            // Pasar los productos a la BuyActivity
            val intent = Intent(this@CartActivity, BuyActivity::class.java)
            intent.putParcelableArrayListExtra("selectedProducts", ArrayList(cartProducts)) // Pasa los productos del carrito
            startActivity(intent)
        }
    }

    private fun updateTotalPrice(products: List<ProductEntity>) {
        val total = products.sumOf { it.price.toSafePriceDouble()* it.quantity }
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        val formatted = formatter.format(total)
        totalPriceTextView.text = "Total: $formatted"
    }

    private fun updateProductQuantity(product: ProductEntity) {
        lifecycleScope.launch {
            db.productDao().update(product) // Actualiza el producto con la nueva cantidad
            val updatedCart = db.productDao().getCartItems()
            cartAdapter.updateData(updatedCart)  // Actualiza la lista de productos
            updateTotalPrice(updatedCart)  // Recalcula el total
        }
    }
    private fun deleteProductFromCart(product: ProductEntity) {
        lifecycleScope.launch {
            // Desmarcar el producto como fuera del carrito
            db.productDao().update(product.copy(isInCart = false))

            // Obtener los productos actualizados del carrito
            val updatedCart = db.productDao().getCartItems()

            // Actualizar RecyclerView y total
            cartAdapter.updateData(updatedCart)
            updateTotalPrice(updatedCart)

            // Deshabilitar botón si ya no hay productos
            finalizePurchaseButton.isEnabled = updatedCart.isNotEmpty()

            if (updatedCart.isEmpty()) {
                Toast.makeText(this@CartActivity, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

}