package com.mp.user.ppob.postPaid

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.mp.R
import com.mp.controller.UserController
import com.mp.controller.ppob.PostPaidController
import com.mp.model.Session
import com.mp.model.User
import kotlinx.android.synthetic.main.activity_post_paid_response.*
import org.json.JSONObject
import java.lang.Exception
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class PostPaidResponseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_paid_response)

        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()

        val dataResponse = intent.getSerializableExtra("response").toString()
        val compriseJson = JSONObject(dataResponse)
        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)
        try {
            PhoneNumberTextView.text = compriseJson["NoHP"].toString()
            PriceTextView.text = numberFormat.format(compriseJson["Harga"].toString().toInt())
            FirstBalanceTextView.text = numberFormat.format(compriseJson["SaldoAwal"].toString().toInt())
            RemainingBalanceTextView.text = numberFormat.format(compriseJson["SisaSaldo"].toString().toInt())
        } catch (e : Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Terjadi Kesalahan saat membaca data", Toast.LENGTH_LONG).show()
            finish()
        }

        loading.dismiss()

        BuyButton.setOnClickListener {
            Handler().postDelayed({
                loading.show()
            }, 100)
            if (MarkupAdminEditText.text.toString().isNotEmpty()) {
                val username = compriseJson["Username"].toString()
                val phone = compriseJson["NoHP"].toString()
                val payCode = compriseJson["Kode"].toString()
                val nominal = compriseJson["Nominal"].toString()
                val firstBalance = compriseJson["SaldoAwal"].toString()
                val markupAdmin = MarkupAdminEditText.text.toString()
                val price = compriseJson["Harga"].toString()
                val remainingBalance = compriseJson["SisaSaldo"].toString()
                Timer().schedule(1000) {
                    val payPostPaidResponse = PostPaidController.Response(username, phone, payCode, nominal, firstBalance, markupAdmin, price, remainingBalance).execute().get()
                    if (payPostPaidResponse["Status"].toString() == "0") {
                        runOnUiThread{
                            Handler().postDelayed({
                                updateBalance()
                                loading.dismiss()
                                Toast.makeText(applicationContext, payPostPaidResponse["Pesan"].toString(), Toast.LENGTH_LONG).show()
                                finish()
                            }, 1000)
                        }
                    } else {
                        runOnUiThread {
                            Handler().postDelayed({
                                loading.dismiss()
                                Toast.makeText(applicationContext, payPostPaidResponse["Pesan"].toString(), Toast.LENGTH_LONG).show()
                            }, 1000)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Markup Admin tidak boleh kosong", Toast.LENGTH_LONG).show()
            }
        }

        BackButton.setOnClickListener {
            finish()
        }
    }

    private fun updateBalance() {
        val session = Session(this)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val response = UserController.Get(session.getString("phone").toString()).execute().get()
                if (response["Status"].toString() == "0") {
                    runOnUiThread{
                        Handler().postDelayed({
                            session.saveString("phone", response["hpagen"].toString())
                            session.saveString("email", response["email"].toString())
                            session.saveString("name", response["nama"].toString())
                            session.saveString("pin", response["password"].toString())
                            session.saveInteger("status", response["statusmember"].toString().toInt())
                            session.saveInteger("type", response["tipeuser"].toString().toInt())
                            session.saveInteger("balance", response["deposit"].toString().toInt())

                            User.setPhone(response["hpagen"].toString())
                            User.setEmail(response["email"].toString())
                            User.setName(response["nama"].toString())
                            User.setPin(response["password"].toString())
                            User.setType(response["tipeuser"].toString().toInt())
                            User.setStatus(response["statusmember"].toString().toInt())
                            User.setBalance(response["deposit"].toString().toInt())
                        }, 500)
                    }
                }
            }
        }, 1000)
    }
}
