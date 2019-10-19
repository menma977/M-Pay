package com.mp.user.merchant

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mp.MainActivity
import com.mp.R

class HomeMerchantActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_merchant)
        val navController = findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val logout : ImageButton = findViewById(R.id.logout)

        logout.setOnClickListener {
            val goTo = Intent(this, MainActivity::class.java)
            startActivity(goTo)
            finish()
        }


        navView.setupWithNavController(navController)
    }
}
