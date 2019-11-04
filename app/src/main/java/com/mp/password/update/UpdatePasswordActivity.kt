package com.mp.password.update

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.mp.MainActivity
import com.mp.R
import com.mp.controller.PasswordController
import com.mp.model.Session
import java.util.*
import kotlin.concurrent.schedule

class UpdatePasswordActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)
        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)

        val session = Session(this)
        val newPassword: EditText = findViewById(R.id.newPassword)
        val newPasswordRepeat: EditText = findViewById(R.id.newPasswordValidation)
        val updatePassword: Button = findViewById(R.id.updatePassword)

        updatePassword.setOnClickListener {
            loading.show()
            if (newPassword.text.isEmpty()) {
                loading.dismiss()
                Toast.makeText(this, "Password Baru tidak boleh kosong", Toast.LENGTH_LONG).show()
                newPassword.requestFocus()
            } else if (!newPassword.text.isDigitsOnly()) {
                loading.dismiss()
                Toast.makeText(this, "Password Baru hanya boleh angka", Toast.LENGTH_LONG).show()
                newPassword.requestFocus()
            } else if (newPassword.text.toString() != newPasswordRepeat.text.toString()) {
                loading.dismiss()
                Toast.makeText(this, "Password yang anda inputkan harus sama", Toast.LENGTH_LONG)
                    .show()
                newPasswordRepeat.requestFocus()
            } else {
                Timer().schedule(1000) {
                    val response = PasswordController.UpdatePassword(
                        session.getString("phone").toString(),
                        session.getString("pin").toString(),
                        newPassword.text.toString(),
                        getIEMI()
                    ).execute().get()
                    if (response["Status"].toString() == "0") {
                        runOnUiThread {
                            loading.dismiss()
                            Toast.makeText(
                                applicationContext,
                                response["Pesan"].toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            session.clear()
                            val goTo = Intent(applicationContext, MainActivity::class.java)
                            startActivity(goTo)
                        }
                    } else {
                        runOnUiThread {
                            loading.dismiss()
                            Toast.makeText(
                                applicationContext,
                                response["Pesan"].toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun getIEMI(): String {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 2)
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            tm.imei
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 2)
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            tm.imei
        }
    }
}
