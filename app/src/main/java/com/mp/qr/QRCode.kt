package com.mp.qr

import android.graphics.Bitmap
import net.glxn.qrgen.android.QRCode as QR

class QRCode(private val value : String) {

    fun qrToBitmap() : Bitmap {
        return QR.from(value).bitmap()
    }

}