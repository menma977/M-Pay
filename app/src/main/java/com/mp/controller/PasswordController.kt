package com.mp.controller

import android.os.AsyncTask
import com.mp.model.User
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class PasswordController {
    class SendCode(private val phone: String) : AsyncTask<Void, Void, JSONObject>() {
        override fun doInBackground(vararg params: Void?): JSONObject {
            val response = JSONObject()
            try {
                val userAgent = "Mozilla/5.0"
                val url = URL("http://budisetiyono.com/seypogsms/index.php")
                val httpURLConnection = url.openConnection() as HttpURLConnection

                //add request header
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("User-Agent", userAgent)
                httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                httpURLConnection.setRequestProperty("Accept", "application/json")

                val randomCode = (100000 until 999999).random()

                val urlParameters =
                    "nohp=$phone&isi=hallo $phone ini code untuk validasi nomor telfon anda ($randomCode). jika anda tidak merasa melakukan pengiriman code ini via sms mohon untuk mengganti password anda segera demi keamanan akun anda dari Mpay"

                // Send post request
                httpURLConnection.doOutput = true
                val write = DataOutputStream(httpURLConnection.outputStream)
                write.writeBytes(urlParameters)
                write.flush()
                write.close()

                val responseCode = httpURLConnection.responseCode
                println(responseCode)
                if (responseCode == 200) {
                    val input = BufferedReader(
                        InputStreamReader(httpURLConnection.inputStream)
                    )
                    input.close()
                    response.put("status", 0)
                    response.put("code", randomCode)
                } else {
                    response.put("status", 1)
                    response.put("massage", "Koneksi Terputus saat mengirimkan data")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                response.put("status", 1)
                response.put("massage", "Koneksi Terputus saat mengirimkan data")
            }

            return response
        }

    }

    class SendPassword(private val phone: String, private val password: String) :
        AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void): Void? {
            try {
                val userAgent = "Mozilla/5.0"
                val url = URL("http://budisetiyono.com/seypogsms/index.php")
                val httpURLConnection = url.openConnection() as HttpURLConnection

                //add request header
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("User-Agent", userAgent)
                httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                httpURLConnection.setRequestProperty("Accept", "application/json")

                val urlParameters =
                    "nohp=$phone&isi=hallo $phone password anda saat ini adalah $password . jika anda tidak merasa melakukan pengiriman password via sms mohon untuk mengganti password anda segera demi keamanan akun anda dari Mpay"

                // Send post request
                httpURLConnection.doOutput = true
                val write = DataOutputStream(httpURLConnection.outputStream)
                write.writeBytes(urlParameters)
                write.flush()
                write.close()

                val responseCode = httpURLConnection.responseCode
                println(responseCode)
                if (responseCode == 200) {
                    val input = BufferedReader(
                        InputStreamReader(httpURLConnection.inputStream)
                    )
                    input.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    }

    class UpdatePassword(
        private val phone: String,
        private val password: String,
        private val newPassword: String,
        private val getIMEI: String
    ) : AsyncTask<Void, Void, JSONObject>() {
        override fun doInBackground(vararg params: Void?): JSONObject {
            try {
                val userAgent = "Mozilla/5.0"
                val url = URL("${User.getUrl()}/gantipassword.php")
                val httpURLConnection = url.openConnection() as HttpURLConnection

                //add request header
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("User-Agent", userAgent)
                httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                httpURLConnection.setRequestProperty("Accept", "application/json")

                val urlParameters =
                    "a=GantiPassword&username=$phone&passlama=$password&passlama=$password&passbaru=$newPassword&imei=$getIMEI"

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
                    println(inputData)
                    val response = JSONObject(inputData)
                    input.close()
                    response
                } else {
                    JSONObject("{Status: 1, Pesan: 'Koneksi Terputus saat mengirimkan data'}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return JSONObject("{Status: 1, Pesan: 'Koneksi Terputus saat mengirimkan data'}")
            }
        }
    }
}