package com.example.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.ProductEntity
import com.example.app.databinding.ActivityAdminDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminDashboardActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var adapter: ProductAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Validar el rol DESPUÉS de haber inicializado el binding
        val role = intent.getStringExtra("USER_ROLE")
        if (role != "admin") {
            finish()
            return
        }

        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            searchByName(query)
        }

        binding.btnFilterPrice.setOnClickListener {
            filterByPrice("2000000")
        }

        adapter = ProductAdminAdapter(
            onEditClick = { product -> goToEdit(product) }
        )

        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter

        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(this, ProductFormActivity::class.java))
        }

        loadProducts()
    }

    private fun loadProducts() = lifecycleScope.launch {
        val dao = AppDatabase.getDatabase(this@AdminDashboardActivity).productDao()
        val list = withContext(Dispatchers.IO) {
            dao.getAll() //Este es el nombre correcto del metodo en tu ProductDao
        }
        adapter.submitList(list)
    }

    private fun goToEdit(product: ProductEntity) {
        val intent = Intent(this, ProductFormActivity::class.java)
        intent.putExtra("PRODUCT_ID", product.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }
    private fun searchByName(query: String) = lifecycleScope.launch {
        val dao = AppDatabase.getDatabase(this@AdminDashboardActivity).productDao()
        val list = withContext(Dispatchers.IO) {
            dao.searchByName(query)
        }
        adapter.submitList(list)
    }

    private fun filterByPrice(max: String) = lifecycleScope.launch {
        val dao = AppDatabase.getDatabase(this@AdminDashboardActivity).productDao()
        val list = withContext(Dispatchers.IO) {
            dao.filterByPrice(max)
        }
        adapter.submitList(list)
    }
}