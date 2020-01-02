package com.mp.controller.ppob

import android.os.AsyncTask
import com.mp.model.User
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ProductController(private val phone : String) : AsyncTask<Void, Void, JSONArray>() {
    override fun doInBackground(vararg params: Void?): JSONArray {
        try {
            val userAgent = "Mozilla/5.0"
            val url = URL("${User.getUrl()}/produk.php")
            val httpURLConnection = url.openConnection() as HttpURLConnection

            //add request header
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.setRequestProperty("User-Agent", userAgent)
            httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
            httpURLConnection.setRequestProperty("Accept", "application/json")

            val urlParameters = "a=Produk&username=${phone}"+
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
                val jsonObject = JSONArray()
                var subJsonObject: JSONObject
                val arrayList = ArrayList<JSONObject>()
                var code : String
                var name : String
                var operator : String
                var typeProduct : String
                jsonObject.put(JSONObject("{Status : 1}"))

                for (value in 0 until response.length()) {
                    code = response.getJSONObject(value)["Kode"].toString()
                    name = response.getJSONObject(value)["Nama"].toString()
                    operator = response.getJSONObject(value)["Operator"].toString()
                    typeProduct = response.getJSONObject(value)["TypeProduk"].toString()
                    if (operator != "Default") {
                        subJsonObject = JSONObject()
                        subJsonObject.put("code", code)
                        subJsonObject.put("name", name)
                        subJsonObject.put("operator", operator)
                        subJsonObject.put("typeProduct", typeProduct)

                        arrayList.add(value, subJsonObject)
                    }
                }
                var index = 0
                arrayList.groupBy { it["operator"] }.entries.map { (name, group) ->
                    jsonObject.getJSONObject(0).put(name.toString(), JSONArray(group.toString()))
                    index++
                }
                input.close()
                jsonObject
            } else {
                val jsonArray = JSONArray()
                jsonArray.put(1)
            }
        }catch (e : Exception) {
            e.printStackTrace()
            val jsonArray = JSONArray()
            return jsonArray.put(1)
        }
    }

}