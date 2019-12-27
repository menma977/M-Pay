package com.mp.user.menu

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.mp.R
import com.mp.controller.PasswordController
import com.mp.controller.TransferController
import com.mp.model.Session
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class ConvertToMDPActivity : AppCompatActivity() {

    private lateinit var usernameMDP: EditText
    private lateinit var nominal: EditText
    private lateinit var codeValidation: EditText
    private lateinit var sendCode: Button
    private lateinit var transferButton: Button
    private var code: String = "x"

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert_to_mdp)

        usernameMDP = findViewById(R.id.username)
        nominal = findViewById(R.id.nominal)
        codeValidation = findViewById(R.id.codeValidation)
        sendCode = findViewById(R.id.sendCode)
        transferButton = findViewById(R.id.transferButton)

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

        transferButton.setOnClickListener {
            if (code != codeValidation.text.toString()) {
                Toast.makeText(this, "Code tidak valid", Toast.LENGTH_SHORT).show()
            } else if (nominal.text.isEmpty()) {
                Toast.makeText(this, "nominal tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else if (!nominal.text.isDigitsOnly()) {
                Toast.makeText(this, "nominal yang anda isikan bukan angka", Toast.LENGTH_SHORT)
                    .show()
            } else if (usernameMDP.text.isEmpty()) {
                Toast.makeText(this, "username MDP tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                loading.show()
                Timer().schedule(1000) {
                    val response = TransferController.Convert(
                        session.getString("phone").toString(),
                        usernameMDP.text.toString(),
                        nominal.text.toString()
                    ).execute().get()
                    if (response["Status"].toString() == "0") {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "Sukeses melakukan transaksi",
                                Toast.LENGTH_SHORT
                            ).show()
                            loading.dismiss()
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "Gagal Melakukan Transaksi mohon ualngi lagi",
                                Toast.LENGTH_SHORT
                            ).show()
                            loading.dismiss()
                        }
                    }
                }
            }
        }
    }
}
