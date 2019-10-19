package com.mp.user.menu

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.mp.R
import com.mp.qr.QRCode

class MPayIdActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpay_id)

        val username = "null"
        val phoneNumber = "081211610807"

        val qrCode = QRCode(username).qrToBitmap()
        val usernameTextView : TextView = findViewById(R.id.username)
        val phoneNumberTextView : TextView = findViewById(R.id.phoneNumber)
        val barcodeQR : ImageView = findViewById(R.id.barcodeQR)

        usernameTextView.text = username
        phoneNumberTextView.text = phoneNumber

        barcodeQR.setImageBitmap(qrCode)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
