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
import com.example.app.data.local.PurchaseEntity
import com.example.app.model.Product
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale
import android.location.Geocoder

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

        // Campos de entrada
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val recyclerView = findViewById<RecyclerView>(R.id.productsRecyclerView)

        // ViewModel
        purchaseViewModel = ViewModelProvider(this)[PurchaseViewModel::class.java]

        // Configurar el botón de ubicación
        locationButton.setOnClickListener {
            obtenerUbicacionActual(addressEditText)
        }

        // Resto de tu código existente para manejar los productos...
        @Suppress("DEPRECATION")
        val productList = intent.getParcelableArrayListExtra<Product>("selectedProducts")

        var productName: String? = null
        var productPrice: String? = null
        var productImageUrl: String? = null
        var totalQuantity = 1

        if (!productList.isNullOrEmpty()) {
            if (productList.size == 1) {
                // Código existente para un producto...
            } else {
                // Código existente para múltiples productos...
            }
        } else {
            // Código existente para producto individual vía extras...
        }

        confirmButton.setOnClickListener {
            // Tu código existente de validación y confirmación...
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