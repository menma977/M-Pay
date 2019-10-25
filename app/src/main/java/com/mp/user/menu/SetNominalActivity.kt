package com.mp.user.menu

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.mp.MainActivity
import com.mp.R
import com.mp.controller.TransferController
import com.mp.controller.UserController
import com.mp.model.Session
import com.mp.model.User
import com.mp.user.member.HomeMemberActivity
import com.mp.user.merchant.HomeMerchantActivity
import kotlinx.android.synthetic.main.activity_set_nominal.*
import org.json.JSONObject
import java.lang.Exception
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class SetNominalActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_nominal)
        val dataResponse = intent.getSerializableExtra("response").toString()
        val compriseJson = JSONObject(dataResponse)
        println(compriseJson)
        name.text = compriseJson["nama"].toString()
        phoneNumber.text = compriseJson["hpagen"].toString()

        val session = Session(this)
        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)

        transfer.setOnClickListener {
            val loading = ProgressDialog(this)
            loading.setTitle("Loading")
            loading.setMessage("Wait while loading...")
            loading.setCancelable(false)
            loading.show()
            try {
                when {
                    (nominal.text.toString().isEmpty() && !nominal.text.isDigitsOnly()) -> Toast.makeText(this, "Total Nominal tidak boleh kosong dan hanya angka", Toast.LENGTH_LONG).show()
                    password.text.toString() == session.getString("pin") -> Timer().schedule(1000) {
                        val description = "Nomor Telfon Anda (${session.getString("phone")} ${session.getString("name")}) telah mentransfer Dari QR Code ke ${phoneNumber.text} dengan nominal ${numberFormat.format(nominal.text.toString().toInt())}/${phoneNumber.text}"
                        val response = TransferController.PostMPay(session.getString("phone").toString(), phoneNumber.text.toString(), nominal.text.toString(), description).execute().get()
                        runOnUiThread {
                            if (response["Status"].toString() == "0") {
                                val gerResponse = UserController.Get(session.getString("phone").toString()).execute().get()
                                session.saveInteger("balance", gerResponse["deposit"].toString().toInt())
                                User.setBalance(gerResponse["deposit"].toString().toInt())
                                if (session.getInteger("type") == 1) {
                                    Handler().postDelayed({
                                        loading.dismiss()
                                        Toast.makeText(applicationContext, response["Pesan"].toString(), Toast.LENGTH_LONG).show()
                                        finish()
                                    }, 1000)
                                    val goTo = Intent(applicationContext, HomeMemberActivity::class.java)
                                    startActivity(goTo)
                                    finish()
                                } else {
                                    Handler().postDelayed({
                                        loading.dismiss()
                                        Toast.makeText(applicationContext, response["Pesan"].toString(), Toast.LENGTH_LONG).show()
                                        finish()
                                    }, 1000)
                                    val goTo = Intent(applicationContext, HomeMerchantActivity::class.java)
                                    startActivity(goTo)
                                    finish()
                                }
                            } else {
                                Handler().postDelayed({
                                    loading.dismiss()
                                    Toast.makeText(applicationContext, response["Pesan"].toString(), Toast.LENGTH_LONG).show()
                                }, 1000)
                            }
                        }
                    }
                    else -> Toast.makeText(this, "password tidak valid", Toast.LENGTH_LONG).show()
                }
            } catch (e : Exception) {
                try {
                    val response = UserController.Get(session.getString("phone").toString()).execute().get()
                    if (session.getString("imei") != response["emai"].toString()) {
                        session.saveString("phone", "")
                        session.saveString("email", "")
                        session.saveString("name", "")
                        session.saveString("pin", "")
                        session.saveInteger("status", 0)
                        session.saveInteger("type", 0)
                        session.saveString("imei", "")

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
                } catch (e : Exception) {
                    session.saveString("phone", "")
                    session.saveString("email", "")
                    session.saveString("name", "")
                    session.saveString("pin", "")
                    session.saveInteger("status", 0)
                    session.saveInteger("type", 0)
                    session.saveString("imei", "")

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
            }
        }
    }
}
