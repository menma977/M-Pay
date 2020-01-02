package com.mp.controller.ppob

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.mp.model.User
import org.json.JSONArray
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class HlrController(private val phone : String) : AsyncTask<Void, Void, JSONArray>() {
    override fun doInBackground(vararg params: Void?): JSONArray {
        try {
            val userAgent = "Mozilla/5.0"
            val url = URL("${User.getUrl()}/hlr.php")
            val httpURLConnection = url.openConnection() as HttpURLConnection

            //add request header
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.setRequestProperty("User-Agent", userAgent)
            httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
            httpURLConnection.setRequestProperty("Accept", "application/json")

            val urlParameters = "a=Hlr&username=${phone}"+
                    "&uzer=2d21dbba2eded322b504c811170190d6"+
                    "&passw=fd1d59e3076e99f13f29a783ac79aecf"

            // Send post request
            httpURLConnection.doOutput = true
            val write = DataOutputStream(httpURLConnection.outputStream)
            write.writeBytes(urlParameters)
            write.flush()
            write.close()

            val responseCode = httpURLConnection.responseCode
            return if (responseCode == 200) {
                val input = BufferedReader(
                    InputStreamReader(httpURLConnection.inputStream)
                )

                val inputData: String = input.readLine()
                val response = JSONArray(inputData)
                input.close()
                response
            } else {
                JSONArray("[{Status: 1, Pesan: 'internet tidak setabil'}, {Status: 1, Pesan: 'internet tidak setabil'}]")
            }
        }catch (e : Exception) {
            e.printStackTrace()
            return JSONArray("[{Status: 1, Pesan: 'internet tidak setabil'}, {Status: 1, Pesan: 'internet tidak setabil'}]")
        }
    }
}