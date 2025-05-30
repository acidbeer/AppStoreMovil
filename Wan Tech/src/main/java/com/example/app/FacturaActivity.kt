package com.example.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.data.local.ProductEntity

class FacturaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_factura)

        val nombreComprador = intent.getStringExtra("nombre")
        val cedulaComprador = intent.getStringExtra("cedula")
        val total = intent.getStringExtra("total")
        val productos = intent.getParcelableArrayListExtra<ProductEntity>("productos")

        findViewById<TextView>(R.id.tvNombre).text = nombreComprador
        findViewById<TextView>(R.id.tvCedula).text = cedulaComprador
        findViewById<TextView>(R.id.tvTienda).text = "TECNOLOGY STORE"
        findViewById<TextView>(R.id.tvNIT).text = "NIT: 900.123.456-7"
        findViewById<TextView>(R.id.tvTotal).text = total

        val recycler = findViewById<RecyclerView>(R.id.rvProductos)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ProductoFacturaAdapter(productos ?: listOf())
    }
}