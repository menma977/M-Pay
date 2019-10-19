package com.mp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mp.user.member.HomeMemberActivity
import com.mp.user.merchant.HomeMerchantActivity
import kotlinx.android.synthetic.main.activity_verify_login.*

class VerifyLoginActivity : AppCompatActivity() {

    private var countPasswordWrong = 0
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_login)

        finish()
        val goTo = Intent(this, HomeMemberActivity::class.java)
        startActivity(goTo)

        button1.setOnClickListener {
            password += "1"
            validationPassword()
        }

        button2.setOnClickListener {
            password += "2"
            validationPassword()
        }

        button3.setOnClickListener {
            password += "3"
            validationPassword()
        }

        button4.setOnClickListener {
            password += "4"
            validationPassword()
        }

        button5.setOnClickListener {
            password += "5"
            validationPassword()
        }

        button6.setOnClickListener {
            password += "6"
            validationPassword()
        }

        button7.setOnClickListener {
            password += "7"
            validationPassword()
        }

        button8.setOnClickListener {
            password += "8"
            validationPassword()
        }

        button9.setOnClickListener {
            password += "9"
            validationPassword()
        }

        button0.setOnClickListener {
            password += "0"
            validationPassword()
        }
    }

    private fun validationPassword() {
        if (password.length == 6) {
            if (password == "462066") {
                val goTo = Intent(this, HomeMerchantActivity::class.java)
                startActivity(goTo)
                finish()
            } else {
                password = ""
                Toast.makeText(this, R.string.validationPassword, Toast.LENGTH_LONG).show()
            }
        }
    }
}
