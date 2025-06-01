package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
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

        // Inicializar Spinner
        val filterOptions = resources.getStringArray(R.array.price_filter_options)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFilterType.adapter = spinnerAdapter

        // Botón buscar por nombre
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            searchByName(query)
        }

        // Botón filtrar por precio
        binding.btnFilterPrice.setOnClickListener {
            val rawPrice = binding.etMaxPrice.text.toString()
            val filterType = binding.spFilterType.selectedItem.toString()

            if (rawPrice.isBlank()) {
                Toast.makeText(this, "Ingresa un precio válido", Toast.LENGTH_SHORT).show()
            } else {
                filterByPrice(rawPrice, filterType)
            }
        }

        // Recycler y adapter
        adapter = ProductAdminAdapter(onEditClick = { product -> goToEdit(product) })
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter

        // Botón agregar producto
        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(this, ProductFormActivity::class.java))
        }

        setSupportActionBar(binding.toolbarAdmin)

        loadProducts()
    }

    private fun loadProducts() = lifecycleScope.launch {
        val dao = AppDatabase.getDatabase(this@AdminDashboardActivity).productDao()
        val list = withContext(Dispatchers.IO) { dao.getAll() }
        adapter.submitList(list)
    }

    private fun searchByName(query: String) = lifecycleScope.launch {
        val dao = AppDatabase.getDatabase(this@AdminDashboardActivity).productDao()
        val list = withContext(Dispatchers.IO) {
            dao.searchByName(query)
        }
        adapter.submitList(list)
    }

    private fun filterByPrice(value: String, filterType: String) = lifecycleScope.launch {
        val dao = AppDatabase.getDatabase(this@AdminDashboardActivity).productDao()
        val allProducts = withContext(Dispatchers.IO) { dao.getAll() }

        // Intentar convertir el valor ingresado a Double (permitiendo ',' o '.')
        val inputPrice = value.trim().replace(",", ".").toDoubleOrNull()
        if (inputPrice == null) {
            Toast.makeText(this@AdminDashboardActivity, "Precio inválido. Usa punto decimal (.)", Toast.LENGTH_SHORT).show()
            return@launch
        }

        // Filtrar en memoria según el tipo
        val filtered = allProducts.filter {
            val productPrice = it.price.trim().replace(",", ".").toDoubleOrNull()
            if (productPrice != null) {
                when (filterType) {
                    "Menor a" -> productPrice < inputPrice
                    "Mayor a" -> productPrice > inputPrice
                    "Igual a" -> productPrice == inputPrice
                    else -> false
                }
            } else {
                false
            }
        }

        adapter.submitList(filtered)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            R.id.action_view_as_user -> {
                val intent = Intent(this, ProductActivity::class.java)
                intent.putExtra("fromAdmin", true)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}