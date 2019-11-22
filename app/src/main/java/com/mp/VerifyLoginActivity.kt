@file:Suppress("DEPRECATION")

package com.mp

import android.Manifest
import android.annotation.TargetApi
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mp.controller.IMEIController
import com.mp.controller.PlayStoreController
import com.mp.controller.UserController
import com.mp.model.Session
import com.mp.model.User
import com.mp.user.member.HomeMemberActivity
import com.mp.user.merchant.HomeMerchantActivity
import kotlinx.android.synthetic.main.activity_verify_login.*
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule
import android.net.Uri
import android.os.Build

class VerifyLoginActivity : AppCompatActivity() {

    private var statusUpdate = true
    private var countPasswordWrong = 0
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_login)
        val session = Session(this)

        Timer().schedule(1000) {
            val responseCodePlayStore =
                PlayStoreController.GetVersion(BuildConfig.APPLICATION_ID).execute().get()
            if (responseCodePlayStore["Status"].toString() == "0") {
                statusUpdate = true
                if (responseCodePlayStore["Version"].toString() != BuildConfig.VERSION_NAME) {
                    try {
                        runOnUiThread {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                                )
                            )
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                                )
                            )
                        }
                    }
                } else {
                    statusUpdate = false
                }
            } else {
                statusUpdate = true
                finishAffinity()
            }
        }

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
                            session.saveString("support", response["hpkomplen"].toString())

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

        back.setOnClickListener {
            session.clear()

            User.setPhone("")
            User.setEmail("")
            User.setName("")
            User.setPin("")
            User.setType(0)
            User.setStatus(0)
            val goTo = Intent(this, MainActivity::class.java)
            startActivity(goTo)
            finish()
        }

        reset.setOnClickListener {
            password = ""
            setTextToInput(password)
        }

        button1.setOnClickListener {
            password += "1"
            setTextToInput(password)
            validationPassword()
        }

        button2.setOnClickListener {
            password += "2"
            setTextToInput(password)
            validationPassword()
        }

        button3.setOnClickListener {
            password += "3"
            setTextToInput(password)
            validationPassword()
        }

        button4.setOnClickListener {
            password += "4"
            setTextToInput(password)
            validationPassword()
        }

        button5.setOnClickListener {
            password += "5"
            setTextToInput(password)
            validationPassword()
        }

        button6.setOnClickListener {
            password += "6"
            setTextToInput(password)
            validationPassword()
        }

        button7.setOnClickListener {
            password += "7"
            setTextToInput(password)
            validationPassword()
        }

        button8.setOnClickListener {
            password += "8"
            setTextToInput(password)
            validationPassword()
        }

        button9.setOnClickListener {
            password += "9"
            setTextToInput(password)
            validationPassword()
        }

        button0.setOnClickListener {
            password += "0"
            setTextToInput(password)
            validationPassword()
        }
    }

    private fun validationPassword() {
        if (password.length == 6) {
            when (password) {
                User.getPin() -> {
                    val loading = ProgressDialog(this)
                    loading.setTitle("Loading")
                    loading.setMessage("Wait while loading...")
                    loading.setCancelable(false)
                    loading.show()
                    Timer().schedule(1000) {
                        when {
                            statusUpdate -> runOnUiThread {
                                loading.dismiss()
                                finishAffinity()
                            }
                            else -> Timer().schedule(1000) {
                                if (sendIEMI()) {
                                    runOnUiThread {
                                        Handler().postDelayed({
                                            loading.dismiss()
                                            if (User.getType() == 1) {
                                                val goTo = Intent(
                                                    applicationContext,
                                                    HomeMemberActivity::class.java
                                                )
                                                startActivity(goTo)
                                                finish()
                                            } else {
                                                val goTo = Intent(
                                                    applicationContext,
                                                    HomeMerchantActivity::class.java
                                                )
                                                startActivity(goTo)
                                                finish()
                                            }
                                        }, 1000)
                                    }
                                } else {
                                    runOnUiThread {
                                        Handler().postDelayed({
                                            loading.dismiss()
                                            val goTo =
                                                Intent(applicationContext, MainActivity::class.java)
                                            startActivity(goTo)
                                            finish()
                                        }, 1000)
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    password = ""
                    countPasswordWrong += 1
                    if (countPasswordWrong == 3) {
                        val session = Session(this)
                        session.clear()

                        User.setPhone("")
                        User.setEmail("")
                        User.setName("")
                        User.setPin("")
                        User.setType(0)
                        User.setStatus(0)
                        val goTo = Intent(this, MainActivity::class.java)
                        startActivity(goTo)
                        finish()
                    } else {
                        setTextToInput(password)
                        Toast.makeText(this, R.string.validationPassword, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setTextToInput(value: String) {
        try {
            if (value.isEmpty()) {
                passwordText.setText("")
            } else {
                passwordText.setText(value)
            }
        } catch (e: Exception) {

        }
    }

    private fun sendIEMI(): Boolean {
        return if (User.getPhone().isNotEmpty()) {
            val response = IMEIController.sendIMEI(User.getPhone(), getIEMI()).execute().get()
            return if (response["Status"].toString() == "1") {
                val session = Session(this)
                session.clear()

                User.setPhone("")
                User.setEmail("")
                User.setName("")
                User.setPin("")
                User.setType(0)
                User.setStatus(0)
                false
            } else {
                val session = Session(this)
                session.saveString("imei", getIEMI())
                true
            }
        } else {
            false
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun getIEMI(): String {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 2)
                val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                tm.imei
            } else {
                User.getPhone()
            }
        } else {
            User.getPhone()
        }
    }
}
