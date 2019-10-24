package com.mp.user.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mp.R
import kotlinx.android.synthetic.main.activity_set_nominal.*
import org.json.JSONObject

class SetNominalActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_nominal)
        val dataResponse = intent.getSerializableExtra("response").toString()
        val compriseJson = JSONObject(dataResponse)
        println(compriseJson)
        name.text = compriseJson["nama"].toString()
        phoneNumber.text = compriseJson["hpagen"].toString()
    }
}
