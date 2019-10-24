package com.mp.user.ppob.postPaidCredit

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.mp.R
import com.mp.controller.UserController
import com.mp.controller.ppob.PaymentController
import com.mp.model.Session
import com.mp.model.User
import kotlinx.android.synthetic.main.activity_post_paid_credit_response.*
import org.json.JSONObject
import java.lang.Exception
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class PostPaidCreditResponseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_paid_credit_response)

        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()

        val session = Session(this)
        val dataResponse = intent.getSerializableExtra("response").toString()
        val compriseJson = JSONObject(dataResponse)
        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)
        val username = session.getString("username").toString()
        val firstBalance = session.getInteger("balance").toString()
        val type = compriseJson["Type"].toString()
        val clientID = compriseJson["IdPel"].toString()
        val clientName = compriseJson["NamaPelanggan"].toString()
        val price = compriseJson["JmlTagih"].toString()
        val admin = compriseJson["AdminBank"].toString()
        val totalPrice = compriseJson["TotalTagihan"].toString()
        val phoneNumber = compriseJson["NoHP"].toString()
        val remainingBalance = compriseJson["SisaSaldo"].toString()
        val ref = compriseJson["Ref"].toString()
        val periodic = compriseJson["PeriodeTagihan"].toString()
        try {
            PhoneNumberTextView.text = phoneNumber
            CustomerIDTextView.text = clientID
            CustomerNameTextView.text = clientName
            TypeTextView.text = type
            PeriodicTextView.text = periodic
            PriceTextView.text = numberFormat.format(price.toInt())
            FirstBalanceTextView.text = numberFormat.format(firstBalance.toInt())
            AdminTextView.text = numberFormat.format(admin.toInt())
            RemainingBalanceTextView.text = numberFormat.format(remainingBalance.toInt())
        } catch (e : Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Terjadi Kesalahan saat membaca data", Toast.LENGTH_LONG).show()
            finish()
        }

        Handler().postDelayed({
            loading.dismiss()
        }, 500)

        BuyButton.setOnClickListener {
            loading.dismiss()
            val markupAdmin = MarkupAdminEditText.text.toString()
            if (markupAdmin.isNotEmpty()) {
                Timer().schedule(1000) {
                    val payPayment = PaymentController.Response(username, type, clientID, clientName, price, admin, markupAdmin, totalPrice, phoneNumber, remainingBalance, ref, periodic).execute().get()
                    println(payPayment)
                    if (payPayment["Status"].toString() == "0") {
                        runOnUiThread {
                            updateBalance()
                            Handler().postDelayed({
                                loading.dismiss()
                            }, 500)
                            Toast.makeText(applicationContext, payPayment["Pesan"].toString(), Toast.LENGTH_LONG).show()
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(applicationContext, payPayment["Pesan"].toString(), Toast.LENGTH_LONG).show()
                            Handler().postDelayed({
                                loading.dismiss()
                            }, 500)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Markup Admin tidak boleh kosong", Toast.LENGTH_LONG).show()
                Handler().postDelayed({
                    loading.dismiss()
                }, 500)
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
