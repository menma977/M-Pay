package com.mp.user.ppob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mp.R
import com.mp.user.ppob.bpjs.BpjsRequestActivity
import com.mp.user.ppob.dana.DanaRequestActivity
import com.mp.user.ppob.eMoneyMandiri.EMoneyMandiriRequestActivity
import com.mp.user.ppob.goPay.GoPayRequestActivity
import com.mp.user.ppob.grap.GrabRequestActivity
import com.mp.user.ppob.insurance.InsuranceRequestActivity
import com.mp.user.ppob.multiFinance.MultiFinanceRequestActivity
import com.mp.user.ppob.ovo.OvoRequestActivity
import com.mp.user.ppob.payment.PaymentRequestActivity
import com.mp.user.ppob.pdam.PDAMRequestActivity
import com.mp.user.ppob.pln.PlnRequestActivity
import com.mp.user.ppob.plnCredit.PLNCreditRequestActivity
import com.mp.user.ppob.postPaid.PostPaidRequestActivity
import com.mp.user.ppob.postPaidCredit.PostPaidCreditRequestActivity
import com.mp.user.ppob.tabCashBNI.TabCashBNIActivity
import com.mp.user.ppob.wifi.WifiRequestActivity
import kotlinx.android.synthetic.main.activity_more.*

class MoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)
        var goTo : Intent

        plnButton.setOnClickListener {
            goTo = Intent(this, PlnRequestActivity::class.java)
            startActivity(goTo)
        }

        PostPaidButton.setOnClickListener {
            goTo = Intent(this, PostPaidRequestActivity::class.java)
            startActivity(goTo)
        }

        wifiButton.setOnClickListener {
            goTo = Intent(this, WifiRequestActivity::class.java)
            startActivity(goTo)
        }

        PostPaidCreditButton.setOnClickListener {
            goTo = Intent(this, PostPaidCreditRequestActivity::class.java)
            startActivity(goTo)
        }

        GoPayButton.setOnClickListener {
            goTo = Intent(this, GoPayRequestActivity::class.java)
            startActivity(goTo)
        }

        GrabButton.setOnClickListener {
            goTo = Intent(this, GrabRequestActivity::class.java)
            startActivity(goTo)
        }

        OVOButton.setOnClickListener {
            goTo = Intent(this, OvoRequestActivity::class.java)
            startActivity(goTo)
        }

        danaButton.setOnClickListener {
            goTo = Intent(this, DanaRequestActivity::class.java)
            startActivity(goTo)
        }

        insuranceButton.setOnClickListener {
            goTo = Intent(this, InsuranceRequestActivity::class.java)
            startActivity(goTo)
        }

        bpjsButton.setOnClickListener {
            goTo = Intent(this, BpjsRequestActivity::class.java)
            startActivity(goTo)
        }

        financeButton.setOnClickListener {
            goTo = Intent(this, MultiFinanceRequestActivity::class.java)
            startActivity(goTo)
        }

        paymentButton.setOnClickListener {
            goTo = Intent(this, PaymentRequestActivity::class.java)
            startActivity(goTo)
        }

        eMandiriButton.setOnClickListener {
            goTo = Intent(this, EMoneyMandiriRequestActivity::class.java)
            startActivity(goTo)
        }

        tabCashBNIButton.setOnClickListener {
            goTo = Intent(this, TabCashBNIActivity::class.java)
            startActivity(goTo)
        }

        pdamButton.setOnClickListener {
            goTo = Intent(this, PDAMRequestActivity::class.java)
            startActivity(goTo)
        }

        plnCreditButton.setOnClickListener {
            goTo = Intent(this, PLNCreditRequestActivity::class.java)
            startActivity(goTo)
        }

        vocerButton.setOnClickListener {
            Toast.makeText(this, "On Build", Toast.LENGTH_LONG).show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
