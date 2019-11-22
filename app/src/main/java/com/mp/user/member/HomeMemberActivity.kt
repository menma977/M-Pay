package com.mp.user.member

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.mp.MainActivity
import com.mp.R
import com.mp.controller.UserController
import com.mp.model.Session
import com.mp.model.User
import com.mp.user.UploadImageActivity
import com.mp.user.finance.FinanceManagementActivity
import kotlinx.android.synthetic.main.activity_home_member.*
import java.util.*

class HomeMemberActivity : AppCompatActivity() {

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_member)

        var session: Session? = Session(this)

        User.setImei(getIEMI())

        if (session!!.getInteger("status") == 0) {
            val fromForBtn: LinearLayout = findViewById(R.id.formForButton)
            fromForBtn.removeAllViews()
            val optionValue = LinearLayout.LayoutParams(50, 50)
            val link = ContextCompat.getColor(this, R.color.Link)
            val imageButton = ImageButton(this)
            imageButton.layoutParams = optionValue
            imageButton.setBackgroundColor(link)
            imageButton.setBackgroundResource(R.drawable.ic_cloud_upload_black_24dp)
            imageButton.setOnClickListener {
                val goTo = Intent(this, UploadImageActivity::class.java)
                startActivity(goTo)
                finish()
                session = null
            }
            fromForBtn.addView(imageButton)
        }

        val logout: ImageButton = findViewById(R.id.logout)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                try {
                    val response =
                        UserController.Get(session!!.getString("phone").toString()).execute().get()
                    if (response["Status"].toString() == "0") {
                        if (response["emai"].toString() == session!!.getString("imei")) {
                            session!!.saveString("phone", response["hpagen"].toString())
                            session!!.saveString("email", response["email"].toString())
                            session!!.saveString("name", response["nama"].toString())
                            session!!.saveString("pin", response["password"].toString())
                            session!!.saveInteger(
                                "status",
                                response["statusmember"].toString().toInt()
                            )
                            session!!.saveInteger("type", response["tipeuser"].toString().toInt())
                            session!!.saveInteger("balance", response["deposit"].toString().toInt())

                            User.setPhone(response["hpagen"].toString())
                            User.setEmail(response["email"].toString())
                            User.setName(response["nama"].toString())
                            User.setPin(response["password"].toString())
                            User.setType(response["tipeuser"].toString().toInt())
                            User.setStatus(response["statusmember"].toString().toInt())
                            User.setBalance(response["deposit"].toString().toInt())
                        } else {
                            session!!.clear()

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
                } catch (e: Exception) {
                    session!!.clear()

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
        }, 1000)

        history.setOnClickListener {
            val goTo = Intent(this, FinanceManagementActivity::class.java)
            startActivity(goTo)
        }

        logout.setOnClickListener {
            session!!.clear()

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
