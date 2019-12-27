package com.mp.user.menu.mpay

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.mp.MainActivity
import com.mp.R
import com.mp.controller.PasswordController
import com.mp.controller.TransferController
import com.mp.controller.UserController
import com.mp.model.Session
import com.mp.model.User
import com.mp.user.member.HomeMemberActivity
import com.mp.user.merchant.HomeMerchantActivity
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class MPayActivity : AppCompatActivity() {

    private lateinit var codeValidation: EditText
    private lateinit var sendCode: Button
    private var code: String = "x"

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpay)

        val phoneTarget: EditText = findViewById(R.id.phoneNumberTarget)
        val nominal: EditText = findViewById(R.id.nominal)
        codeValidation = findViewById(R.id.codeValidation)
        sendCode = findViewById(R.id.sendCode)
        val description: EditText = findViewById(R.id.description)
        val transfer: Button = findViewById(R.id.transferButton)

        val session = Session(this)
        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)

        sendCode.setOnClickListener {
            loading.show()
            try {
                Timer().schedule(1000) {
                    val response =
                        PasswordController.SendCode(session.getString("phone").toString()).execute()
                            .get()
                    runOnUiThread {
                        if (response["status"].toString() == "0") {
                            code = response["code"].toString()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                response["massage"].toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this,
                    "Ada masalah saat pengiriman kode mohon ulangi lagi",
                    Toast.LENGTH_LONG
                ).show()
            }
            Timer().schedule(1000) {
                loading.dismiss()
            }
        }

        transfer.setOnClickListener {
            if (code != codeValidation.text.toString()) {
                Toast.makeText(this, "Code tidak valid", Toast.LENGTH_SHORT).show()
            } else if (phoneTarget.text.toString().isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "nomor telfon hanya boleh angka dan tidak boleh kosong",
                    Toast.LENGTH_LONG
                ).show()
                phoneTarget.requestFocus()
            } else if (!phoneTarget.text.isDigitsOnly()) {
                Toast.makeText(
                    applicationContext,
                    "nomor telfon hanya boleh angka dan tidak boleh kosong",
                    Toast.LENGTH_LONG
                ).show()
                phoneTarget.requestFocus()
            } else if (phoneTarget.text.toString() == session.getString("phone")) {
                Toast.makeText(
                    applicationContext,
                    "Anda Tidak bisa topup ke nomor telfon anda sendiri",
                    Toast.LENGTH_LONG
                ).show()
                phoneTarget.requestFocus()
            } else if (nominal.text.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "nominal hanya boleh angka dan tidak boleh kosong",
                    Toast.LENGTH_LONG
                ).show()
                nominal.requestFocus()
            } else if (!nominal.text.toString().isDigitsOnly()) {
                Toast.makeText(
                    applicationContext,
                    "nominal hanya boleh angka dan tidak boleh kosong",
                    Toast.LENGTH_LONG
                ).show()
                nominal.requestFocus()
            } else if (description.text.toString().isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Deskripsi tidak boleh kosong",
                    Toast.LENGTH_LONG
                ).show()
                description.requestFocus()
            } else {
                loading.show()
                Timer().schedule(1000) {
                    try {
                        val userResponse =
                            UserController.Get(session.getString("phone").toString()).execute()
                                .get()
                        session.saveInteger("balance", userResponse["deposit"].toString().toInt())
                        User.setBalance(userResponse["deposit"].toString().toInt())
                        if (userResponse["Status"].toString() == "0") {
                            val response = TransferController.PostMPay(
                                session.getString("phone").toString(),
                                phoneTarget.text.toString(),
                                nominal.text.toString(),
                                description.text.toString()
                            ).execute().get()
                            runOnUiThread {
                                if (response["Status"].toString() == "0") {
                                    val userResponses =
                                        UserController.Get(session.getString("phone").toString())
                                            .execute()
                                            .get()
                                    session.saveInteger(
                                        "balance",
                                        userResponses["deposit"].toString().toInt()
                                    )
                                    User.setBalance(userResponses["deposit"].toString().toInt())
                                    if (session.getInteger("type") == 1) {
                                        Handler().postDelayed({
                                            loading.dismiss()
                                            Toast.makeText(
                                                applicationContext,
                                                response["Pesan"].toString(),
                                                Toast.LENGTH_LONG
                                            ).show()
                                            val goTo = Intent(
                                                applicationContext,
                                                HomeMemberActivity::class.java
                                            )
                                            startActivity(goTo)
                                            finish()
                                        }, 1000)
                                    } else {
                                        Handler().postDelayed({
                                            loading.dismiss()
                                            Toast.makeText(
                                                applicationContext,
                                                response["Pesan"].toString(),
                                                Toast.LENGTH_LONG
                                            ).show()
                                            val goTo = Intent(
                                                applicationContext,
                                                HomeMerchantActivity::class.java
                                            )
                                            startActivity(goTo)
                                            finish()
                                        }, 1000)
                                    }
                                } else {
                                    loading.dismiss()
                                    Toast.makeText(
                                        applicationContext,
                                        response["Pesan"].toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        } else {
                            runOnUiThread {
                                loading.dismiss()
                                if (session.getString("imei") != userResponse["emai"].toString()) {
                                    session.clear()

                                    User.setPhone("")
                                    User.setEmail("")
                                    User.setName("")
                                    User.setPin("")
                                    User.setType(0)
                                    User.setStatus(0)
                                    val goTo = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(goTo)
                                    finish()
                                } else {
                                    loading.dismiss()
                                    Toast.makeText(
                                        applicationContext,
                                        "Internet bermasalah tolong cari lokasi lebih baik untuk mendapatkan sinyal lebih baik",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            loading.dismiss()
                            session.clear()

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
                }
            }
        }
    }
}
