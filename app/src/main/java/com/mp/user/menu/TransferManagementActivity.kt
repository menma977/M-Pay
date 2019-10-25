package com.mp.user.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mp.R
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
