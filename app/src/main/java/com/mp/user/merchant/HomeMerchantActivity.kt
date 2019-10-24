package com.mp.user.merchant

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mp.MainActivity
import com.mp.R
import com.mp.model.Session
import com.mp.model.User

class HomeMerchantActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_merchant)
        val session = Session(this)
        val navController = findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val logout : ImageButton = findViewById(R.id.logout)



        logout.setOnClickListener {
            session.saveString("phone", "")
            session.saveString("email", "")
            session.saveString("name", "")
            session.saveString("pin", "")
            session.saveInteger("status", 0)
            session.saveInteger("type", 0)

            User.setPhone("")
            User.setEmail("")
            User.setName("")
            User.setPin("")
            User.setType(0)
            User.setStatus(0)
            val goTo = Intent(this, MainActivity::class.java)
            startActivity(goTo)
            finish()
        }

        navView.setupWithNavController(navController)
    }
}
