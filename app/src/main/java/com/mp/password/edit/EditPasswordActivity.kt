package com.mp.password.edit

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.mp.MainActivity
import com.mp.R
import com.mp.password.update.UpdatePasswordActivity
import kotlinx.android.synthetic.main.activity_edit_password.*
import kotlinx.android.synthetic.main.activity_register_user.*

class EditPasswordActivity : AppCompatActivity() {

    private var countPasswordWrong = 0

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_password)

        back.setOnClickListener {
            val goTo = Intent(this, MainActivity::class.java)
            startActivity(goTo)
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
            if (pin.text == "462066") {
                val goTo = Intent(this, UpdatePasswordActivity::class.java)
                startActivity(goTo)
                finish()
            } else {
                pin.text = ""
                Toast.makeText(this, "Password Tidak Cocok", Toast.LENGTH_LONG).show()
            }
        }
    }
}
