package com.mp.user.member

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.mp.MainActivity
import com.mp.R
import com.mp.model.Session
import com.mp.model.User

class HomeMemberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_member)
        val logout : ImageButton = findViewById(R.id.logout)

        logout.setOnClickListener {
            val session = Session(this)
            session.saveString("phone", "")
            session.saveString("email", "")
            session.saveString("name", "")
            session.saveString("pin", "")
            session.saveInteger("status", 0)
            session.saveInteger("type", 0)

            User.setPhone("")
            User.setEmail("")
            User.setName("")
            User.setPin("")
            User.setType(0)
            User.setStatus(0)
            val goTo = Intent(this, MainActivity::class.java)
            startActivity(goTo)
            finish()
        }
    }
}
