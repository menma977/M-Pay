package com.mp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mp.model.Session
import com.mp.model.User
import com.mp.password.edit.EditPasswordActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton : Button = findViewById(R.id.login)
        val registerButton : Button = findViewById(R.id.register)
        val forgotPassword : Button = findViewById(R.id.forgotPassword)

        doRequestPermission()

        val session = Session(this)
        if (session.getString("phoneUser").toString().isNotEmpty() && session.getString("pinUser").toString().isNotEmpty()) {
            User.setPhone(session.getString("phoneUser"))
            User.setPin(session.getString("pinUser"))
            User.setType(session.getInteger("typeUser"))
            val goTo = Intent(this, VerifyLoginActivity::class.java)
            startActivity(goTo)
            finish()
        }

        loginButton.setOnClickListener {
            val phoneTemporary = phoneNumber.text.toString()
            if (phoneTemporary.isEmpty()) {
                Toast.makeText(this, "Nomor Telfon Anda Tidak boleh kosong", Toast.LENGTH_LONG).show()
            } else {
                val pin = "462066"
                val typeUser = 1
                session.saveString("phoneUser", phoneTemporary)
                session.saveString("pinUser", pin)
                session.saveInteger("typeUser", typeUser)
                User.setPhone(phoneTemporary)
                User.setPin(pin)
                User.setType(typeUser)
                doRequestPermission()
                val goTo = Intent(this, VerifyLoginActivity::class.java)
                startActivity(goTo)
                finish()
            }
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.FOREGROUND_SERVICE), 100)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 100)
        }
    }
}
