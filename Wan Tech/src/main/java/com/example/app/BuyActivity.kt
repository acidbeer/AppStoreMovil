package com.example.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app.data.local.ProductEntity
import com.example.app.data.local.PurchaseEntity
import com.example.app.utils.toSafePriceDouble
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import android.location.Geocoder
import java.io.IOException

class BuyActivity: AppCompatActivity() {

    private lateinit var purchaseViewModel: PurchaseViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)

        // Inicializar FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Referencias a los views
        val imageView = findViewById<ImageView>(R.id.productImageBuy)
        val nameView = findViewById<TextView>(R.id.productNameBuy)
        val priceView = findViewById<TextView>(R.id.productPriceBuy)
        val confirmButton = findViewById<android.widget.Button>(R.id.confirmBuyButton)
        val locationButton = findViewById<ImageButton>(R.id.locationButton)

        // Nuevas referencias a los campos de entrada
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val recyclerView = findViewById<RecyclerView>(R.id.productsRecyclerView)
        val totalTextView = findViewById<TextView>(R.id.totalPriceTextView)


        // ViewModel
        purchaseViewModel = ViewModelProvider(this)[PurchaseViewModel::class.java]

        // Configurar el botón de ubicación
        locationButton.setOnClickListener {
            obtenerUbicacionActual(addressEditText)
        }

        // Obtener productos
        @Suppress("DEPRECATION")
        val productList = intent.getParcelableArrayListExtra<ProductEntity>("selectedProducts")

        var productName: String? = null
        var productPrice: String? = null
        var productImageUrl: String? = null
        var totalQuantity = 1

        if (!productList.isNullOrEmpty()) {
            if (productList.size == 1) {
                val product = productList[0]

                nameView.visibility = View.VISIBLE
                priceView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                totalTextView.visibility = View.GONE

                productName = product.name
                val subtotal = product.price.toSafePriceDouble() * product.quantity
                productPrice = "Subtotal: $${"%.2f".format(subtotal)} (${product.quantity} x $${
                    "%.2f".format(product.price.toSafePriceDouble())
                })"
                productImageUrl = product.imageUrl
                totalQuantity = product.quantity

                nameView.text = productName
                priceView.text = productPrice
                Glide.with(this).load(productImageUrl).into(imageView)

            } else {
                recyclerView.visibility = View.VISIBLE
                nameView.visibility = View.GONE
                priceView.visibility = View.GONE
                imageView.visibility = View.GONE
                totalTextView.visibility = View.VISIBLE

                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = BuyProductAdapter(productList)

                val total = productList.sumOf {
                    it.price.toSafePriceDouble() * it.quantity
                }

                productName = productList.joinToString("\n") { "${it.name} x${it.quantity}" }
                productPrice = "Total: $${"%.2f".format(total)}"
                productImageUrl = null
                totalQuantity = productList.sumOf { it.quantity }

                totalTextView.text = productPrice

            }

        } else {
            // En caso de que venga desde ProductActivity individual
            nameView.visibility = View.VISIBLE
            priceView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            totalTextView.visibility = View.GONE

            productName = intent.getStringExtra("name")
            productPrice = intent.getStringExtra("price")
            productImageUrl = intent.getStringExtra("imageUrl")

            nameView.text = productName ?: "Nombre no disponible"
            priceView.text = productPrice ?: "$0.00"

            if (!productImageUrl.isNullOrEmpty()) {
                Glide.with(this).load(productImageUrl).into(imageView)
            }
        }

        confirmButton.setOnClickListener {
            val userName = nameEditText.text.toString().trim()
            val userEmail = emailEditText.text.toString().trim()
            val userAddress = addressEditText.text.toString().trim()

            if (userName.isEmpty() || userEmail.isEmpty() || userAddress.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val purchase = PurchaseEntity(
                name = productName ?: "Nombre no disponible",
                price = productPrice ?: "$0.00",
                imageUrl = productImageUrl ?: "",
                quantity = totalQuantity
            )

            purchaseViewModel.savePurchase(purchase)

            Toast.makeText(this, "¡Compra confirmada!", Toast.LENGTH_LONG).show()

            startActivity(Intent(this, ProductActivity::class.java))
            finish()
        }
    }

    private fun obtenerUbicacionActual(addressEditText: EditText) {
        // Verificar permisos
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permisos si no los tenemos
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Ya tenemos permisos, obtener ubicación
            obtenerUbicacion(addressEditText)
        }
    }

    private fun obtenerUbicacion(addressEditText: EditText) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Convertir coordenadas a dirección
                    convertirCoordenadasADireccion(location.latitude, location.longitude, addressEditText)
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación. Asegúrate de tener el GPS activado", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun convertirCoordenadasADireccion(latitud: Double, longitud: Double, addressEditText: EditText) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitud, longitud, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val direccion = address.getAddressLine(0)
                addressEditText.setText(direccion)
            } else {
                Toast.makeText(this, "No se encontró una dirección para esta ubicación", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Error al obtener la dirección", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, obtener ubicación
                obtenerUbicacion(findViewById(R.id.addressEditText))
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
