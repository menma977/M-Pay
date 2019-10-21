package com.mp.password.update

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mp.R

class UpdatePasswordActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)
    }
}
