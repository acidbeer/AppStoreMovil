package com.example.app

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app.data.local.ProductEntity
import com.example.app.utils.toSafePriceDouble

class CartAdapter(private var products: List<ProductEntity>,
                  private val listener: OnProductClickListener):
    RecyclerView.Adapter<CartAdapter.CartViewHolder>(){

    interface OnProductClickListener {
        fun onDeleteProduct(product: ProductEntity)
        fun onProductQuantityChanged(product: ProductEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = products[position]
        holder.productName.text = product.name
        holder.productPrice.text = product.price
        Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.productImage)

        // Inicializar el campo de cantidad (esto puede venir de la base de datos si es necesario)
        holder.productQuantity.setText(product.quantity.toString())

        // Escuchar los cambios en la cantidad del producto
        holder.productQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val quantity = editable.toString().toIntOrNull() ?: 1
                product.quantity = quantity // Actualiza el valor de la cantidad
                listener.onProductQuantityChanged(product) // Notifica a la actividad que la cantidad cambió
                updateTotalPrice() // Actualiza el precio total
            }
        })

        // Manejo del botón de eliminar
        holder.deleteButton.setOnClickListener {
            listener.onDeleteProduct(product)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun getCurrentProducts(): List<ProductEntity> {
        return products
    }

    fun updateData(newProducts: List<ProductEntity>) {
        products = newProducts
        notifyDataSetChanged()
    }
    // Metodo para actualizar el precio total
    private fun updateTotalPrice() {
        val total = products.sumOf { it.price.toSafePriceDouble() * it.quantity }
        // Actualiza el total en la actividad que contiene el RecyclerView
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productQuantity: EditText = itemView.findViewById(R.id.productQuantity)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)  // Botón para eliminar
    }

}