package com.mp.user.finance

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.mp.R
import kotlinx.android.synthetic.main.activity_detail_finance.*
import java.lang.Exception

class DetailFinanceActivity : AppCompatActivity(), PrintingCallback {
    private var printing: Printing? = null
    private var type: Int = 0

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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_finance)
        type = intent.getIntExtra("type", 0)
        val title = intent.getStringExtra("title")
        val indexArray = intent.getStringArrayListExtra("indexArray")
        val valueArray = intent.getStringArrayListExtra("valueArray")
        val container: LinearLayout = findViewById(R.id.body)
        val titleTextView: TextView = findViewById(R.id.title)

        titleTextView.text = title

        container.removeAllViews()

        Printooth.init(this)

        val optionContent = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        optionContent.topMargin = 10
        optionContent.bottomMargin = 10

        val optionValue = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )

        for (i in 0 until indexArray.size) {
            val linearLayout = LinearLayout(this)
            linearLayout.layoutParams = optionContent
            linearLayout.orientation = LinearLayout.HORIZONTAL

            val textIndex = TextView(this)
            textIndex.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
            textIndex.layoutParams = optionValue
            textIndex.text = indexArray[i]
            linearLayout.addView(textIndex)

            val text = TextView(this)
            text.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
            text.layoutParams = optionValue
            text.text = valueArray[i]
            linearLayout.addView(text)

            container.addView(linearLayout)
        }

        if (printing != null) {
            printing!!.printingCallback = this
        }

        printButton.setOnClickListener {
            if (Printooth.hasPairedPrinter()) {
                //Printooth.removeCurrentPrinter()
                when (type) {
                    0 -> printPulsa(
                        valueArray[0],
                        valueArray[1],
                        valueArray[2],
                        valueArray[3],
                        valueArray[4]
                    )
                    1 -> printPLN(
                        valueArray[0],
                        valueArray[1],
                        valueArray[2],
                        valueArray[3],
                        valueArray[4],
                        valueArray[5],
                        valueArray[6],
                        valueArray[7],
                        valueArray[8]
                    )
                    else -> printPayment(
                        valueArray[0],
                        valueArray[1],
                        valueArray[2],
                        valueArray[3],
                        valueArray[4],
                        valueArray[5],
                        valueArray[6]
                    )
                }
            } else {
                startActivityForResult(
                    Intent(this, ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            initPairing()
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

    private fun printPulsa(date: String, type: String, phone: String, sn: String, price: String) {
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
                    .setText("Mpay")
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
        } catch (e: Exception) {
            println(e)
            Toast.makeText(
                this,
                "Proses print gagal di lakukan tolong ulangi lagi",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun printPLN(
        date: String,
        type: String,
        numberPLN: String,
        namePLN: String,
        typePLN: String,
        volt: String,
        countPLN: String,
        token: String,
        price: String
    ) {
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
                    .setText("Mpay")
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
        } catch (e: Exception) {
            println(e)
            Toast.makeText(
                this,
                "Proses print gagal di lakukan tolong ulangi lagi",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun printPayment(
        date: String,
        type: String,
        numberPayment: String,
        namePayment: String,
        bill: String,
        admin: String,
        totalBill: String
    ) {
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
                    .setText("Mpay")
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
        } catch (e: Exception) {
            println(e)
            Toast.makeText(
                this,
                "Proses print gagal di lakukan tolong ulangi lagi",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}
