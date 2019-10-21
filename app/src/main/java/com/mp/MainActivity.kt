package com.mp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import com.mp.password.edit.EditPasswordActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.glxn.qrgen.android.QRCode

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton : Button = findViewById(R.id.login)
        val registerButton : Button = findViewById(R.id.register)
        val forgotPassword : Button = findViewById(R.id.forgotPassword)

        doRequestPermission()

        loginButton.setOnClickListener {
            doRequestPermission()
            val goTo = Intent(this, VerifyLoginActivity::class.java)
            startActivity(goTo)
            finish()
        }

        registerButton.setOnClickListener {
            doRequestPermission()
            val goTo = Intent(this, RegisterUserActivity::class.java)
            startActivity(goTo)
        }

        forgotPassword.setOnClickListener {
            doRequestPermission()
            val goTo = Intent(this, EditPasswordActivity::class.java)
            startActivity(goTo)
        }
    }

    @SuppressLint("NewApi")
    private fun doRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
        }
    }
}
