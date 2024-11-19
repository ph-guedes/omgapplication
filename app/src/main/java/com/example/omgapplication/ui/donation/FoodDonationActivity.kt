package com.example.omgapplication.ui.donation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omgapplication.R
import com.example.omgapplication.databinding.ActivityFoodDonationBinding
import com.example.omgapplication.entities.Locations
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class FoodDonationActivity : AppCompatActivity(), FoodDonationAdapter.OnLocationClickListener {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityFoodDonationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.navigationBarColor = android.graphics.Color.WHITE

        binding = ActivityFoodDonationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
        }

        listLocations()
    }

    private fun listLocations() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val listLocations = listOf(
            Locations("Abrigo dos Bichinhos", "Segunda a Sexta:\n9h às 17h",-7.831276909879499, -35.25180162698176),
            Locations("SOS Animais", "Segunda a Domingo:\n10h às 18h",-8.024037132672277, -34.9244994438708),
            Locations("Pata-Azul", "Segunda a Quinta:\n7h às 16h", -7.812730550188992, -35.27320046804759),
            Locations("Cor Cristal", "Segunda a Quarta:\n9h às 18h",-7.823793679004197, -34.889209981618095),
            Locations("Pet360", "Segunda a Sábado:\n9h às 12h", -7.935340306361865, -34.84526466804758),
            Locations("Salvação", "Segunda a Sexta:\n6h às 12h", -7.952916043713282, -35.38116320968659),
            Locations("Salve o Caramelo", "Segunda a Sábado:\n11h às 16h", -8.094445807116728, -35.27922462737928)
        )

        recyclerView.adapter = FoodDonationAdapter(listLocations, this)
    }

    override fun onLocationClicked(latitude: Double, longitude: Double) {
        val location = LatLng(latitude, longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        googleMap.addMarker(MarkerOptions().position(location).title("Localização"))
    }
}
