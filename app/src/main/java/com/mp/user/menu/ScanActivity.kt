package com.mp.user.menu

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.zxing.Result
import com.mp.R
import com.mp.controller.UserController
import kotlinx.android.synthetic.main.activity_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.*
import kotlin.concurrent.schedule

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var mScannerView: ZXingScannerView

    override fun handleResult(responseQR : Result) {
        if (responseQR.text.toString().isNotEmpty()) {
            val loading = ProgressDialog(this)
            loading.setTitle("Loading")
            loading.setMessage("Wait while loading...")
            loading.setCancelable(false)
            Handler().postDelayed({
                loading.show()
            }, 200)
            Timer().schedule(1000) {
                val response = UserController.Get(responseQR.text.toString()).execute().get()
                if (response["Status"].toString() == "0") {
                    runOnUiThread {
                        Handler().postDelayed({
                            loading.dismiss()
                        }, 200)
                        Toast.makeText(applicationContext, "Melanjutkan ke nominal yang akan di isi", Toast.LENGTH_LONG).show()
                        val goTo = Intent(applicationContext, SetNominalActivity::class.java).putExtra("response", response.toString())
                        startActivity(goTo)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Handler().postDelayed({
                            loading.dismiss()
                        }, 200)
                        Toast.makeText(applicationContext, "QR belum terdaftar", Toast.LENGTH_LONG).show()
                        val goTo = Intent(applicationContext, ScanActivity::class.java).putExtra("response", response.toString())
                        startActivity(goTo)
                        finish()
                    }
                }
            }
        }
    }

    override fun onStart() {
        mScannerView.startCamera()
        super.onStart()
    }

    override fun onPause() {
        mScannerView.stopCamera()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        initScannerView()
    }

    private fun initScannerView() {
        mScannerView = ZXingScannerView(this)
        mScannerView.setAutoFocus(true)
        mScannerView.setResultHandler(this)
        QRCamera.addView(mScannerView)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
