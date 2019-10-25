package com.mp.user.menu.mpay

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
import kotlinx.android.synthetic.main.activity_mpay.*
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class MPayActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpay)

        val session = Session(this)
        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)

        transferButton.setOnClickListener {
            if (!phoneNumberTarget.text.isDigitsOnly()) {
                Toast.makeText(this, "nomor telfon hanya boleh angka dan tidak boleh kosong", Toast.LENGTH_LONG).show()
            } else if(phoneNumberTarget.text.toString() == session.getString("phone")) {
                Toast.makeText(this, "Anda Tidak bisa topup ke nomor telfon anda sendiri", Toast.LENGTH_LONG).show()
            } else if (!nominal.text.isDigitsOnly()) {
                Toast.makeText(this, "nominal hanya boleh angka dan tidak boleh kosong", Toast.LENGTH_LONG).show()
            } else if (!password.text.isDigitsOnly()) {
                Toast.makeText(this, "password hanya boleh angka dan tidak boleh kosong", Toast.LENGTH_LONG).show()
            } else {
                loading.show()
                Timer().schedule(1000) {
                    try {
                        val userResponse = UserController.Get(session.getString("phone").toString()).execute().get()
                        session.saveInteger("balance", userResponse["deposit"].toString().toInt())
                        User.setBalance(userResponse["deposit"].toString().toInt())
                        if (userResponse["Status"].toString() == "0") {
                            val response = TransferController.PostMPay(session.getString("phone").toString(), phoneNumberTarget.text.toString(), nominal.text.toString(), description.text.toString()).execute().get()
                            runOnUiThread {
                                if (response["Status"].toString() == "0") {
                                    if (session.getInteger("type") == 1) {
                                        Handler().postDelayed({
                                            loading.dismiss()
                                            Toast.makeText(applicationContext, response["Pesan"].toString(), Toast.LENGTH_LONG).show()
                                            val goTo = Intent(applicationContext, HomeMemberActivity::class.java)
                                            startActivity(goTo)
                                            finish()
                                        }, 1000)
                                    } else {
                                        Handler().postDelayed({
                                            loading.dismiss()
                                            Toast.makeText(applicationContext, response["Pesan"].toString(), Toast.LENGTH_LONG).show()
                                            val goTo = Intent(applicationContext, HomeMerchantActivity::class.java)
                                            startActivity(goTo)
                                            finish()
                                        }, 1000)
                                    }
                                } else {
                                    loading.dismiss()
                                    Toast.makeText(applicationContext, response["Pesan"].toString(), Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            runOnUiThread {
                                loading.dismiss()
                                if (session.getString("imei") != userResponse["emai"].toString()) {
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
                                } else {
                                    loading.dismiss()
                                    Toast.makeText(applicationContext, "Internet bermasalah tolong cari lokasi lebih baik untuk mendapatkan sinyal lebih baik", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    } catch (e : Exception) {
                        runOnUiThread {
                            loading.dismiss()
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
    }
}
