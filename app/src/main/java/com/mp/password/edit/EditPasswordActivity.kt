package com.mp.password.edit

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.mp.MainActivity
import com.mp.R
import com.mp.controller.PasswordController
import com.mp.controller.RegisterController
import com.mp.model.Session
import com.mp.password.update.UpdatePasswordActivity
import kotlinx.android.synthetic.main.activity_edit_password.*
import kotlinx.android.synthetic.main.activity_register_user.*

class EditPasswordActivity : AppCompatActivity() {

    private var countPasswordWrong = 0
    private var getPin = ""

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_password)

        val session = Session(this)

        val sendPinButton: Button = findViewById(R.id.sendPin)

        sendPinButton.setOnClickListener {
            val response =
                PasswordController.SendCode(session.getString("phone").toString()).execute().get()
            println(response.toString())
            if (response["status"].toString() == "0") {
                getPin = response["code"].toString()
            } else {
                Toast.makeText(this, response["massage"].toString(), Toast.LENGTH_LONG).show()
            }
        }

        back.setOnClickListener {
            finish()
        }

        reset.setOnClickListener {
            pin.text = ""
        }

        button1.setOnClickListener {
            pin.text = "${pin.text}1"
            validationPassword()
        }

        button2.setOnClickListener {
            pin.text = "${pin.text}2"
            validationPassword()
        }

        button3.setOnClickListener {
            pin.text = "${pin.text}3"
            validationPassword()
        }

        button4.setOnClickListener {
            pin.text = "${pin.text}4"
            validationPassword()
        }

        button5.setOnClickListener {
            pin.text = "${pin.text}5"
            validationPassword()
        }

        button6.setOnClickListener {
            pin.text = "${pin.text}6"
            validationPassword()
        }

        button7.setOnClickListener {
            pin.text = "${pin.text}7"
            validationPassword()
        }

        button8.setOnClickListener {
            pin.text = "${pin.text}8"
            validationPassword()
        }

        button9.setOnClickListener {
            pin.text = "${pin.text}9"
            validationPassword()
        }

        button0.setOnClickListener {
            pin.text = "${pin.text}0"
            validationPassword()
        }
    }

    private fun validationPassword() {
        if (pin.text.length == 6) {
            if (pin.text == getPin) {
                val goTo = Intent(this, UpdatePasswordActivity::class.java)
                startActivity(goTo)
                finish()
            } else {
                countPasswordWrong++
                if (countPasswordWrong >= 3) {
                    finishAffinity()
                } else {
                    pin.text = ""
                    Toast.makeText(this, "Password Tidak Cocok", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
