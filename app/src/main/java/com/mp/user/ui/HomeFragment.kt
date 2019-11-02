@file:Suppress("DEPRECATION")

package com.mp.user.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.mp.user.menu.TransferManagementActivity
import com.mp.user.ppob.MoreActivity
import com.mp.user.ppob.goPay.GoPayRequestActivity
import com.mp.user.ppob.grap.GrabRequestActivity
import com.mp.user.ppob.ovo.OvoRequestActivity
import com.mp.user.ppob.pln.PlnRequestActivity
import com.mp.user.ppob.postPaid.PostPaidRequestActivity
import com.mp.user.ppob.postPaidCredit.PostPaidCreditRequestActivity
import com.mp.user.ppob.wifi.WifiRequestActivity
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val idr = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(idr)

        val balance: TextView = root.findViewById(R.id.balance)
        val reloadBalance: ImageButton = root.findViewById(R.id.reloadBalance)
        val nameUser: TextView = root.findViewById(R.id.name_user)
        balance.text = numberFormat.format(if (User.getBalance() != null) User.getBalance() else 0)

        Timer().schedule(5000, 10000) {
            val response = UserController.Get(User.getPhone()).execute().get()
            if (response["Status"].toString() == "0") {
                val session = Session(root.context)
                session.saveInteger(
                    "balance",
                    response["deposit"].toString().toInt()
                )
                session.saveString("support", response["hpkomplen"].toString())
                User.setBalance(response["deposit"].toString().toInt())
                balance.text =
                    numberFormat.format(if (User.getBalance() != null) User.getBalance() else 0)
            }
        }

        nameUser.text = Session(root.context).getString("name")

        reloadBalance.setOnClickListener {
            val loading = ProgressDialog(root.context)
            loading.setTitle("Loading")
            loading.setMessage("Wait while loading...")
            loading.setCancelable(false)
            loading.show()
            Timer().schedule(1000) {
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
                        loading.dismiss()
                        balance.text =
                            numberFormat.format(if (User.getBalance() != null) User.getBalance() else 0)
                    }
                } catch (e: Exception) {
                    loading.dismiss()
                    balance.text =
                        numberFormat.format(if (User.getBalance() != null) User.getBalance() else 0)
                }
            }
        }

        //menu
        var goTo: Intent
        val mPayId = root.findViewById<LinearLayout>(R.id.mPayId)
        val scan = root.findViewById<LinearLayout>(R.id.scan)
        val transferButton = root.findViewById<LinearLayout>(R.id.transferButton)

        mPayId.setOnClickListener {
            goTo = Intent(root.context, MPayIdActivity::class.java)
            startActivity(goTo)
        }

        scan.setOnClickListener {
            goTo = Intent(root.context, ScanActivity::class.java)
            startActivity(goTo)
        }

        transferButton.setOnClickListener {
            goTo = Intent(root.context, TransferManagementActivity::class.java)
            startActivity(goTo)
        }

        val more = root.findViewById<LinearLayout>(R.id.more)

        more.setOnClickListener {
            goTo = Intent(root.context, MoreActivity::class.java)
            startActivity(goTo)
        }

        //ppiob
        val postPaid: LinearLayout = root.findViewById(R.id.PostPaidButton)
        val postPaidCredit: LinearLayout = root.findViewById(R.id.PostPaidCreditButton)
        val pln: LinearLayout = root.findViewById(R.id.plnButton)
        val wifi: LinearLayout = root.findViewById(R.id.wifiButton)
        val goPay: LinearLayout = root.findViewById(R.id.GoPayButton)
        val grab: LinearLayout = root.findViewById(R.id.GrabButton)
        val ovo: LinearLayout = root.findViewById(R.id.OVOButton)

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