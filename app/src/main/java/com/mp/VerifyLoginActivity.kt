package com.mp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mp.user.member.HomeMemberActivity
import com.mp.user.merchant.HomeMerchantActivity
import kotlinx.android.synthetic.main.activity_verify_login.*
import java.lang.Exception

class VerifyLoginActivity : AppCompatActivity() {

    private var countPasswordWrong = 0
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_login)

//        finish()
//        val goTo = Intent(this, HomeMemberActivity::class.java)
//        startActivity(goTo)

        back.setOnClickListener {
            val goTo = Intent(this, MainActivity::class.java)
            startActivity(goTo)
            finish()
        }

        reset.setOnClickListener {
            password = ""
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
            if (password == "462066") {
                val goTo = Intent(this, HomeMerchantActivity::class.java)
                startActivity(goTo)
                finish()
            } else {
                password = ""
                setTextToInput(password)
                Toast.makeText(this, R.string.validationPassword, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setTextToInput(value : String) {
        try {
            if (value.isEmpty()) {
                passwordText.setText("")
            } else {
                passwordText.setText(value)
            }
        } catch (e : Exception) {

        }
    }
}
