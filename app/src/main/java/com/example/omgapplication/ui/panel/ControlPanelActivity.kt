package com.example.omgapplication.ui.panel

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.omgapplication.R
import com.example.omgapplication.databinding.ActivityControlPanelBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ControlPanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityControlPanelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityControlPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        window.navigationBarColor = android.graphics.Color.parseColor("#F8F8F8")
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

        supportActionBar?.hide()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_control_panel) as NavHostFragment
        val navController = navHostFragment.navController

        val navView: BottomNavigationView = binding.navView2

        val appBarConfiguration = AppBarConfiguration(
            setOf(
               R.id.navigation_back, R.id.navigation_addAnimal, R.id.navigation_viewReport
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_back -> {
                    onBackPressed()
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}