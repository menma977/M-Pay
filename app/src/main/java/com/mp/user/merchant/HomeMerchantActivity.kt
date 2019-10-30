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
import com.mp.controller.UserController
import com.mp.model.Session
import com.mp.model.User
import java.util.*

class HomeMerchantActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_merchant)
        val session = Session(this)
        val navController = findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val logout: ImageButton = findViewById(R.id.logout)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                try {
                    val response =
                        UserController.Get(session.getString("phone").toString()).execute().get()
                    if (response["Status"].toString() == "0") {
                        if (response["emai"].toString() == session.getString("imei")) {
                            session.saveString("phone", response["hpagen"].toString())
                            session.saveString("email", response["email"].toString())
                            session.saveString("name", response["nama"].toString())
                            session.saveString("pin", response["password"].toString())
                            session.saveInteger(
                                "status",
                                response["statusmember"].toString().toInt()
                            )
                            session.saveInteger("type", response["tipeuser"].toString().toInt())
                            session.saveInteger("balance", response["deposit"].toString().toInt())

                            User.setPhone(response["hpagen"].toString())
                            User.setEmail(response["email"].toString())
                            User.setName(response["nama"].toString())
                            User.setPin(response["password"].toString())
                            User.setType(response["tipeuser"].toString().toInt())
                            User.setStatus(response["statusmember"].toString().toInt())
                            User.setBalance(response["deposit"].toString().toInt())
                        } else {
                            session.saveString("phone", "")
                            session.saveString("email", "")
                            session.saveString("name", "")
                            session.saveString("pin", "")
                            session.saveInteger("status", 0)
                            session.saveInteger("type", 0)
                            session.saveString("imei", "")

                            User.setPhone("")
                            User.setEmail("")
                            User.setName("")
                            User.setPin("")
                            User.setType(0)
                            User.setStatus(0)
                            val goTo = Intent(applicationContext, MainActivity::class.java)
                            startActivity(goTo)
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    session.saveString("phone", "")
                    session.saveString("email", "")
                    session.saveString("name", "")
                    session.saveString("pin", "")
                    session.saveInteger("status", 0)
                    session.saveInteger("type", 0)
                    session.saveString("imei", "")

                    User.setPhone("")
                    User.setEmail("")
                    User.setName("")
                    User.setPin("")
                    User.setType(0)
                    User.setStatus(0)
                    val goTo = Intent(applicationContext, MainActivity::class.java)
                    startActivity(goTo)
                    finish()
                }
            }
        }, 1000)

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
