package com.mp.user.ppob.pln

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.mp.MainActivity
import com.mp.R
import com.mp.controller.UserController
import com.mp.controller.ppob.TokenController
import com.mp.model.Session
import com.mp.model.User
import com.mp.user.member.HomeMemberActivity
import com.mp.user.merchant.HomeMerchantActivity
import kotlinx.android.synthetic.main.activity_pln_response.*
import org.json.JSONObject
import java.lang.Exception
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class PlnResponseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pln_response)

        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()

        val session = Session(this)

        try {
            val response = UserController.Get(session.getString("phone").toString()).execute().get()
            if (session.getString("imei") != response["emai"].toString()) {
                session.clear()

                User.setPhone("")
                User.setEmail("")
                User.setName("")
                User.setPin("")
                User.setType(0)
                User.setStatus(0)
                val goTo = Intent(applicationContext, MainActivity::class.java)
                startActivity(goTo)
                finish()
            } else {
                session.saveInteger("balance", response["deposit"].toString().toInt())
            }
        } catch (e: Exception) {
            session.clear()

            User.setPhone("")
            User.setEmail("")
            User.setName("")
            User.setPin("")
            User.setType(0)
            User.setStatus(0)
            val goTo = Intent(applicationContext, MainActivity::class.java)
            startActivity(goTo)
            finish()
        }

        val dataResponse = intent.getSerializableExtra("response").toString()
        val compriseJson = JSONObject(dataResponse)
        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)
        val username = compriseJson["Username"].toString()
        val costumerID = compriseJson["IdPel"].toString()
        val type = compriseJson["Kode"].toString()
        val firstBalance = compriseJson["SaldoAwal"].toString()
        val price = compriseJson["Harga"].toString()
        val remainingBalance = compriseJson["SisaSaldo"].toString()
        val phone = compriseJson["NoHP"].toString()

        PhoneNumberTextView.text = phone
        TokenNumberEditTextTextView.text = costumerID
        PriceTextView.text = numberFormat.format(price.toInt())
        FirstBalanceTextView.text = numberFormat.format(firstBalance.toInt())
        RemainingBalanceTextView.text = numberFormat.format(remainingBalance.toInt())

        Handler().postDelayed({
            loading.dismiss()
        }, 500)

        BuyButton.setOnClickListener {
            loading.show()
            val markupAdmin = MarkupAdminEditText.text.toString()
            if (markupAdmin.isNotEmpty()) {
                Timer().schedule(1000) {
                    val payPayment = TokenController.Response(
                        username,
                        phone,
                        type,
                        costumerID,
                        firstBalance,
                        markupAdmin,
                        price,
                        remainingBalance
                    ).execute().get()
                    if (payPayment["Status"].toString() == "0") {
                        runOnUiThread {
                            updateBalance()
                            Handler().postDelayed({
                                loading.dismiss()
                            }, 500)
                            Toast.makeText(
                                applicationContext,
                                payPayment["Pesan"].toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            Handler().postDelayed({
                                if (session.getInteger("type") == 1) {
                                    val goTo =
                                        Intent(applicationContext, HomeMemberActivity::class.java)
                                    startActivity(goTo)
                                    finish()
                                } else {
                                    val goTo =
                                        Intent(applicationContext, HomeMerchantActivity::class.java)
                                    startActivity(goTo)
                                    finish()
                                }
                            }, 1000)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                payPayment["Pesan"].toString(),
                                Toast.LENGTH_LONG
                            ).show()
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
                val response =
                    UserController.Get(session.getString("phone").toString()).execute().get()
                if (response["Status"].toString() == "0") {
                    runOnUiThread {
                        Handler().postDelayed({
                            session.saveString("phone", response["hpagen"].toString())
                            session.saveString("email", response["email"].toString())
                            session.saveString("name", response["nama"].toString())
                            session.saveString("pin", response["password"].toString())
                            session.saveInteger(
                                "status",
                                response["statusmember"].toString().toInt()
                            )
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
