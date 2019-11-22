@file:Suppress("DEPRECATION")

package com.mp

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.mp.controller.PasswordController
import com.mp.controller.UserController
import com.mp.model.Session
import com.mp.model.User
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doRequestPermission()

        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()

        val loginButton: Button = findViewById(R.id.login)
        val registerButton: Button = findViewById(R.id.register)
        //val forgotPassword : Button = findViewById(R.id.forgotPassword)

        val session = Session(this)
        session.saveString("url", "https://multipayment.co/api")
        if (session.getString("phone").toString().isNotEmpty()
            && session.getString("phone") != null
            && session.getString("pin").toString().isNotEmpty()
            && session.getString("pin") != null
        ) {
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
                Toast.makeText(this, "Nomor Telepon Anda Tidak boleh kosong", Toast.LENGTH_LONG)
                    .show()
                loading.dismiss()
            } else if (!phoneTemporary.isDigitsOnly()) {
                Toast.makeText(this, "Nomor Telepon Hanya Angka", Toast.LENGTH_LONG)
                    .show()
                loading.dismiss()
            } else {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        val response = UserController.Get(phoneTemporary).execute().get()
                        if (response["Status"].toString() == "0") {
                            runOnUiThread {
                                session.saveString("phone", response["hpagen"].toString())
                                session.saveString("email", response["email"].toString())
                                session.saveString("name", response["nama"].toString())
                                session.saveString("pin", response["password"].toString())
                                session.saveInteger(
                                    "status",
                                    response["statusmember"].toString().toInt()
                                )
                                session.saveInteger("type", response["tipeuser"].toString().toInt())
                                session.saveInteger(
                                    "balance",
                                    response["deposit"].toString().toInt()
                                )

                                User.setPhone(response["hpagen"].toString())
                                User.setEmail(response["email"].toString())
                                User.setName(response["nama"].toString())
                                User.setPin(response["password"].toString())
                                User.setType(response["tipeuser"].toString().toInt())
                                User.setStatus(response["statusmember"].toString().toInt())
                                User.setBalance(response["deposit"].toString().toInt())
                                Handler().postDelayed({
                                    loading.dismiss()
                                    val goTo =
                                        Intent(applicationContext, VerifyLoginActivity::class.java)
                                    startActivity(goTo)
                                    finish()
                                }, 1000)
                            }
                        } else {
                            runOnUiThread {
                                Handler().postDelayed({
                                    loading.dismiss()
                                    Toast.makeText(
                                        applicationContext,
                                        "Nomor Telepon tidak di temukan",
                                        Toast.LENGTH_LONG
                                    ).show()
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

        forgotPassword.setOnClickListener {
            loading.show()
            if (phoneNumber.text.isNotEmpty()) {
                Timer().schedule(1000) {
                    try {
                        val response =
                            UserController.Get(phoneNumber.text.toString()).execute().get()
                        if (response["Status"].toString() == "0") {
                            PasswordController.SendPassword(
                                response["hpagen"].toString(),
                                response["password"].toString()
                            ).execute().get()
                            runOnUiThread {
                                loading.dismiss()
                            }
                        } else {
                            runOnUiThread {
                                loading.dismiss()
                                Toast.makeText(
                                    applicationContext,
                                    "${response["Pesan"]}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            loading.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "Ada masalah saat pengiriman password mohon ulangi lagi",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } else {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            loading.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "Nomor Telepon Tidak boleh kosong",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }, 1000)
            }
        }
    }

    private fun doRequestPermission() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                    ), 100
                )
            }
        }
    }
}
