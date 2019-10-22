package com.mp.user.merchant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mp.MainActivity
import com.mp.R
import com.mp.model.Session
import com.mp.model.User
import com.mp.user.ui.HomeFragment
import com.mp.user.ui.MenuFragment

class HomeMerchantActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val fragment = HomeFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_menu -> {
                val fragment = MenuFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    @SuppressLint("PrivateResource")
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.nav_host_fragment, fragment, fragment.javaClass.simpleName).commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_merchant)
        val navController = findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val logout : ImageButton = findViewById(R.id.logout)

        //navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        logout.setOnClickListener {
            val session = Session(this)
            session.saveString("phoneUser" , "")
            session.saveString("pinUser" , "")
            session.saveString("typeUser" , "")
            User.setPhone("")
            User.setPin("")
            User.setType(0)
            val goTo = Intent(this, MainActivity::class.java)
            startActivity(goTo)
            finish()
        }

        navView.setupWithNavController(navController)
    }
}
