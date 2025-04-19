package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app.model.Product

class PopularAdapter( private val items: List<Product>,
                      private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    inner class PopularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.productImage)
        val title: TextView = itemView.findViewById(R.id.productTitle)

        fun bind(product: Product) {
            title.text = product.name
            Glide.with(itemView.context).load(product.imageUrl).into(image)

            itemView.setOnClickListener {
                onClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_popular, parent, false)
        return PopularViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

}