package com.mp.user.menu

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.mp.R
import com.mp.controller.TopUpController
import com.mp.model.Session
import java.util.*
import kotlin.concurrent.schedule

class TopUpActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_up)
        val session = Session(this)
        val topUpButton: Button = findViewById(R.id.topUp)
        val nominal: EditText = findViewById(R.id.nominal)
        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)

        topUpButton.setOnClickListener {
            loading.show()
            if (nominal.text.isEmpty()) {
                Toast.makeText(this, "moninal To Up tidak boleh kosong", Toast.LENGTH_LONG).show()
                nominal.requestFocus()
                loading.dismiss()
            } else if (!nominal.text.isDigitsOnly()) {
                Toast.makeText(this, "moninal To Up hanya boleh angka", Toast.LENGTH_LONG).show()
                nominal.text.clear()
                nominal.requestFocus()
                loading.dismiss()
            } else if (nominal.text.toString().toInt() < 50000) {
                Toast.makeText(this, "minimum To Up adalah Rp 50.000", Toast.LENGTH_LONG).show()
                nominal.text.clear()
                nominal.requestFocus()
                loading.dismiss()
            } else {
                Timer().schedule(1000) {
                    val response = TopUpController.Get(
                        session.getString("phone").toString(),
                        nominal.text.toString()
                    ).execute().get()
                    println("========================")
                    println(response)
                    println("========================")
                    if (response["Status"].toString() == "0") {
                        runOnUiThread {
                            Timer().schedule(1000) {
                                runOnUiThread {
                                    Toast.makeText(
                                        applicationContext,
                                        response["Pesan"].toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    finish()
                                    loading.dismiss()
                                }
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                response["Pesan"].toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            loading.dismiss()
                        }
                    }
                }
            }
        }
    }
}
