package com.mp.user.ppob.pln

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.mp.R
import com.mp.controller.ppob.ProductController
import com.mp.controller.ppob.TokenController
import com.mp.model.Session
import com.mp.model.User
import kotlinx.android.synthetic.main.activity_pln_request.*
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class PlnRequestActivity : AppCompatActivity() {

    private var type = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pln_request)
        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)
        val loading = ProgressDialog(this)
        val session = Session(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()
        BalanceTextView.text =
            "Saldo saat ini : ${numberFormat.format(if (User.getBalance() != null) User.getBalance() else 0)}"

        val arrayCodeProduct = ArrayList<String>()
        val arrayNameProduct = ArrayList<String>()

        Timer().schedule(1000) {
            val productRequest = ProductController(User.getPhone()).execute().get()
            val arrayProduct = productRequest.getJSONObject(0).getJSONArray("PLN")
            for (value in 0 until arrayProduct.length() - 1) {
                arrayNameProduct.add(
                    numberFormat.format(
                        "${arrayProduct.getJSONObject(value)["code"].toString().replace(
                            "PLN",
                            ""
                        )}000".toInt()
                    )
                )
                arrayCodeProduct.add(arrayProduct.getJSONObject(value)["code"].toString())
            }
            runOnUiThread {
                val spinnerAdapter = ArrayAdapter(
                    applicationContext,
                    android.R.layout.simple_spinner_item,
                    arrayNameProduct
                )
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                ProductSpinner.adapter = spinnerAdapter
            }
        }

        Handler().postDelayed({
            loading.dismiss()
        }, 500)

        ProductSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (parent.count > 1) {
                    type = arrayCodeProduct[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        ContinueButton.setOnClickListener {
            Handler().postDelayed({
                loading.show()
            }, 100)
            when {
                PhoneNumberEditText.text.toString().isEmpty() -> {
                    Toast.makeText(this, "No Telepon tidak boleh kosong.", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
                !PhoneNumberEditText.text.isDigitsOnly() -> {
                    Toast.makeText(this, "No Telepon tidak boleh kosong.", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
                TokenNumberEditText.text.toString().isEmpty() -> {
                    Toast.makeText(this, "Token tidak boleh kosong.", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
                !TokenNumberEditText.text.isDigitsOnly() -> {
                    Toast.makeText(this, "No Telepon tidak boleh kosong.", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
                else -> {
                    Timer().schedule(1000) {
                        val username = session.getString("phone").toString()
                        val phone =
                            PhoneNumberEditText.text.toString().replace("-", "").replace("+62", "0")
                                .replace(" ", "")
                        val token = TokenNumberEditText.text.toString()
                        println(type)
                        val postPaidController =
                            TokenController.Request(username, phone, token, type).execute().get()
                        println(postPaidController)
                        if (postPaidController["Status"].toString() == "0") {
                            runOnUiThread {
                                loading.dismiss()
                                val goTo = Intent(
                                    applicationContext,
                                    PlnResponseActivity::class.java
                                ).putExtra("response", postPaidController.toString())
                                startActivity(goTo)
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    postPaidController["Pesan"].toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                Handler().postDelayed({
                                    loading.dismiss()
                                }, 500)
                            }
                        }
                    }
                }
            }
        }
    }
}
