package com.mp.user.ppob.eMoneyMandiri

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.core.text.isDigitsOnly
import com.mp.R
import com.mp.controller.ppob.ProductController
import com.mp.controller.ppob.TokenController
import com.mp.model.Session
import com.mp.model.User
import com.mp.user.ppob.pln.PlnResponseActivity
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class EMoneyMandiriRequestActivity : AppCompatActivity() {

    private var type = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emoney_mandiri_request)
        val balanceTextView : TextView = findViewById(R.id.BalanceTextView)
        val productSpinner : Spinner = findViewById(R.id.ProductSpinner)
        val continueButton : Button = findViewById(R.id.ContinueButton)
        val phoneNumberEditText : EditText = findViewById(R.id.PhoneNumberEditText)
        val tokenNumberEditText : EditText = findViewById(R.id.TokenNumberEditText)
        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)
        val loading = ProgressDialog(this)
        val session = Session(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()
        balanceTextView.text = "Saldo saat ini : ${numberFormat.format(if(User.getBalance() != null) User.getBalance() else 0)}"

        val arrayCodeProduct = ArrayList<String>()
        val arrayNameProduct = ArrayList<String>()

        Timer().schedule(1000) {
            val productRequest = ProductController(User.getPhone()).execute().get()
            val arrayProduct = productRequest.getJSONObject(0).getJSONArray("ETOLL")
            for (value in 0 until arrayProduct.length() - 1) {
                if (arrayProduct.getJSONObject(value)["typeProduct"].toString() == "MANDIRI") {
                    arrayNameProduct.add(arrayProduct.getJSONObject(value)["name"].toString())
                    arrayCodeProduct.add(arrayProduct.getJSONObject(value)["code"].toString())
                }
            }
            runOnUiThread {
                val spinnerAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, arrayNameProduct)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                productSpinner.adapter = spinnerAdapter
            }
        }

        Handler().postDelayed({
            loading.dismiss()
        }, 500)

        productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (parent.count > 1) {
                    type = arrayCodeProduct[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        continueButton.setOnClickListener {
            Handler().postDelayed({
                loading.show()
            }, 100)
            when {
                phoneNumberEditText.text.toString().isEmpty() -> {
                    Toast.makeText(this, "No Telepon tidak boleh kosong.", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
                phoneNumberEditText.text.toString().isDigitsOnly() -> {
                    Toast.makeText(this, "No Telepon hanya angka.", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
                tokenNumberEditText.text.toString().isEmpty() -> {
                    Toast.makeText(this, "Token tidak boleh kosong.", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
                else -> {
                    Timer().schedule(1000) {
                        val username = session.getString("phone").toString()
                        val phone = phoneNumberEditText.text.toString().replace("-", "").replace("+62", "0").replace(" ", "")
                        val token = tokenNumberEditText.text.toString()
                        println(type)
                        val postPaidController = TokenController.Request(username, phone, token, type).execute().get()
                        println(postPaidController)
                        if (postPaidController["Status"].toString() == "0") {
                            runOnUiThread {
                                loading.dismiss()
                                val goTo = Intent(applicationContext, PlnResponseActivity::class.java).putExtra("response", postPaidController.toString())
                                startActivity(goTo)
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(applicationContext, postPaidController["Pesan"].toString(), Toast.LENGTH_LONG).show()
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
