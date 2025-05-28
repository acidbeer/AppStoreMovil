package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app.data.local.ProductEntity

class ProductAdminAdapter(
    private val onEditClick: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductAdminAdapter.ProductViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(product: ProductEntity) {
            itemView.findViewById<TextView>(R.id.tvName).text = product.name
            itemView.findViewById<TextView>(R.id.tvPrice).text = "$${product.price}"
            itemView.setOnClickListener { onEditClick(product) }
            itemView.findViewById<View>(R.id.btnEdit).setOnClickListener {
                onEditClick(product)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductEntity>() {
        override fun areItemsTheSame(old: ProductEntity, new: ProductEntity) = old.id == new.id
        override fun areContentsTheSame(old: ProductEntity, new: ProductEntity) = old == new
    }

}