package com.example.omgapplication.ui.localization

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.omgapplication.R
import com.example.omgapplication.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        window.navigationBarColor = android.graphics.Color.WHITE
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                        android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                        android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    window.decorView.systemUiVisibility or
                            android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                            android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    )
        }

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.buttonConfirmLocation.setOnClickListener {
            confirmLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        if (latitude != 0.0 && longitude != 0.0) {
            val initialLocation = LatLng(latitude, longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15f))

            marker = mMap.addMarker(
                MarkerOptions()
                    .position(initialLocation)
                    .title("Sua localização")
            )

            mMap.setOnMapClickListener { latLng ->
                marker?.remove()
                marker = mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Localização selecionada")
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        } else {
            Toast.makeText(this, "Localização inicial inválida.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmLocation() {
        marker?.position?.let { selectedLocation ->
            val resultIntent = Intent().apply {
                putExtra("latitude", selectedLocation.latitude)
                putExtra("longitude", selectedLocation.longitude)
            }
            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Localização confirmada!", Toast.LENGTH_SHORT).show()
            finish()
        } ?: run {
            Toast.makeText(this, "Nenhuma localização selecionada.", Toast.LENGTH_SHORT).show()
        }
    }
}
