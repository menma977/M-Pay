package com.mp.user.ppob.grap

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.core.widget.doOnTextChanged
import com.mp.R
import com.mp.controller.ppob.HlrController
import com.mp.controller.ppob.PostPaidController
import com.mp.controller.ppob.ProductController
import com.mp.model.Session
import com.mp.model.User
import com.mp.user.ppob.postPaid.PostPaidResponseActivity
import org.json.JSONObject
import java.lang.Exception
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class GrabRequestActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grab_request)
        val balanceTextView : TextView = findViewById(R.id.BalanceTextView)
        val productSpinner : Spinner = findViewById(R.id.ProductSpinner)
        val phoneNumberEditText : EditText = findViewById(R.id.PhoneNumberEditText)
        val logoImageView : ImageView = findViewById(R.id.LogoImageView)
        val continueButton : Button = findViewById(R.id.ContinueButton)
        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)
        var operator: String
        var nominal = ""
        val type = "DANA"
        val loading = ProgressDialog(this)
        val session = Session(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()
        balanceTextView.text = "Saldo saat ini : ${numberFormat.format(if(User.getBalance() != null) User.getBalance() else 0)}"

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
        var spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productNameArrayList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        productSpinner.adapter = spinnerAdapter
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val productRequest = ProductController(User.getPhone()).execute().get()
                jsonConverterProductName = productRequest.getJSONObject(0)
            }
        }, 500)

        Handler().postDelayed({
            loading.dismiss()
        }, 1000)

        phoneNumberEditText.doOnTextChanged { text, _, _, _ ->
            val arrayResponse = arrayList.find { it["Hlr"].toString() == text.toString()}
            if (text.toString().length <= 4) {
                operator = arrayResponse?.get("Operator").toString()
                try {
                    when (operator) {
                        "TELKOMSEL" -> logoImageView.setImageResource(R.drawable.telkomsel)
                        "INDOSAT" -> logoImageView.setImageResource(R.drawable.indosat)
                        "XL" -> logoImageView.setImageResource(R.drawable.xl)
                        "AXIS" -> logoImageView.setImageResource(R.drawable.axis)
                        "SMART" -> logoImageView.setImageResource(R.drawable.smart)
                        "THREE" -> logoImageView.setImageResource(R.drawable.three)
                        else -> logoImageView.setImageResource(R.mipmap.ic_launcher_foreground)
                    }

                    if (!arrayResponse?.get("Operator")?.toString().isNullOrEmpty() && arrayResponse?.get("Operator")?.toString() != "Default") {
                        if (operator != "Default" && operator.isNotEmpty()) {
                            Timer().schedule(500) {
                                val arrayProduct = jsonConverterProductName.getJSONArray("GRAB")
                                productNameArrayList.clear()
                                productCodeArrayList.clear()
                                for (value in 0 until arrayProduct.length() - 1) {
                                    productNameArrayList.add("Rp${arrayProduct.getJSONObject(value).get("code").toString()
                                        .replace("GRAB", "")
                                    }.000")
                                    productCodeArrayList.add(arrayProduct.getJSONObject(value).get("code").toString())
                                }
                                runOnUiThread{
                                    spinnerAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, productNameArrayList)
                                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                                    productSpinner.adapter = spinnerAdapter
                                }
                            }
                        }
                    } else {
                        productNameArrayList = ArrayList()
                        productCodeArrayList = ArrayList()
                        productNameArrayList.add("Nomor yang anda inputkan tidak valid")
                        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productNameArrayList)
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                        productSpinner.adapter = spinnerAdapter
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }

        productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (parent.count > 1) {
                    nominal = productCodeArrayList[position]
                    println(productCodeArrayList)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                nominal = ""
            }
        }

        continueButton.setOnClickListener {
            loading.show()
            if (phoneNumberEditText.text.length >= 10 && nominal.isNotEmpty()) {
                onRequestPayment(
                    session.getString("phone").toString(),
                    phoneNumberEditText.text.toString().replace("-", "").replace("+62", "0").replace(" ", ""),
                    nominal,
                    type
                )
                Handler().postDelayed({
                    loading.dismiss()
                }, 1000)
            } else if(phoneNumberEditText.text.length < 10) {
                Toast.makeText(this, "Nomoar Telfon yang anda inputkan kurang dari 10 digit.", Toast.LENGTH_LONG).show()
                Handler().postDelayed({
                    loading.dismiss()
                }, 500)
            } else {
                Toast.makeText(this, "Provider tidak di temukan.", Toast.LENGTH_LONG).show()
                Handler().postDelayed({
                    loading.dismiss()
                }, 500)
            }
        }
    }

    private fun onRequestPayment(username : String, phone : String, nominal : String, type : String) {
        try {
            Timer().schedule(1000) {
                val url = "${User.getUrl()}/isiovo.php"
                val postPaidResponse = PostPaidController.RequestFlexible(url, username, phone, nominal, type).execute().get()
                println("$username, $phone, $nominal, $type")
                println(postPaidResponse)
                runOnUiThread{
                    if (postPaidResponse["Status"].toString() == "1") {
                        Toast.makeText(applicationContext, postPaidResponse["Pesan"].toString(), Toast.LENGTH_LONG).show()
                    } else {
                        val goTo = Intent(applicationContext, PostPaidResponseActivity::class.java).putExtra("response", postPaidResponse.toString())
                        startActivity(goTo)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Produk atau Nomor Telfon tidak valid", Toast.LENGTH_LONG).show()
        }
    }
}
