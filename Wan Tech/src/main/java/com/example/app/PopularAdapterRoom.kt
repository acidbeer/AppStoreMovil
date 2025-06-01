package com.example.app

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app.data.local.ProductEntity

class PopularAdapterRoom(private val onClick: (ProductEntity) -> Unit
) : RecyclerView.Adapter<PopularAdapterRoom.PopularViewHolder>() {

    private var items: List<ProductEntity> = emptyList()

    fun submitList(newItems: List<ProductEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class PopularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.productImage)
        private val title: TextView = itemView.findViewById(R.id.productTitle)
        private val price: TextView = itemView.findViewById(R.id.productPrice)

        fun bind(product: ProductEntity) {
            title.text = product.name
            price.text = product.price
            Log.d("PopularAdapterRoom", "URL: ${product.imageUrl}")
            Glide.with(itemView.context).load(product.imageUrl).into(image)

            itemView.setOnClickListener {
                onClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popular, parent, false)
        return PopularViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

}