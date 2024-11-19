package com.example.omgapplication.ui.donation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.omgapplication.R
import com.example.omgapplication.databinding.ActivityMoneyDonationBinding

class MoneyDonationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoneyDonationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        window.navigationBarColor = android.graphics.Color.WHITE
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    window.decorView.systemUiVisibility or
                            android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    )
        }

        binding = ActivityMoneyDonationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

}