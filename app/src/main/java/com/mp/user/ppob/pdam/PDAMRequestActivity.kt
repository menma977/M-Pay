package com.mp.user.ppob.pdam

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
import com.mp.controller.ppob.PaymentController
import com.mp.model.Session
import com.mp.model.User
import com.mp.user.ppob.postPaidCredit.PostPaidCreditResponseActivity
import org.json.JSONArray
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class PDAMRequestActivity : AppCompatActivity() {

    private var type = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdamrequest)
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
            "[{ code: 'PAMAET;2', name: 'AETRA JAKARTA' },\n" +
                    "{ code: 'PAMPLJ;4', name: 'PALYJA JAKARTA' },\n" +
                    "{ code: 'PAMBNA;4', name: 'PDAM ACEH BESAR' },\n" +
                    "{ code: 'PAMBTM;4', name: 'PDAM BATAM' },\n" +
                    "{ code: 'PAMCMS;4', name: 'PDAM CIAMIS' },\n" +
                    "{ code: 'PAMCBN;4', name: 'PDAM CIREBON' },\n" +
                    "{ code: 'PAMGNK;4', name: 'PDAM GUNUNG KIDUL' },\n" +
                    "{ code: 'PAMJAP;4', name: 'PDAM JAYAPURA' },\n" +
                    "{ code: 'PAMKPRN;4', name: 'PDAM KAB. BALANGAN' },\n" +
                    "{ code: 'PAMKBDG;4', name: 'PDAM KAB. BANDUNG' },\n" +
                    "{ code: 'PAMKBKL;4', name: 'PDAM KAB. BANGKALAN' },\n" +
                    "{ code: 'PAMKPWT;4', name: 'PDAM KAB. BANYUMAS' },\n" +
                    "{ code: 'PAMKBTG;4', name: 'PDAM KAB. BATANG' },\n" +
                    "{ code: 'PAMBLA;4', name: 'PDAM KAB. BLORA' },\n" +
                    "{ code: 'PAMKBGR;4', name: 'PDAM KAB. BOGOR' },\n" +
                    "{ code: 'PAMKBGN;4', name: 'PDAM KAB. BOJONEGORO' },\n" +
                    "{ code: 'PAMKBDW;4', name: 'PDAM KAB. BONDOWOSO' },\n" +
                    "{ code: 'PAMKBYL;4', name: 'PDAM KAB. BOYOLALI' },\n" +
                    "{ code: 'PAMKBBS;4', name: 'PDAM KAB. BREBES' },\n" +
                    "{ code: 'PAMKSGR;4', name: 'PDAM KAB. BULELENG' },\n" +
                    "{ code: 'PAMKCLP;4', name: 'PDAM KAB. CILACAP' },\n" +
                    "{ code: 'PAMKPWD;4', name: 'PDAM KAB. GROBOGAN' },\n" +
                    "{ code: 'PAMKJMR;4', name: 'PDAM KAB. JEMBER' },\n" +
                    "{ code: 'PAMKJPA;4', name: 'PDAM KAB. JEPARA' },\n" +
                    "{ code: 'PAMKKRG;4', name: 'PDAM KAB. KARANGANYAR' },\n" +
                    "{ code: 'PAMKKBM;4', name: 'PDAM KAB. KEBUMEN' },\n" +
                    "{ code: 'PAMKKDL;4', name: 'PDAM KAB. KENDAL' },\n" +
                    "{ code: 'PAMKSRP;4', name: 'PDAM KAB. KLUNGKUNG' },\n" +
                    "{ code: 'PAMKSRY;4', name: 'PDAM KAB. KUBU RAYA' },\n" +
                    "{ code: 'PAMKKN;4', name: 'PDAM KAB. KUTAI KERTANEGARA' },\n" +
                    "{ code: 'PAMKPYA;4', name: 'PDAM KAB. LOMBOK TENGAH' },\n" +
                    "{ code: 'PAMMGG;4', name: 'PDAM KAB. MAGELANG' },\n" +
                    "{ code: 'PAMKMLG;4', name: 'PDAM KAB. MALANG' },\n" +
                    "{ code: 'PAMKMJK;4', name: 'PDAM KAB. MOJOKERTO' },\n" +
                    "{ code: 'PAMMBA;4', name: 'PDAM KAB. MUSI BANYUASIN' },\n" +
                    "{ code: 'PAMKPKL;4', name: 'PDAM KAB. PEKALONGAN' },\n" +
                    "{ code: 'PAMKPBL;4', name: 'PDAM KAB. PROBOLINGGO' },\n" +
                    "{ code: 'PAMKPBG;4', name: 'PDAM KAB. PURBALINGGA' },\n" +
                    "{ code: 'PAMKPWR;4', name: 'PDAM KAB. PURWOREJO' },\n" +
                    "{ code: 'PAMKRBG;4', name: 'PDAM KAB. REMBANG' },\n" +
                    "{ code: 'PAMKSPG;4', name: 'PDAM KAB. SAMPANG' },\n" +
                    "{ code: 'PAMKSMG;4', name: 'PDAM KAB. SEMARANG' },\n" +
                    "{ code: 'PAMKSDA;4', name: 'PDAM KAB. SIDOARJO' },\n" +
                    "{ code: 'PAMKSIT;4', name: 'PDAM KAB. SITUBONDO' },\n" +
                    "{ code: 'PAMKSMN;4', name: 'PDAM KAB. SLEMAN' },\n" +
                    "{ code: 'PAMKSKH;4', name: 'PDAM KAB. SUKOHARJO' },\n" +
                    "{ code: 'PAMKTNG;4', name: 'PDAM KAB. TANGERANG' },\n" +
                    "{ code: 'PAMKRTA;4', name: 'PDAM KAB. TAPIN' },\n" +
                    "{ code: 'PAMKTMG;4', name: 'PDAM KAB. TEMANGGUNG' },\n" +
                    "{ code: 'PAMKWNG;4', name: 'PDAM KAB. WONOGIRI' },\n" +
                    "{ code: 'PAMKWSB;4', name: 'PDAM KAB. WONOSOBO' },\n" +
                    "{ code: 'PAMBDL;4', name: 'PDAM KOTA BANDAR LAMPUNG' },\n" +
                    "{ code: 'PAMBDG;4', name: 'PDAM KOTA BANDUNG' },\n" +
                    "{ code: 'PAMBJB;4', name: 'PDAM KOTA BANJARBARU' },\n" +
                    "{ code: 'PAMDPR;4', name: 'PDAM KOTA DENPASAR' },\n" +
                    "{ code: 'PAMDPK;4', name: 'PDAM KOTA DEPOK' },\n" +
                    "{ code: 'PAMMAD;4', name: 'PDAM KOTA MADIUN' },\n" +
                    "{ code: 'PAMMLG;4', name: 'PDAM KOTA MALANG' },\n" +
                    "{ code: 'PAMMND;4', name: 'PDAM KOTA MANADO' },\n" +
                    "{ code: 'PAMMTR;4', name: 'PDAM KOTA MATARAM' },\n" +
                    "{ code: 'PAMMDN;4', name: 'PDAM KOTA MEDAN' },\n" +
                    "{ code: 'PAMPLG;4', name: 'PDAM KOTA PALEMBANG' },\n" +
                    "{ code: 'PAMPSR;4', name: 'PDAM KOTA PASURUAN' },\n" +
                    "{ code: 'PAMSLT;4', name: 'PDAM KOTA SALATIGA' },\n" +
                    "{ code: 'PAMSMG;4', name: 'PDAM KOTA SEMARANG' },\n" +
                    "{ code: 'PAMSBY;4', name: 'PDAM KOTA SURABAYA' },\n" +
                    "{ code: 'PAMSKT;4', name: 'PDAM KOTA SURAKARTA / KOTA SOLO' },\n" +
                    "{ code: 'PAMTGT;4', name: 'PDAM KOTA TANAH GROGOT' },\n" +
                    "{ code: 'PAMYYK;4', name: 'PDAM KOTA YOGYAKARTA' },\n" +
                    "{ code: 'PAMKTI;4', name: 'PDAM KUTAI' },\n" +
                    "{ code: 'PAMMGT;4', name: 'PDAM MAGETAN' },\n" +
                    "{ code: 'PAMMSI;4', name: 'PDAM MUSI' },\n" +
                    "{ code: 'PAMNGW;4', name: 'PDAM NGAWI' },\n" +
                    "{ code: 'PAMPLP;4', name: 'PDAM PALOPO' },\n" +
                    "{ code: 'PAMPTI;4', name: 'PDAM PATI' },\n" +
                    "{ code: 'PAMTTE;4', name: 'PDAM TERNATE' }]"
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
                phoneNumber.isDigitsOnly() -> {
                    Toast.makeText(this, "Nomor telfon Hanya boleh nakga", Toast.LENGTH_LONG)
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
