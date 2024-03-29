package com.mp.user.menu.bank

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.activity_bank.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class BankActivity : AppCompatActivity() {


    private lateinit var codeValidation: EditText
    private lateinit var sendCode: Button
    private var code: String = "x"

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)

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

        val sdf = SimpleDateFormat("H")
        val currentDate = sdf.format(Date())
        if (currentDate.toInt() < 8 || currentDate.toInt() > 15) {
            Toast.makeText(
                this,
                "Transaksi hanya bisa di lakukan dari jam 08:00 sampai 15:00",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }

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
            } else if (bankTarget.text.isEmpty()) {
                Toast.makeText(this, "Nama BANK tidak boleh kosong", Toast.LENGTH_LONG).show()
                bankTarget.requestFocus()
            } else if (accountTarget.text.isEmpty() || !accountTarget.text.isDigitsOnly()) {
                Toast.makeText(
                    this,
                    "Nomor rekening hanya boleh angka dan tidak boleh kosong",
                    Toast.LENGTH_LONG
                ).show()
                accountTarget.requestFocus()
            } else if (nominal.text.isEmpty() || !nominal.text.toString().isDigitsOnly() || nominal.text.toString().toInt() > 15000000) {
                Toast.makeText(
                    this,
                    "nominal hanya boleh angka, tidak boleh kosong dan maksimum withdraw adalah 15juta rupiah",
                    Toast.LENGTH_LONG
                ).show()
                nominal.requestFocus()
            } else if (description.text.toString().isEmpty()) {
                Toast.makeText(this, "Deskripsi tidak boleh kosong", Toast.LENGTH_LONG).show()
                description.requestFocus()
            } else if (name.text.isEmpty()) {
                Toast.makeText(this, "nama sesuai akun BANK tidak boleh kosong", Toast.LENGTH_LONG)
                    .show()
                name.requestFocus()
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
                            val response = TransferController.PostBank(
                                session.getString("phone").toString(),
                                bankTarget.text.toString(),
                                accountTarget.text.toString(),
                                name.text.toString(),
                                nominal.text.toString()
                            ).execute().get()
                            println(response)
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
