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


class CartAdapter(private var products: MutableList<ProductEntity>,
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
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun getCurrentProducts(): List<ProductEntity> = products.toList() // copia segura

    fun updateData(newProducts: List<ProductEntity>) {
        products = newProducts.map { it.copy() }.toMutableList()
        notifyDataSetChanged()
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productQuantity: EditText = itemView.findViewById(R.id.productQuantity)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        private var currentWatcher: TextWatcher? = null

        fun bind(product: ProductEntity) {
            productName.text = product.name
            productPrice.text = product.price
            Glide.with(itemView.context).load(product.imageUrl).into(productImage)

            // Eliminar watcher previo antes de actualizar el texto
            currentWatcher?.let {
                productQuantity.removeTextChangedListener(it)
            }

            productQuantity.setText(product.quantity.toString())

            currentWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val quantity = s?.toString()?.toIntOrNull() ?: 1
                    if (product.quantity != quantity && quantity > 0) {
                        product.quantity = quantity
                        listener.onProductQuantityChanged(product)
                    }
                }
            }

            productQuantity.addTextChangedListener(currentWatcher)

            deleteButton.setOnClickListener {
                listener.onDeleteProduct(product)
            }
        }
    }

}