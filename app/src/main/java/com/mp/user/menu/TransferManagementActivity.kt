package com.mp.user.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mp.R
import com.mp.model.Session
import com.mp.user.menu.bank.BankActivity
import com.mp.user.menu.mpay.MPayActivity
import kotlinx.android.synthetic.main.activity_transfer_management.*

class TransferManagementActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_management)

        val session = Session(this)
        if (session.getInteger("status") == 0) {
            Toast.makeText(this, "Nomor Telfon anda belum di validasi oleh admin mohon tunggu 1x24jam atau hubungi admin", Toast.LENGTH_LONG).show()
            finish()
        }

        var goTo : Intent
        mPayButton.setOnClickListener {
            goTo = Intent(this, MPayActivity::class.java)
            startActivity(goTo)
        }

        BankButton.setOnClickListener {
            goTo = Intent(this, BankActivity::class.java)
            startActivity(goTo)
        }
    }
}
