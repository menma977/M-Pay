package com.mp.user.ppob.postPaidCredit

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
import com.mp.R
import com.mp.controller.ppob.PaymentController
import com.mp.model.Session
import com.mp.model.User
import kotlinx.android.synthetic.main.activity_post_paid_credit_request.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class PostPaidCreditRequestActivity : AppCompatActivity() {

    private var type = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_paid_credit_request)

        val loading = ProgressDialog(this)
        val session = Session(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()

        val arrayListName = ArrayList<String>()
        arrayListName.add("TELKOMSEL HALO")
        arrayListName.add("XL (XPLOR)")
        arrayListName.add("INDOSAT (MATRIX)")
        arrayListName.add("THREE (POSTPAID)")
        arrayListName.add("SMARTFREN (POSTPAID)")
        arrayListName.add("ESIA (POSTPAID)")
        arrayListName.add("FREN, MOBI (POSTPAID)")

        val arrayCodeName = ArrayList<String>()
        arrayCodeName.add("HPTSEL;2")
        arrayCodeName.add("HPXL;2")
        arrayCodeName.add("HPMTRIX;2")
        arrayCodeName.add("HPTHREE;2")
        arrayCodeName.add("HPSMART;2")
        arrayCodeName.add("HPESIA;2")
        arrayCodeName.add("HPFREN;2")

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayListName)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        ProductSpinner.adapter = spinnerAdapter

        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)
        BalanceTextView.text = "Saldo anda saat ini : ${numberFormat.format(User.getBalance())}"

        Handler().postDelayed({
            loading.dismiss()
        }, 500)

        ProductSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                type = arrayCodeName[position]
            }
        }

        ContinueButton.setOnClickListener {
            loading.show()
            val username = session.getString("phone").toString()
            val costumerID = CustomerID.text.toString()
            val phoneNumber = PhoneNumberEditText.text.toString().replace("-", "").replace("+62", "0").replace(" ", "")
            val balance = session.getInteger("balance").toString()
            when {
                phoneNumber.isEmpty() -> {
                    Toast.makeText(this, "Nomor telfon tidak boleh kosong", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
                costumerID.isEmpty() -> {
                    Toast.makeText(this, "Id pelanggan tidak boleh kosong", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
                type.isEmpty() -> {
                    Toast.makeText(this, "Product Tidak Boleh Kosong", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
                else -> {
                    Timer().schedule(1000) {
                        val requestPayment = PaymentController.Request(username, costumerID, phoneNumber, balance, type).execute().get()
                        println(requestPayment)
                        if (requestPayment["Status"].toString() == "0") {
                            runOnUiThread {
                                Handler().postDelayed({
                                    loading.dismiss()
                                }, 500)
                                val goTo = Intent(applicationContext, PostPaidCreditResponseActivity::class.java).putExtra("response", requestPayment.toString())
                                startActivity(goTo)
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(applicationContext, requestPayment["Pesan"].toString(), Toast.LENGTH_LONG).show()
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
