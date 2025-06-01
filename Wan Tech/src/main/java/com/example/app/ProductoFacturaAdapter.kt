package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app.data.local.ProductEntity

class ProductoFacturaAdapter(private val productos: List<ProductEntity>) :
    RecyclerView.Adapter<ProductoFacturaAdapter.ProductoViewHolder>()  {

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val cantidad: TextView = itemView.findViewById(R.id.tvCantidadProducto)
        val subtotal: TextView = itemView.findViewById(R.id.tvSubtotalProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_factura, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.name
        holder.cantidad.text = "Cantidad: ${producto.quantity}"
        val subtotal = producto.quantity * (producto.price.toDoubleOrNull() ?: 0.0)
        holder.subtotal.text = "Subtotal: $${"%.2f".format(subtotal)}"
    }

    override fun getItemCount(): Int = productos.size

}