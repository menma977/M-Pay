package com.mp.user.menu

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import com.mp.R
import com.mp.controller.UserController
import com.mp.model.Session
import com.mp.model.User
import com.mp.qr.QRCode
import java.util.*

class MPayIdActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpay_id)

        val session = Session(this)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                val response =
                    UserController.Get(session.getString("phone").toString()).execute().get()
                if (response["Status"].toString() == "0") {
                    runOnUiThread {
                        Handler().postDelayed({
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
                        }, 500)
                    }
                }
            }
        }, 1000)

        User.setPhone(session.getString("phone"))
        User.setEmail(session.getString("email"))
        User.setName(session.getString("name"))
        User.setPin(session.getString("pin"))
        User.setType(session.getInteger("type"))
        User.setStatus(session.getInteger("status"))

        val name = User.getName()
        val phoneNumber = User.getPhone()

        val qrCode = QRCode(phoneNumber).qrToBitmap()
        val usernameTextView: TextView = findViewById(R.id.username)
        val phoneNumberTextView: TextView = findViewById(R.id.phoneNumber)
        val barcodeQR: ImageView = findViewById(R.id.barcodeQR)

        usernameTextView.text = name
        phoneNumberTextView.text = phoneNumber

        barcodeQR.setImageBitmap(qrCode)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
