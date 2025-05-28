package com.example.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.ProductEntity
import com.example.app.databinding.ActivityProductFormBinding
import kotlinx.coroutines.launch

class ProductFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductFormBinding
    private var editingProductId: Int? = null
    private val dao by lazy {
        AppDatabase.getDatabase(this).productDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar producto si se está editando
        editingProductId = intent.getIntExtra("PRODUCT_ID", -1).takeIf { it != -1 }

        editingProductId?.let { loadProduct(it) }

        // Cargar imagen al cambiar el texto
        binding.etImageUrl.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) loadImagePreview()
        }

        binding.btnSave.setOnClickListener {
            saveProduct()
        }
    }

    private fun loadImagePreview() {
        val url = binding.etImageUrl.text.toString().trim()
        if (url.isNotEmpty()) {
            Glide.with(this)
                .load(url)
                .into(binding.ivPreview)
        }
    }

    private fun loadProduct(id: Int) = lifecycleScope.launch {
        val product = dao.getById(id)
        product?.let {
            binding.etName.setText(it.name)
            binding.etPrice.setText(it.price)
            binding.etImageUrl.setText(it.imageUrl)
            binding.etDescription.setText(it.description)
            loadImagePreview()
        }
    }

    private fun saveProduct() = lifecycleScope.launch {
        val name = binding.etName.text.toString().trim()
        val price = binding.etPrice.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (name.isBlank() || price.isBlank() || imageUrl.isBlank() || description.isBlank()) {
            Toast.makeText(this@ProductFormActivity, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return@launch
        }

        val entity = ProductEntity(
            id = editingProductId ?: (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            name = name,
            imageUrl = imageUrl,
            price = price,
            description = description,
            isInCart = false,
            quantity = 1
        )

        dao.insert(entity)

        Toast.makeText(this@ProductFormActivity, "Producto guardado", Toast.LENGTH_SHORT).show()
        finish()
    }

}