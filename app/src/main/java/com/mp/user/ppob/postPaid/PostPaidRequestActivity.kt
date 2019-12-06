package com.mp.user.ppob.postPaid

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
import androidx.core.widget.doOnTextChanged
import com.mp.R
import com.mp.controller.ppob.HlrController
import com.mp.controller.ppob.PostPaidController
import com.mp.controller.ppob.ProductController
import com.mp.model.PhoneNumber
import com.mp.model.Session
import com.mp.model.User
import kotlinx.android.synthetic.main.activity_post_paid_request.*
import org.json.JSONObject
import java.lang.Exception
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class PostPaidRequestActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_paid_request)
        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)
        var operator = ""
        var nominal = ""
        var type = "PULSA"
        val loading = ProgressDialog(this)
        val session = Session(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()
        BalanceTextView.text =
            "Saldo saat ini : ${numberFormat.format(if (User.getBalance() != null) User.getBalance() else 0)}"

        val arrayList = ArrayList<JSONObject>()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val hrlRequest = HlrController(User.getPhone()).execute().get()
                for (value in 0 until hrlRequest.length()) {
                    arrayList.add(JSONObject(hrlRequest[value].toString()))
                }
            }
        }, 500)

        var jsonConverterProductName = JSONObject("{id : 0}")
        var productNameArrayList = ArrayList<String>()
        var productCodeArrayList = ArrayList<String>()
        productNameArrayList.add("Mohon isikan nomor terlebih dahulu")
        var spinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, productNameArrayList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        ProductSpinner.adapter = spinnerAdapter
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val productRequest = ProductController(User.getPhone()).execute().get()
                jsonConverterProductName = productRequest.getJSONObject(0)
            }
        }, 500)

        Handler().postDelayed({
            loading.dismiss()
        }, 1000)

        PhoneNumberEditText.doOnTextChanged { text, _, _, _ ->
            val arrayResponse = arrayList.find { it["Hlr"].toString() == text.toString() }
            if (text.toString().length <= 4 && !text.isNullOrEmpty()) {
                operator = arrayResponse?.get("Operator").toString()
                try {
                    if (!arrayResponse?.get("Operator")?.toString().isNullOrEmpty() && arrayResponse?.get(
                            "Operator"
                        )?.toString() != "Default"
                    ) {
                        if (operator != "Default" && operator.isNotEmpty()) {
                            Timer().schedule(500) {
                                try {
                                    val arrayProduct =
                                        jsonConverterProductName.getJSONArray(operator)
                                    productNameArrayList.clear()
                                    productCodeArrayList.clear()
                                    for (value in 0 until arrayProduct.length() - 1) {
                                        if (PostPaidButton.isChecked) {
                                            if (arrayProduct.getJSONObject(value).get("typeProduct").toString() == "REGULER") {
                                                productNameArrayList.add(
                                                    "Rp${arrayProduct.getJSONObject(value).get("code").toString()
                                                        .replace("TELKOMSEL", "")
                                                        .replace("INDOSAT", "")
                                                        .replace("XL", "")
                                                        .replace("AXIS", "")
                                                        .replace("SMART", "")
                                                        .replace("THREE", "")
                                                        .replace("CERIA", "")
                                                    }.000"
                                                )
                                                productCodeArrayList.add(
                                                    arrayProduct.getJSONObject(
                                                        value
                                                    ).get("code").toString()
                                                )
                                            }
                                        } else {
                                            if (arrayProduct.getJSONObject(value).get("typeProduct").toString() == "DATA") {
                                                productNameArrayList.add(
                                                    arrayProduct.getJSONObject(
                                                        value
                                                    ).get("name").toString()
                                                )
                                                productCodeArrayList.add(
                                                    arrayProduct.getJSONObject(
                                                        value
                                                    ).get("code").toString()
                                                )
                                            }
                                        }
                                    }
                                    runOnUiThread {
                                        spinnerAdapter = ArrayAdapter(
                                            applicationContext,
                                            android.R.layout.simple_spinner_item,
                                            productNameArrayList
                                        )
                                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                                        ProductSpinner.adapter = spinnerAdapter
                                    }
                                } catch (e: Exception) {
                                    runOnUiThread {
                                        productNameArrayList = ArrayList()
                                        productCodeArrayList = ArrayList()
                                        productNameArrayList.add("Nomor yang anda inputkan tidak valid")
                                        spinnerAdapter = ArrayAdapter(
                                            applicationContext,
                                            android.R.layout.simple_spinner_item,
                                            productNameArrayList
                                        )
                                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                                        ProductSpinner.adapter = spinnerAdapter
                                    }
                                }
                            }
                        }
                    } else {
                        productNameArrayList = ArrayList()
                        productCodeArrayList = ArrayList()
                        productNameArrayList.add("Nomor yang anda inputkan tidak valid")
                        spinnerAdapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            productNameArrayList
                        )
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                        ProductSpinner.adapter = spinnerAdapter
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        PostPaidButton.setOnClickListener {
            try {
                if (operator != "Default" && operator.isNotEmpty()) {
                    if (PostPaidButton.isChecked) {
                        Timer().schedule(500) {
                            try {
                                val arrayProduct = jsonConverterProductName.getJSONArray(operator)
                                productNameArrayList.clear()
                                productCodeArrayList.clear()
                                for (value in 0 until arrayProduct.length() - 1) {
                                    if (arrayProduct.getJSONObject(value).get("typeProduct").toString() == "REGULER") {
                                        productNameArrayList.add(
                                            "Rp${arrayProduct.getJSONObject(value).get("code").toString()
                                                .replace("TELKOMSEL", "")
                                                .replace("INDOSAT", "")
                                                .replace("XL", "")
                                                .replace("AXIS", "")
                                                .replace("SMART", "")
                                                .replace("THREE", "")
                                                .replace("CERIA", "")
                                            }.000"
                                        )
                                        productCodeArrayList.add(
                                            arrayProduct.getJSONObject(value).get(
                                                "code"
                                            ).toString()
                                        )
                                    }
                                }
                                runOnUiThread {
                                    type = "PULSA"
                                    spinnerAdapter = ArrayAdapter(
                                        applicationContext,
                                        android.R.layout.simple_spinner_item,
                                        productNameArrayList
                                    )
                                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                                    ProductSpinner.adapter = spinnerAdapter
                                }
                            } catch (e: Exception) {
                                runOnUiThread {
                                    productNameArrayList = ArrayList()
                                    productCodeArrayList = ArrayList()
                                    productNameArrayList.add("Nomor yang anda inputkan tidak valid")
                                    spinnerAdapter = ArrayAdapter(
                                        applicationContext,
                                        android.R.layout.simple_spinner_item,
                                        productNameArrayList
                                    )
                                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                                    ProductSpinner.adapter = spinnerAdapter
                                }
                            }
                        }
                    }
                } else {
                    productNameArrayList = ArrayList()
                    productCodeArrayList = ArrayList()
                    productNameArrayList.add("Nomor yang anda inputkan tidak valid")
                    spinnerAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        productNameArrayList
                    )
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                    ProductSpinner.adapter = spinnerAdapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        DataButton.setOnClickListener {
            try {
                if (operator != "Default" && operator.isNotEmpty()) {
                    if (DataButton.isChecked) {
                        Timer().schedule(500) {
                            val arrayProduct = jsonConverterProductName.getJSONArray(operator)
                            try {
                                productNameArrayList.clear()
                                productCodeArrayList.clear()
                                for (value in 0 until arrayProduct.length() - 1) {
                                    if (arrayProduct.getJSONObject(value).get("typeProduct").toString() == "DATA") {
                                        productNameArrayList.add(
                                            arrayProduct.getJSONObject(value).get(
                                                "name"
                                            ).toString()
                                        )
                                        productCodeArrayList.add(
                                            arrayProduct.getJSONObject(value).get(
                                                "code"
                                            ).toString()
                                        )
                                    }
                                }
                                runOnUiThread {
                                    type = "DATA"
                                    nominal = arrayProduct.getJSONObject(0).get("code").toString()
                                    spinnerAdapter = ArrayAdapter(
                                        applicationContext,
                                        android.R.layout.simple_spinner_item,
                                        productNameArrayList
                                    )
                                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                                    ProductSpinner.adapter = spinnerAdapter
                                }
                            } catch (e: Exception) {
                                runOnUiThread {
                                    productNameArrayList = ArrayList()
                                    productCodeArrayList = ArrayList()
                                    productNameArrayList.add("Nomor yang anda inputkan tidak valid")
                                    spinnerAdapter = ArrayAdapter(
                                        applicationContext,
                                        android.R.layout.simple_spinner_item,
                                        productNameArrayList
                                    )
                                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                                    ProductSpinner.adapter = spinnerAdapter
                                }
                            }
                        }
                    }
                } else {
                    productNameArrayList = ArrayList()
                    productCodeArrayList = ArrayList()
                    productNameArrayList.add("Nomor yang anda inputkan tidak valid")
                    spinnerAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        productNameArrayList
                    )
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                    ProductSpinner.adapter = spinnerAdapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        ProductSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                try {
                    if (parent.count > 1) {
                        nominal = productCodeArrayList[position]
                    }
                } catch (e: Exception) {
                    nominal = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                nominal = ""
            }
        }

        ContinueButton.setOnClickListener {
            loading.show()
            try {
                if (PhoneNumberEditText.text.length >= 10 && PhoneNumberEditText.text.toString().isDigitsOnly() && nominal.isNotEmpty()) {
                    val phoneNumber = PhoneNumberEditText.text.toString()
                    val phoneArrayList = PhoneNumber.getPhone()
                    val filter: Boolean = try {
                        phoneArrayList.single { string -> string == phoneNumber }
                        false
                    } catch (e: Exception) {
                        PhoneNumber.setPhone(phoneNumber)
                        true
                    }

                    if (filter) {
                        onRequestPayment(
                            session.getString("phone").toString(),
                            PhoneNumberEditText.text.toString()
                                .replace("-", "")
                                .replace("+62", "0")
                                .replace(" ", "")
                                .replace(".", ""),
                            nominal,
                            type
                        )
                    } else {
                        Toast.makeText(
                            this,
                            "Tidak Boleh miengisi pulsa dengan nomor hp yang sama selama 30 menit.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 1000)
                } else if (PhoneNumberEditText.text.length < 10) {
                    Toast.makeText(
                        this,
                        "Nomoar Telepon yang anda inputkan kurang dari 10 digit.",
                        Toast.LENGTH_LONG
                    ).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                } else {
                    Toast.makeText(
                        this,
                        "Provider tidak di temukan. nomor tidak boleh menggunakan sepasi atau simbol.",
                        Toast.LENGTH_LONG
                    ).show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this,
                    "Terjadi Kesalahan Ketika meperoses Data yang di kirim",
                    Toast.LENGTH_LONG
                ).show()
                Handler().postDelayed({
                    loading.dismiss()
                }, 500)
            }
        }
    }

    private fun onRequestPayment(username: String, phone: String, nominal: String, type: String) {
        try {
            Timer().schedule(1000) {
                val postPaidResponse =
                    PostPaidController.Request(username, phone, nominal, type).execute().get()
                runOnUiThread {
                    if (postPaidResponse["Status"].toString() == "1") {
                        Toast.makeText(
                            applicationContext,
                            postPaidResponse["Pesan"].toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val goTo = Intent(
                            applicationContext,
                            PostPaidResponseActivity::class.java
                        ).putExtra("response", postPaidResponse.toString())
                        startActivity(goTo)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Produk atau Nomor Telepon tidak valid", Toast.LENGTH_LONG).show()
        }
    }
}
