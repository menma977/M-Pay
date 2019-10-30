@file:Suppress("DEPRECATION")

package com.mp

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mp.controller.UserController
import com.mp.model.Session
import com.mp.model.User
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()

        doRequestPermission()

        val loginButton : Button = findViewById(R.id.login)
        val registerButton : Button = findViewById(R.id.register)
        //val forgotPassword : Button = findViewById(R.id.forgotPassword)

        val session = Session(this)
        session.saveString("url", "https://multipayment.co/api")
        User.setUrl(session.getString("url"))
        if (session.getString("phone").toString().isNotEmpty()
            && session.getString("phone") != null
            && session.getString("pin").toString().isNotEmpty()
            && session.getString("pin") != null) {
            println("==================Login=======================")
            println(session.getString("phone").toString())
            println(session.getString("pin"))
            println("==============================================")
            User.setPhone(session.getString("phone"))
            User.setPin(session.getString("pin"))
            User.setType(session.getInteger("type"))
            val goTo = Intent(this, VerifyLoginActivity::class.java)
            startActivity(goTo)
            finish()
        }

        loading.dismiss()

        loginButton.setOnClickListener {
            loading.show()
            val phoneTemporary = phoneNumber.text.toString()
            if (phoneTemporary.isEmpty()) {
                Toast.makeText(this, "Nomor Telepon Anda Tidak boleh kosong", Toast.LENGTH_LONG).show()
                loading.dismiss()
            } else {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        val response = UserController.Get(phoneTemporary).execute().get()
                        if (response["Status"].toString() == "0") {
                            runOnUiThread{
                                session.saveString("phone", response["hpagen"].toString())
                                session.saveString("email", response["email"].toString())
                                session.saveString("name", response["nama"].toString())
                                session.saveString("pin", response["password"].toString())
                                session.saveInteger("status", response["statusmember"].toString().toInt())
                                session.saveInteger("type", response["tipeuser"].toString().toInt())
                                session.saveInteger("balance", response["deposit"].toString().toInt())

                                User.setPhone(response["hpagen"].toString())
                                User.setEmail(response["email"].toString())
                                User.setName(response["nama"].toString())
                                User.setPin(response["password"].toString())
                                User.setType(response["tipeuser"].toString().toInt())
                                User.setStatus(response["statusmember"].toString().toInt())
                                User.setBalance(response["deposit"].toString().toInt())
                                doRequestPermission()
                                Handler().postDelayed({
                                    loading.dismiss()
                                    val goTo = Intent(applicationContext, VerifyLoginActivity::class.java)
                                    startActivity(goTo)
                                    finish()
                                }, 1000)
                            }
                        } else {
                            runOnUiThread{
                                Handler().postDelayed({
                                    loading.dismiss()
                                    Toast.makeText(applicationContext, "Nomor Telepon tidak di temukan", Toast.LENGTH_LONG).show()
                                }, 500)
                            }
                        }
                    }
                }, 1000)
            }
        }

        registerButton.setOnClickListener {
            val goTo = Intent(this, RegisterUserActivity::class.java)
            startActivity(goTo)
        }

//        forgotPassword.setOnClickListener {
//            if (phoneNumber.text.isNotEmpty()) {
//                try {
//                    val response = UserController.Get(phoneNumber.text.toString()).execute().get()
//                    println()
//                    println("===============Register===================")
//                    println(response)
//                    println("==========================================")
//                    if (response["Status"].toString() == "0") {
//                        val goTo = Intent(this, EditPasswordActivity::class.java)
//                            .putExtra("pin", response["password"].toString())
//                        startActivity(goTo)
//                    } else {
//                        Toast.makeText(this, "${response["Pesan"]}", Toast.LENGTH_LONG).show()
//                    }
//                } catch (e : Exception) {
//                    e.printStackTrace()
//                    Toast.makeText(this, "Ada masalah saat pengiriman kode mohon ulangi lagi", Toast.LENGTH_LONG).show()
//                }
//            } else {
//                Timer().schedule(object : TimerTask() {
//                    override fun run() {
//                        runOnUiThread{
//                            Toast.makeText(applicationContext, "Nomor Telepon Tidak boleh kosong", Toast.LENGTH_LONG).show()
//                        }
//                    }
//                }, 1000)
//            }
//        }
    }

    @SuppressLint("NewApi")
    private fun doRequestPermission() {
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.READ_PHONE_STATE
            ), 100)
        }
    }
}
