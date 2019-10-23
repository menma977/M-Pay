package com.mp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mp.controller.RegisterController
import com.mp.controller.UserController
import com.mp.model.Session
import com.mp.model.User
import com.mp.password.edit.EditPasswordActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doRequestPermission()

        val loginButton : Button = findViewById(R.id.login)
        val registerButton : Button = findViewById(R.id.register)
        val forgotPassword : Button = findViewById(R.id.forgotPassword)

        val session = Session(this)
        if (session.getString("phoneUser").toString().isNotEmpty()
            && session.getString("phoneUser") != null
            && session.getString("pinUser").toString().isNotEmpty()
            && session.getString("pinUser") != null) {
            println("==================Login=======================")
            println(session.getString("phoneUser").toString())
            println(session.getString("pinUser"))
            println("==============================================")
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
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        val response = UserController.Get(phoneTemporary).execute().get()
                        if (response["Status"].toString() == "0") {
                            runOnUiThread{
                                Handler().postDelayed({
                                    Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
                                }, 500)
                            }
                        } else {
                            runOnUiThread{
                                Handler().postDelayed({
                                    Toast.makeText(applicationContext, response["Pesan"].toString(), Toast.LENGTH_LONG).show()
                                }, 500)
                            }
                        }
                    }
                }, 1000)
//                val pin = "462066"
//                val typeUser = 1
//                session.saveString("phoneUser", phoneTemporary)
//                session.saveString("pinUser", pin)
//                session.saveInteger("typeUser", typeUser)
//                User.setPhone(phoneTemporary)
//                User.setPin(pin)
//                User.setType(typeUser)
//                doRequestPermission()
//                val goTo = Intent(this, VerifyLoginActivity::class.java)
//                startActivity(goTo)
//                finish()
            }
        }

        registerButton.setOnClickListener {
            val goTo = Intent(this, RegisterUserActivity::class.java)
            startActivity(goTo)
        }

        forgotPassword.setOnClickListener {
            if (phoneNumber.text.isNotEmpty()) {
                try {
                    val response = RegisterController.VerifiedPhone(phoneNumber.text.toString()).execute().get()
                    println()
                    println("===============Register===================")
                    println(response)
                    println("==========================================")
                    if (response["Status"].toString() == "0") {
                        Toast.makeText(this, "${response["Pesan"]}", Toast.LENGTH_LONG).show()
                        val goTo = Intent(this, EditPasswordActivity::class.java)
                            .putExtra("pin", response["code_key"].toString())
                        startActivity(goTo)
                    } else {
                        Toast.makeText(this, "${response["Pesan"]}", Toast.LENGTH_LONG).show()
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Ada masalah saat pengiriman kode mohon ulangi lagi", Toast.LENGTH_LONG).show()
                }
            } else {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread{
                            Toast.makeText(applicationContext, "Nomor Telfon Tidak boleh kosong", Toast.LENGTH_LONG).show()
                        }
                    }
                }, 1000)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun doRequestPermission() {
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.FOREGROUND_SERVICE
            ), 100)
        }
    }
}
