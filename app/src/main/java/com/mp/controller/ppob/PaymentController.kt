package com.mp.controller.ppob

import android.os.AsyncTask
import com.mp.model.User
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class PaymentController {
    class Request(
        private val username: String,
        private val customerID: String,
        private val phone: String,
        private val firstBalance: String,
        private val type: String
    ) : AsyncTask<Void, Void, JSONObject>() {
        override fun doInBackground(vararg params: Void?): JSONObject {
            try {
                val userAgent = "Mozilla/5.0"
                val url = URL("${User.getUrl()}/payment.php")
                val httpURLConnection = url.openConnection() as HttpsURLConnection

                //add request header
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("User-Agent", userAgent)
                httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                httpURLConnection.setRequestProperty("Accept", "application/json")

                val urlParameters =
                    "a=ReqPayment&username=${username}&idpel=${customerID}&nohp=$phone&saldoawal=$firstBalance&type=$type"+
                            "&uzer=2d21dbba2eded322b504c811170190d6"+
                            "&passw=fd1d59e3076e99f13f29a783ac79aecf"
                println(urlParameters)

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

    class Response(
        private val username: String,
        private val type: String,
        private val clientID: String,
        private val clientName: String,
        private val price: String,
        private val admin: String,
        private val markupAdmin: String,
        private val totalPrice: String,
        private val phoneNumber: String,
        private val remainingBalance: String,
        private val ref: String,
        private val periodic: String
    ) : AsyncTask<Void, Void, JSONObject>() {
        override fun doInBackground(vararg params: Void?): JSONObject {
            try {
                val userAgent = "Mozilla/5.0"
                val url = URL("${User.getUrl()}/payment.php")
                val httpURLConnection = url.openConnection() as HttpsURLConnection

                //add request header
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("User-Agent", userAgent)
                httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                httpURLConnection.setRequestProperty("Accept", "application/json")

                val urlParameters = "a=BayarPayment&username=$username" +
                        "&type=$type&idpel=$clientID&npelanggan=$clientName&jmltagih=$price" +
                        "&admin=$admin&totaltagih=$totalPrice&hppembeli=$phoneNumber&sisasaldo=$remainingBalance" +
                        "&markup=$markupAdmin&ref=$ref&periodetagih=$periodic"+
                        "&uzer=2d21dbba2eded322b504c811170190d6"+
                        "&passw=fd1d59e3076e99f13f29a783ac79aecf"

                println(urlParameters)

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
}