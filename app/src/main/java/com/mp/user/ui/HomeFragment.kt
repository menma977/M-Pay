package com.mp.user.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mp.R
import com.mp.controller.UserController
import com.mp.model.Session
import com.mp.model.User
import com.mp.user.menu.MPayIdActivity
import com.mp.user.menu.ScanActivity
import com.mp.user.ppob.MoreActivity
import com.mp.user.ppob.dana.DanaRequestActivity
import com.mp.user.ppob.goPay.GoPayRequestActivity
import com.mp.user.ppob.grap.GrabRequestActivity
import com.mp.user.ppob.ovo.OvoRequestActivity
import com.mp.user.ppob.pln.PlnRequestActivity
import com.mp.user.ppob.postPaid.PostPaidRequestActivity
import com.mp.user.ppob.postPaidCredit.PostPaidCreditRequestActivity
import com.mp.user.ppob.wifi.WifiRequestActivity
import java.text.NumberFormat
import java.util.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)

        val balance = root.findViewById<TextView>(R.id.balance)
        val reloadBalance = root.findViewById<ImageButton>(R.id.reloadBalance)
        balance.text = numberFormat.format(if(User.getBalance() != null) User.getBalance() else 0)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                try {
                    val session = Session(root.context)
                    val response = UserController.Get(session.getString("phone").toString()).execute().get()
                    if (response["Status"].toString() == "0") {
                        session.saveString("phone", response["hpagen"].toString())
                        session.saveString("email", response["email"].toString())
                        session.saveString("name", response["nama"].toString())
                        session.saveString("pin", response["password"].toString())
                        session.saveInteger("status", response["statusmember"].toString().toInt())
                        session.saveInteger("type", response["tipeuser"].toString().toInt())
                        session.saveInteger("balance", response["deposit"].toString().toInt())

                        User.setPhone(response["hpagen"].toString())
                        User.setEmail(response["email"].toString())
                        User.setName(response["nama"].toString())
                        User.setPin(response["password"].toString())
                        User.setType(response["tipeuser"].toString().toInt())
                        User.setStatus(response["statusmember"].toString().toInt())
                        User.setBalance(response["deposit"].toString().toInt())

                        balance.text = numberFormat.format(if(User.getBalance() != null) User.getBalance() else 0)
                    }
                } catch (e : Exception) {
                    balance.text = numberFormat.format(if(User.getBalance() != null) User.getBalance() else 0)
                }
            }
        }, 1000)

        reloadBalance.setOnClickListener {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    try {
                        val response = UserController.Get(User.getPhone()).execute().get()
                        if (response["Status"].toString() == "0") {
                            User.setPhone(response["hpagen"].toString())
                            User.setEmail(response["email"].toString())
                            User.setName(response["nama"].toString())
                            User.setPin(response["password"].toString())
                            User.setType(response["tipeuser"].toString().toInt())
                            User.setStatus(response["statusmember"].toString().toInt())
                            User.setBalance(response["deposit"].toString().toInt())

                            balance.text = numberFormat.format(if(User.getBalance() != null) User.getBalance() else 0)
                        }
                    } catch (e : Exception) {
                        balance.text = numberFormat.format(if(User.getBalance() != null) User.getBalance() else 0)
                    }
                }
            }, 1000)
        }

        //menu
        var goTo : Intent
        val mPayId = root.findViewById<LinearLayout>(R.id.mPayId)
        val scan = root.findViewById<LinearLayout>(R.id.scan)

        mPayId.setOnClickListener {
            goTo = Intent(root.context, MPayIdActivity::class.java)
            startActivity(goTo)
        }

        scan.setOnClickListener {
            goTo = Intent(root.context, ScanActivity::class.java)
            startActivity(goTo)
        }

        val more = root.findViewById<LinearLayout>(R.id.more)

        more.setOnClickListener {
            goTo = Intent(root.context, MoreActivity::class.java)
            startActivity(goTo)
        }

        //ppiob
        val postPaid : LinearLayout = root.findViewById(R.id.PostPaidButton)
        val postPaidCredit : LinearLayout = root.findViewById(R.id.PostPaidCreditButton)
        val pln : LinearLayout = root.findViewById(R.id.plnButton)
        val wifi : LinearLayout = root.findViewById(R.id.wifiButton)
        val goPay : LinearLayout = root.findViewById(R.id.GoPayButton)
        val grab : LinearLayout = root.findViewById(R.id.GrabButton)
        val ovo : LinearLayout = root.findViewById(R.id.OVOButton)

        postPaid.setOnClickListener {
            goTo = Intent(root.context, PostPaidRequestActivity::class.java)
            startActivity(goTo)
        }

        postPaidCredit.setOnClickListener {
            goTo = Intent(root.context, PostPaidCreditRequestActivity::class.java)
            startActivity(goTo)
        }

        pln.setOnClickListener {
            goTo = Intent(root.context, PlnRequestActivity::class.java)
            startActivity(goTo)
        }

        wifi.setOnClickListener {
            goTo = Intent(root.context, WifiRequestActivity::class.java)
            startActivity(goTo)
        }

        goPay.setOnClickListener {
            goTo = Intent(root.context, GoPayRequestActivity::class.java)
            startActivity(goTo)
        }

        grab.setOnClickListener {
            goTo = Intent(root.context, GrabRequestActivity::class.java)
            startActivity(goTo)
        }

        ovo.setOnClickListener {
            goTo = Intent(root.context, OvoRequestActivity::class.java)
            startActivity(goTo)
        }

        return root
    }
}