package com.example.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException

class BuyActivity: AppCompatActivity() {

    private lateinit var purchaseViewModel: PurchaseViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val CAMERA_REQUEST_CODE = 101
    private lateinit var nameEditText: EditText
    private lateinit var idNumberEditText: EditText
    private lateinit var emailEditText: EditText
    private var photoUri: Uri? = null

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
        val scanButton = findViewById<ImageButton>(R.id.scanButton)

        // Nuevas referencias a los campos de entrada
        nameEditText = findViewById(R.id.nameEditText)
        idNumberEditText = findViewById(R.id.idNumberEditText)
        emailEditText = findViewById(R.id.emailEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val recyclerView = findViewById<RecyclerView>(R.id.productsRecyclerView)
        val totalTextView = findViewById<TextView>(R.id.totalPriceTextView)


        // ViewModel
        purchaseViewModel = ViewModelProvider(this)[PurchaseViewModel::class.java]

        // Configurar escáner fuera del botón de confirmar
        scanButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            } else {
                val photoFile = File.createTempFile(
                    "cedula_scan", ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )

                photoUri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
            }
        }

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

            val productosParaFactura = if (!productList.isNullOrEmpty()) {
                ArrayList(productList)
            } else {
                arrayListOf(
                    ProductEntity(
                        id = 0, // Puedes poner 0 o un valor temporal
                        name = productName ?: "Sin nombre",
                        imageUrl = productImageUrl ?: "",
                        price = productPrice?.replace(Regex("[^\\d.]"), "") ?: "0.0",
                        description = "Descripción no disponible", // Ajusta si tienes una descripción real
                        isInCart = false,
                        quantity = totalQuantity
                    )
                )
            }

            val intentFactura = Intent(this, FacturaActivity::class.java).apply {
                putExtra("email", userEmail)
                putExtra("direccion", userAddress)
                putExtra("producto", productName)
                putExtra("precio", productPrice)
                putExtra("cantidad", totalQuantity)
                putExtra("imageUrl", productImageUrl ?: "")
                putExtra("nombreTienda", "TECNOLOGY STORE")
                putExtra("nitTienda", "123456789-0")
                putExtra("nombre", userName)
                putExtra("cedula", idNumberEditText.text.toString())
                putExtra("total", productPrice)
                putParcelableArrayListExtra("productos", productosParaFactura)
            }
            startActivity(intentFactura)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            photoUri?.let { uri ->
                val inputImage = InputImage.fromFilePath(this, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        var cedula = ""
                        var nombres = ""
                        var apellidos = ""

                        val lines = visionText.textBlocks.flatMap { it.lines.map { it.text.trim() } }

                        // Extraer cédula usando Regex
                        val cedulaRegex = Regex("N[ÚU]MERO[:\\s]*([0-9\\.]{6,15})")
                        lines.forEach { line ->
                            val match = cedulaRegex.find(line.uppercase())
                            if (match != null) {
                                val rawCedula = match.groupValues[1]
                                cedula = rawCedula.replace(".", "")
                            }
                        }

                        // 2. Extraer nombres y apellidos usando la línea anterior a la etiqueta
                        for (i in 1 until lines.size) {
                            val currentLine = lines[i].uppercase()

                            if (currentLine.contains("NOMBRES")) {
                                nombres = limpiarTexto(lines[i - 1])
                            }

                            if (currentLine.contains("APELLIDOS")) {
                                apellidos = limpiarTexto(lines[i - 1])
                            }
                        }

                        // 3. Asignar a campos
                        if (nombres.isNotBlank() || apellidos.isNotBlank()) {
                            nameEditText.setText("$nombres $apellidos".trim())
                        }

                        if (cedula.isNotBlank()) {
                            idNumberEditText.setText(cedula)
                        }

                        if (cedula.isBlank() && nombres.isBlank() && apellidos.isBlank()) {
                            Toast.makeText(this, "No se reconoció información válida", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al escanear texto", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun limpiarTexto(texto: String): String {
        return texto
            .replace("[^A-Za-zÁÉÍÓÚÑáéíóúñ\\s]".toRegex(), "") // solo letras y espacios
            .replace("\\s+".toRegex(), " ") // un solo espacio entre palabras
            .trim()
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
