package com.mp.user.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.zxing.Result
import com.mp.R
import com.mp.model.Scan
import kotlinx.android.synthetic.main.activity_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var mScannerView: ZXingScannerView

    override fun handleResult(responseQR : Result) {
        if (responseQR.text.toString().isNotEmpty()) {
            val scan = Scan()
            scan.barcodeQR = responseQR.text.toString()
            Toast.makeText(this, "Melanjutkan ke nominal yang akan di isi", Toast.LENGTH_LONG).show()
            val goTo = Intent(this, SetNominalActivity::class.java)
            startActivity(goTo)
            finish()
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
