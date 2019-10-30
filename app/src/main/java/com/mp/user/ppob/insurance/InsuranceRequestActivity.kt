package com.mp.user.ppob.insurance

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import com.mp.R
import com.mp.controller.ppob.PaymentController
import com.mp.model.Session
import com.mp.model.User
import com.mp.user.ppob.postPaidCredit.PostPaidCreditResponseActivity
import org.json.JSONArray
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class InsuranceRequestActivity : AppCompatActivity() {

    private var type = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insurance_request)
        val productSpinner: Spinner = findViewById(R.id.ProductSpinner)
        val balanceTextView: TextView = findViewById(R.id.BalanceTextView)
        val continueButton: Button = findViewById(R.id.ContinueButton)
        val customerID: EditText = findViewById(R.id.CustomerID)
        val phoneNumberEditText: EditText = findViewById(R.id.PhoneNumberEditText)
        val loading = ProgressDialog(this)
        val session = Session(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()

        val jsonArrayConverter = JSONArray(
            "[" +
                    "{ code: 'ASCAR;1', name: 'CAR' },\n" +
                    "{ code: 'ASPRU;2', name: 'PRUDENTIAL PREMI LANJUTAN' },\n" +
                    "{ code: 'ASBINT1;2', name: 'ASURANSI BINTANG PAKET' },\n" +
                    "{ code: 'ASBINT2;2', name: 'ASURANSI BINTANG PAKET 2' },\n" +
                    "{ code: 'ASJWS;2', name: 'ASURANSI JIWASRAYA -' },\n" +
                    "{ code: 'ASTOKIOS;2', name: 'TOKIO MARINE (SatuTagihan)' },\n" +
                    "{ code: 'ASTOKIO;2', name: 'TOKIO MARINE (SemuaTagihan)' }," +
                    "]"
        )

        val arrayListName = ArrayList<String>()
        val arrayCodeName = ArrayList<String>()

        for (value in 0 until jsonArrayConverter.length() - 1) {
            arrayCodeName.add(jsonArrayConverter.getJSONObject(value)["code"].toString())
            arrayListName.add(jsonArrayConverter.getJSONObject(value)["name"].toString())
        }

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayListName)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        productSpinner.adapter = spinnerAdapter

        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)
        balanceTextView.text = "Saldo anda saat ini : ${numberFormat.format(User.getBalance())}"

        Handler().postDelayed({
            loading.dismiss()
        }, 500)

        productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        continueButton.setOnClickListener {
            loading.show()
            val username = session.getString("phone").toString()
            val costumerID = customerID.text.toString()
            val phoneNumber =
                phoneNumberEditText.text.toString().replace("-", "").replace("+62", "0")
                    .replace(" ", "")
            val balance = session.getInteger("balance").toString()
            when {
                phoneNumber.isEmpty() -> {
                    Toast.makeText(this, "Nomor telfon tidak boleh kosong", Toast.LENGTH_LONG)
                        .show()
                    Handler().postDelayed({
                        loading.dismiss()
                    }, 500)
                }
                costumerID.isEmpty() -> {
                    Toast.makeText(this, "Id pelanggan tidak boleh kosong", Toast.LENGTH_LONG)
                        .show()
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
                        val requestPayment = PaymentController.Request(
                            username,
                            costumerID,
                            phoneNumber,
                            balance,
                            type
                        ).execute().get()
                        println(requestPayment)
                        if (requestPayment["Status"].toString() == "0") {
                            runOnUiThread {
                                Handler().postDelayed({
                                    loading.dismiss()
                                }, 500)
                                val goTo = Intent(
                                    applicationContext,
                                    PostPaidCreditResponseActivity::class.java
                                ).putExtra("response", requestPayment.toString())
                                startActivity(goTo)
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    requestPayment["Pesan"].toString(),
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
