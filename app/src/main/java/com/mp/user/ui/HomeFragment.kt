package com.mp.user.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.mp.R
import com.mp.user.menu.MPayIdActivity
import com.mp.user.menu.ScanActivity
import com.mp.user.ppob.MoreActivity

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
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

        return root
    }
}