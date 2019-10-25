package com.mp.user.finance

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.widget.*
import androidx.core.content.ContextCompat
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.mp.R
import com.mp.controller.FinanceController
import com.mp.model.Session
import kotlinx.android.synthetic.main.activity_finance_management.*
import org.json.JSONArray
import java.lang.Exception

class FinanceManagementActivity : AppCompatActivity(), PrintingCallback {
    override fun connectingWithPrinter() {
        Toast.makeText(this, "Connecting to printer", Toast.LENGTH_LONG).show()
    }

    override fun connectionFailed(error: String) {
        Toast.makeText(this, "Failed : $error", Toast.LENGTH_LONG).show()
    }

    override fun onError(error: String) {
        Toast.makeText(this, "Failed : $error", Toast.LENGTH_LONG).show()
    }

    override fun onMessage(message: String) {
        Toast.makeText(this, "Failed : $message", Toast.LENGTH_LONG).show()
    }

    override fun printingOrderSentSuccessfully() {
        Toast.makeText(this, "Order sent to printer", Toast.LENGTH_LONG).show()
    }

    private var printing : Printing? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Printooth.init(this)

        setContentView(R.layout.activity_finance_management)
        val session = Session(this)
        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        loading.show()

        val  response = FinanceController.GetAll(session.getString("phone").toString()).execute().get()

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
                    printButton.text = "Cetak"
                    printButton.setTextColor(colorValue)
                    printButton.layoutParams = optionValue
                    printButton.setBackgroundResource(R.drawable.button_info)
                    printButton.setOnClickListener {
                        if (!Printooth.hasPairedPrinter()) {
                            startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
                        } else {
                            val getDataToPrint = FinanceController.getDital(session.getString("phone").toString(), convertToJsonArray.getJSONObject(value)["idtrx"].toString()).execute()
                            val responsePLN = getDataToPrint.get().getJSONArray("trx")
                            //println(responsePLN)
                            Printooth.getPairedPrinter()
                            println(responsePLN.getJSONObject(0)["ket"].toString().split(">>"))
                            val date = responsePLN.getJSONObject(0)["tgl"].toString()
                            val type = responsePLN.getJSONObject(0)["ket"].toString().split(">>")[0]
                            val numberPLN =responsePLN.getJSONObject(0)["ket"].toString().split(">>")[1]
                            val namePLN = responsePLN.getJSONObject(0)["ket"].toString().split(">>")[2].split("/")[1]
                            val typePLN = responsePLN.getJSONObject(0)["ket"].toString().split(">>")[2].split("/")[2]
                            val volt = responsePLN.getJSONObject(0)["ket"].toString().split(">>")[2].split("/")[3]
                            val countPLN = responsePLN.getJSONObject(0)["ket"].toString().split(">>")[2].split("/")[4]
                            val token = responsePLN.getJSONObject(0)["ket"].toString().split(">>")[2].split("/")[0]
                            val price = (
                                    responsePLN.getJSONObject(0)["markup"].toString().toInt() + responsePLN.getJSONObject(0)["harga"].toString().toInt()
                                    ).toString()
                            printPLN(date, type, numberPLN, namePLN, typePLN, volt, countPLN, token, price)
                        }
                    }
                    tableRow.addView(printButton)
                }

                if (convertToJsonArray.getJSONObject(value)["type"].toString() == "PULSA") {
                    val colorValue = ContextCompat.getColor(this, R.color.textPrimary)
                    val printButton = Button(this)
                    printButton.text = "Cetak"
                    printButton.setTextColor(colorValue)
                    printButton.minWidth = 400
                    printButton.layoutParams = optionValue
                    printButton.setBackgroundResource(R.drawable.button_info)
                    printButton.setOnClickListener {
                        if (!Printooth.hasPairedPrinter()) {
                            startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
                        } else {
                            val getDataToPrint = FinanceController.getDital(session.getString("phone").toString(), convertToJsonArray.getJSONObject(value)["idtrx"].toString()).execute()
                            val responsePulsa = getDataToPrint.get().getJSONArray("trx")
                            Printooth.getPairedPrinter()
                            val date = responsePulsa.getJSONObject(0)["tgl"].toString()
                            val type = responsePulsa.getJSONObject(0)["ket"].toString().split(">>")[0]
                            val phone = responsePulsa.getJSONObject(0)["ket"].toString().split(">>")[3]
                            val sn = responsePulsa.getJSONObject(0)["ket"].toString().split(">>")[1]
                            val price = (
                                    responsePulsa.getJSONObject(0)["markup"].toString().toInt() + responsePulsa.getJSONObject(0)["harga"].toString().toInt()
                                    ).toString()
                            printPulsa(date, type, phone, sn, price)
                        }
                    }
                    tableRow.addView(printButton)
                }

                if (convertToJsonArray.getJSONObject(value)["type"].toString() == "PEMBAYARAN") {
                    val colorValue = ContextCompat.getColor(this, R.color.textPrimary)
                    val printButton = Button(this)
                    printButton.text = "Cetak"
                    printButton.setTextColor(colorValue)
                    printButton.minWidth = 400
                    printButton.layoutParams = optionValue
                    printButton.setBackgroundResource(R.drawable.button_info)
                    printButton.setOnClickListener {
                        if (!Printooth.hasPairedPrinter()) {
                            startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
                        } else {
                            val getDataToPrint = FinanceController.getDital(session.getString("phone").toString(), convertToJsonArray.getJSONObject(value)["idtrx"].toString()).execute()
                            val responsePayment = getDataToPrint.get().getJSONArray("trx")
                            Printooth.getPairedPrinter()
                            println(responsePayment)
                            val date = responsePayment.getJSONObject(0)["tgl"].toString()
                            val type = responsePayment.getJSONObject(0)["sn"].toString().split("|")[0]
                            val numberPayment = responsePayment.getJSONObject(0)["sn"].toString().split("|")[1]
                            val namePayment = responsePayment.getJSONObject(0)["sn"].toString().split("|")[2]
                            val bill = responsePayment.getJSONObject(0)["sn"].toString().split("|")[3]
                            val admin = responsePayment.getJSONObject(0)["sn"].toString().split("|")[4]
                            val totalBill = (
                                    responsePayment.getJSONObject(0)["markup"].toString().toInt() + responsePayment.getJSONObject(0)["harga"].toString().toInt()
                                    ).toString()
                            printPayment(date, type, numberPayment, namePayment, bill, admin, totalBill)
                        }
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

        initView()

        Handler().postDelayed({
            loading.dismiss()
        }, 1000)
    }

    private fun initView() {
        changePairAndUnpaired()
        if (printing != null) {
            printing!!.printingCallback = this
        }

        SelectPrintButton.setOnClickListener {
            if (Printooth.hasPairedPrinter()) {
                Printooth.removeCurrentPrinter()
                changePairAndUnpaired()
            } else {
                changePairAndUnpaired()
                startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
                changePairAndUnpaired()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changePairAndUnpaired() {
        if (Printooth.hasPairedPrinter()) {
            PrinterNameTextView.text = "${Printooth.getPairedPrinter()!!.name}"
        } else {
            PrinterNameTextView.text = "Anda Belum Tersambung"
        }
    }

    private fun printPulsa(date : String, type : String, phone : String, sn : String, price : String) {
        try {
            printing = Printooth.printer()
            val printable = ArrayList<Printable>()
            printable.add(RawPrintable.Builder(byteArrayOf(27, 100, 1)).build())

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("================================")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("PADIPPOB.COM")
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("================================")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Struk Cetak Pulsa Dan Top Up")
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("================================")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Tanggal : $date")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Type : $type")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Nomor HP : $phone")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Nomor S/N : $sn")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Harga : $price")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Struk ini sebagai bukti pembayaran sah.")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(2)
                    .build()
            )

            printing!!.print(printable)
        } catch (e : Exception) {
            println(e)
            Toast.makeText(this, "Proses print gagal di lakukan tolong ulangi lagi", Toast.LENGTH_LONG).show()
        }
    }

    private fun printPLN(date : String, type : String, numberPLN : String, namePLN : String, typePLN : String, volt : String, countPLN : String, token : String, price : String) {
        try {
            printing = Printooth.printer()
            val printable = ArrayList<Printable>()
            printable.add(RawPrintable.Builder(byteArrayOf(27, 100, 1)).build())

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("================================")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("PADIPPOB.COM")
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("================================")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Struk Cetak Token Listrik")
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("================================")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Tanggal : $date")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Type : $type")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Nomor Pelanggan : $numberPLN")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Nama Pelanggan : $namePLN")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Type : $typePLN")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Voltase : $volt")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Jumlah Token : $countPLN")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Nomor Token : $token")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Harga : $price")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Struk ini sebagai bukti pembayaran sah.")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(3)
                    .build()
            )

            printing!!.print(printable)
        } catch (e : Exception) {
            println(e)
            Toast.makeText(this, "Proses print gagal di lakukan tolong ulangi lagi", Toast.LENGTH_LONG).show()
        }
    }

    private fun printPayment(date : String, type : String, numberPayment : String, namePayment : String, bill : String, admin : String, totalBill : String) {
        try {
            printing = Printooth.printer()
            val printable = ArrayList<Printable>()
            printable.add(RawPrintable.Builder(byteArrayOf(27, 100, 1)).build())

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("================================")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("PADIPPOB.COM")
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("================================")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Struk Cetak Pembayaran")
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("================================")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Tanggal : $date")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Type : $type")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Nomor Pelanggan : $numberPayment")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Nama Pelanggan : $namePayment")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Tagihan : $bill")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Admin : $admin")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Total Bayar : $totalBill")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(1)
                    .build()
            )

            printable.add(
                TextPrintable
                    .Builder()
                    .setText("Struk ini sebagai bukti pembayaran sah.")
                    .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                    .setNewLinesAfter(2)
                    .build()
            )

            printing!!.print(printable)
        } catch (e : Exception) {
            println(e)
            Toast.makeText(this, "Proses print gagal di lakukan tolong ulangi lagi", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            initPairing()
            changePairAndUnpaired()
        }
    }

    private fun initPairing() {
        if (Printooth.hasPairedPrinter()) {
            printing = Printooth.printer()
        }

        if (printing != null) {
            printing!!.printingCallback = this
        }
    }
}
