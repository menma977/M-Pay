package com.mp.controller

import android.os.AsyncTask
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class FinanceController {
    class GetAll(private val phone: String) : AsyncTask<Void, Void, JSONObject>() {
        override fun doInBackground(vararg params: Void?): JSONObject {
            try {
                val userAgent = "Mozilla/5.0"
                val url = URL("http://152601282294.ip-dynamic.com/mpmdp/apidephis.php")
                val httpURLConnection = url.openConnection() as HttpURLConnection

                //add request header
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("User-Agent", userAgent)
                httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                httpURLConnection.setRequestProperty("Accept", "application/json")

                val urlParameters = "username=${phone}"

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
                    val response = JSONObject(inputData)
                    input.close()
                    response
                } else {
                    JSONObject("{Status: 1, Pesan: 'internet tidak setabil'}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return JSONObject("{Status: 1, Pesan: 'internet tidak setabil'}")
            }
        }
    }

    class GetDetail(private val phone: String, private val trx: String) :
        AsyncTask<Void, Void, JSONObject>() {
        override fun doInBackground(vararg params: Void?): JSONObject {
            try {
                val userAgent = "Mozilla/5.0"
                val url = URL("http://152601282294.ip-dynamic.com/mpmdp/apidephis.php")
                val httpURLConnection = url.openConnection() as HttpURLConnection

                //add request header
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("User-Agent", userAgent)
                httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                httpURLConnection.setRequestProperty("Accept", "application/json")

                val urlParameters = "username=$phone&idtrx=$trx"

                // Send post request
                httpURLConnection.doOutput = true
                val write = DataOutputStream(httpURLConnection.outputStream)
                write.writeBytes(urlParameters)
                write.flush()
                write.close()

                val responseCode = httpURLConnection.responseCode
                println(responseCode)
                return if (responseCode == 200) {
                    val input = BufferedReader(
                        InputStreamReader(httpURLConnection.inputStream)
                    )

                    val inputData: String = input.readLine()

                    val response = JSONObject(inputData)
                    input.close()
                    response
                } else {
                    JSONObject("{trx : {Status: 1, Pesan: 'internet tidak setabil'}}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return JSONObject("{trx : {Status: 1, Pesan: 'internet tidak setabil'}}")
            }
        }
    }
}