package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app.model.Product

class BuyProductAdapter(
    private val productList: List<Product>
): RecyclerView.Adapter<BuyProductAdapter.ProductViewHolder>()  {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewProductBuy)
        val nameView: TextView = itemView.findViewById(R.id.textViewProductNameBuy)
        val quantityView: TextView = itemView.findViewById(R.id.textViewProductQuantityBuy)
        val priceView: TextView = itemView.findViewById(R.id.textViewProductPriceBuy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_buy, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.nameView.text = product.name
        holder.quantityView.text = "x${product.quantity}"
        holder.priceView.text = "$${"%.2f".format(product.price.toDoubleOrNull() ?: 0.0)}"

        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = productList.size
}