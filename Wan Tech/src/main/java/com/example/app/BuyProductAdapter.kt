package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app.data.local.ProductEntity
import com.example.app.utils.toSafePriceDouble
import java.text.NumberFormat
import java.util.Locale


class BuyProductAdapter(
    private val productList: List<ProductEntity>
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

        val quantity = product.quantity
        val unitPrice = product.price.toSafePriceDouble()
        val subtotal = unitPrice * quantity

        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        val formattedUnitPrice = formatter.format(unitPrice)
        val formattedSubtotal = formatter.format(subtotal)

        holder.nameView.text = product.name
        holder.quantityView.text = "Cantidad: $quantity"
        holder.priceView.text = "Subtotal: $formattedSubtotal  ($formattedUnitPrice c/u)"

        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = productList.size
}