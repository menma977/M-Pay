package com.mp.user.finance

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.widget.*
import androidx.core.content.ContextCompat
import com.mp.R
import com.mp.controller.FinanceController
import com.mp.model.Session
import org.json.JSONArray

class FinanceManagementActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_finance_management)
        val session = Session(this)
        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()

        val response =
            FinanceController.GetAll(session.getString("phone").toString()).execute().get()

        val noteTableLayout = findViewById<TableLayout>(R.id.NoteTableLayout)
        noteTableLayout.removeAllViews()

        val optionRow = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT,
            0f
        )
        optionRow.topMargin = 20
        optionRow.bottomMargin = 20

        val optionValue = TableRow.LayoutParams(
            0,
            TableRow.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        optionValue.topMargin = 20
        optionValue.bottomMargin = 20
        optionValue.marginStart = 10
        optionValue.marginEnd = 20

        val button = TableRow.LayoutParams(
            150,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        optionValue.topMargin = 20
        optionValue.bottomMargin = 20
        optionValue.marginStart = 10
        optionValue.marginEnd = 20

        val line = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            3,
            1.0f
        )

        val convertToJsonArray = JSONArray(response["trx"].toString())

        for (value in 0 until convertToJsonArray.length()) {
            if (convertToJsonArray.getJSONObject(value)["type"].toString() != "Default") {
                val tableRow = TableRow(this)
                tableRow.gravity = Gravity.VERTICAL_GRAVITY_MASK
                tableRow.layoutParams = optionRow

                val codeTRX = TextView(this)
                codeTRX.text = convertToJsonArray.getJSONObject(value)["ket"].toString()
                codeTRX.layoutParams = optionValue
                tableRow.addView(codeTRX)

                if (convertToJsonArray.getJSONObject(value)["type"].toString() == "TOKEN") {
                    val colorValue = ContextCompat.getColor(this, R.color.textPrimary)
                    val printButton = Button(this)
                    printButton.text = "Detail"
                    printButton.setTextColor(colorValue)
                    printButton.layoutParams = button
                    printButton.setBackgroundResource(R.drawable.button_info)
                    printButton.setOnClickListener {
                        val getDataToPrint = FinanceController.GetDetail(
                            session.getString("phone").toString(),
                            convertToJsonArray.getJSONObject(value)["idtrx"].toString()
                        ).execute()
                        val responsePLN = getDataToPrint.get().getJSONArray("trx")
                        println(responsePLN.getJSONObject(0)["ket"].toString().split(">>"))
                        val date = responsePLN.getJSONObject(0)["tgl"].toString()
                        val type = responsePLN.getJSONObject(0)["ket"].toString().split(">>")[0]
                        val numberPLN =
                            responsePLN.getJSONObject(0)["ket"].toString().split(">>")[1]
                        val namePLN =
                            responsePLN.getJSONObject(0)["ket"].toString().split(">>")[2].split(
                                "/"
                            )[1]
                        val typePLN =
                            responsePLN.getJSONObject(0)["ket"].toString().split(">>")[2].split(
                                "/"
                            )[2]
                        val volt =
                            responsePLN.getJSONObject(0)["ket"].toString().split(">>")[2].split(
                                "/"
                            )[3]
                        val countPLN =
                            responsePLN.getJSONObject(0)["ket"].toString().split(">>")[2].split(
                                "/"
                            )[4]
                        val token =
                            responsePLN.getJSONObject(0)["ket"].toString().split(">>")[2].split(
                                "/"
                            )[0]
                        val price = (
                                responsePLN.getJSONObject(0)["markup"].toString().toInt() + responsePLN.getJSONObject(
                                    0
                                )["harga"].toString().toInt()
                                ).toString()

                        val indexArray = ArrayList<String>()
                        indexArray.add("Tanggal")
                        indexArray.add("Type")
                        indexArray.add("NNomor Pelanggan")
                        indexArray.add("Nomor Nama")
                        indexArray.add("Type")
                        indexArray.add("Voltase")
                        indexArray.add("Jumlah Token")
                        indexArray.add("Jumlah Token")
                        indexArray.add("Harga")

                        val valueArray = ArrayList<String>()
                        valueArray.add(date)
                        valueArray.add(type)
                        valueArray.add(numberPLN)
                        valueArray.add(namePLN)
                        valueArray.add(typePLN)
                        valueArray.add(volt)
                        valueArray.add(countPLN)
                        valueArray.add(token)
                        valueArray.add(price)

                        val goTo = Intent(
                            this,
                            DetailFinanceActivity::class.java
                        ).putExtra("indexArray", indexArray).putExtra("valueArray", valueArray)
                            .putExtra("title", "Token Listrik").putExtra("type", 1)
                        startActivity(goTo)
                    }
                    tableRow.addView(printButton)
                }

                if (convertToJsonArray.getJSONObject(value)["type"].toString() == "PULSA") {
                    val colorValue = ContextCompat.getColor(this, R.color.textPrimary)
                    val printButton = Button(this)
                    printButton.text = "Detail"
                    printButton.setTextColor(colorValue)
                    printButton.minWidth = 400
                    printButton.layoutParams = button
                    printButton.setBackgroundResource(R.drawable.button_info)
                    printButton.setOnClickListener {
                        val getDataToPrint = FinanceController.GetDetail(
                            session.getString("phone").toString(),
                            convertToJsonArray.getJSONObject(value)["idtrx"].toString()
                        ).execute()
                        val responseP =
                            getDataToPrint.get().getJSONArray("trx").getJSONObject(0)
                        val date = responseP["tgl"].toString()
                        val type = responseP["ket"].toString().split(">>")[0]
                        val phone = responseP["ket"].toString().split(">>")[3]
                        val sn = responseP["ket"].toString().split(">>")[1]
                        val price =
                            (responseP["markup"].toString().toInt() + responseP["harga"].toString().toInt()).toString()

                        val indexArray = ArrayList<String>()
                        indexArray.add("Tanggal")
                        indexArray.add("Type")
                        indexArray.add("Nomor HP")
                        indexArray.add("Nomor S/N")
                        indexArray.add("Harga")

                        val valueArray = ArrayList<String>()
                        valueArray.add(date)
                        valueArray.add(type)
                        valueArray.add(phone)
                        valueArray.add(sn)
                        valueArray.add(price)
                        val goTo = Intent(
                            this,
                            DetailFinanceActivity::class.java
                        ).putExtra("indexArray", indexArray).putExtra("valueArray", valueArray)
                            .putExtra("title", "Pulsa Dan Top Up").putExtra("type", 0)
                        startActivity(goTo)
                    }
                    tableRow.addView(printButton)
                }

                if (convertToJsonArray.getJSONObject(value)["type"].toString() == "PEMBAYARAN") {
                    val colorValue = ContextCompat.getColor(this, R.color.textPrimary)
                    val printButton = Button(this)
                    printButton.text = "Detail"
                    printButton.setTextColor(colorValue)
                    printButton.minWidth = 400
                    printButton.layoutParams = button
                    printButton.setBackgroundResource(R.drawable.button_info)
                    printButton.setOnClickListener {
                        val getDataToPrint = FinanceController.GetDetail(
                            session.getString("phone").toString(),
                            convertToJsonArray.getJSONObject(value)["idtrx"].toString()
                        ).execute()
                        val responsePayment = getDataToPrint.get().getJSONArray("trx")
                        println(responsePayment)
                        val date = responsePayment.getJSONObject(0)["tgl"].toString()
                        val type =
                            responsePayment.getJSONObject(0)["sn"].toString().split("|")[0]
                        val numberPayment =
                            responsePayment.getJSONObject(0)["sn"].toString().split("|")[1]
                        val namePayment =
                            responsePayment.getJSONObject(0)["sn"].toString().split("|")[2]
                        val bill =
                            responsePayment.getJSONObject(0)["sn"].toString().split("|")[3]
                        val admin =
                            responsePayment.getJSONObject(0)["sn"].toString().split("|")[4]
                        val totalBill = (
                                responsePayment.getJSONObject(0)["markup"].toString().toInt() + responsePayment.getJSONObject(
                                    0
                                )["harga"].toString().toInt()
                                ).toString()

                        val indexArray = ArrayList<String>()
                        indexArray.add("Tanggal")
                        indexArray.add("Type")
                        indexArray.add("Nomor Pelanggan")
                        indexArray.add("Nama Pelanggan")
                        indexArray.add("Tagihan")
                        indexArray.add("Admin")
                        indexArray.add("Total Bayar")

                        val valueArray = ArrayList<String>()
                        valueArray.add(date)
                        valueArray.add(type)
                        valueArray.add(numberPayment)
                        valueArray.add(namePayment)
                        valueArray.add(bill)
                        valueArray.add(admin)
                        valueArray.add(totalBill)
                        val goTo = Intent(
                            this,
                            DetailFinanceActivity::class.java
                        ).putExtra("indexArray", indexArray).putExtra("valueArray", valueArray)
                            .putExtra("title", "Pembayaran").putExtra("type", 3)
                        startActivity(goTo)
                    }
                    tableRow.addView(printButton)
                }

                val colorValue = ContextCompat.getColor(this, R.color.colorPrimaryDark)
                val textLine = TextView(this)
                textLine.setBackgroundColor(colorValue)
                textLine.layoutParams = line

                noteTableLayout.addView(tableRow)
                noteTableLayout.addView(textLine)
            }
        }

        Handler().postDelayed({
            loading.dismiss()
        }, 1000)
    }
}
