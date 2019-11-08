@file:Suppress("DEPRECATION")

package com.mp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import com.mp.controller.RegisterController
import com.mp.model.Session
import kotlinx.android.synthetic.main.activity_register_user.*
import net.gotev.uploadservice.MultipartUploadRequest
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule
import android.os.Handler
import androidx.core.text.isDigitsOnly
import com.mp.model.User


class RegisterUserActivity : AppCompatActivity() {

    private var imageSwitcher: Int = 0
    private var imageKTP: Uri? = null
    private var filePathKTP: String = ""
    private var fileNameKTP: String = ""
    private var imageSelfAndKTP: Uri? = null
    private var filePathSelfAndKTP: String = ""
    private var fileNameSelfAndKTP: String = ""
    private var code = ""
    private var session: Session? = null

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        session = Session(this)

        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)

        KTP.setOnClickListener {
            try {
                imageSwitcher = 0
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "KTP")
                values.put(MediaStore.Images.Media.DESCRIPTION, "Foto KTP")
                imageKTP =
                    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageKTP)
                startActivityForResult(callCameraIntent, 0)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this,
                    "Terjadi Kesalahan saatmembuka kemera coba ulangi lagi",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        selfAndKTP.setOnClickListener {
            try {
                imageSwitcher = 1
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "Self And KTP")
                values.put(MediaStore.Images.Media.DESCRIPTION, "Foto Diri dan KTP")
                imageSelfAndKTP =
                    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageSelfAndKTP)
                startActivityForResult(callCameraIntent, 0)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this,
                    "Terjadi Kesalahan saatmembuka kemera coba ulangi lagi",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        sendCode.setOnClickListener {
            loading.show()
            if (phone.text.isNotEmpty()) {
                try {
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            val response =
                                RegisterController.VerifiedPhone(phone.text.toString()).execute()
                                    .get()
                            val massageResponse: String
                            if (response["Status"].toString() == "0") {
                                code = response["code_key"].toString()
                                massageResponse = response["Pesan"].toString()
                            } else {
                                massageResponse = response["Pesan"].toString()
                            }
                            runOnUiThread {
                                Handler().postDelayed({
                                    Toast.makeText(
                                        applicationContext,
                                        massageResponse,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }, 1500)
                            }
                        }
                    }, 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Ada masalah saat pengiriman kode mohon ulangi lagi",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(this, "Nomor Tlefon tidak boleh kosong", Toast.LENGTH_LONG).show()
            }
            Timer().schedule(1000) {
                loading.dismiss()
            }
        }

        registerButton.setOnClickListener {
            try {
                sendData()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this,
                    "Terjadi Kesalahan saat mengirim data coba ulangi lagi",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun sendData() {
        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)
        if (code == codeValidation.text.toString() && code.isNotEmpty()) {
            if (filePathKTP.isEmpty()) {
                Toast.makeText(this, "Foto KTP tidak boleh kosong", Toast.LENGTH_LONG).show()
            } else if (filePathSelfAndKTP.isEmpty()) {
                Toast.makeText(this, "Foto Diri dengan KTP tidak boleh kosong", Toast.LENGTH_LONG)
                    .show()
            } else if (phone.text.isEmpty() && !phone.text.isDigitsOnly()) {
                Toast.makeText(
                    this,
                    "nomor telfon tidak boleh kosong dan hanya boleh angka",
                    Toast.LENGTH_LONG
                ).show()
            } else if (email.text.isEmpty()) {
                Toast.makeText(this, "email tidak boleh kosong", Toast.LENGTH_LONG).show()
            } else if (name.text.isEmpty()) {
                Toast.makeText(this, "nama tidak boleh kosong", Toast.LENGTH_LONG).show()
            } else if (password.text.isEmpty()) {
                Toast.makeText(this, "kata sandi tidak boleh kosong", Toast.LENGTH_LONG).show()
            } else if (!password.text.isDigitsOnly()) {
                Toast.makeText(this, "kata sandi hanya boleh angka", Toast.LENGTH_LONG).show()
            } else if (password.text.length < 6) {
                Toast.makeText(this, "kata sandi kurang dari 6", Toast.LENGTH_LONG).show()
            } else if (password.text.length > 6) {
                Toast.makeText(this, "kata sandi tidak boleh lebih dari 6", Toast.LENGTH_LONG)
                    .show()
            } else if (passwordValidation.text.isEmpty() && passwordValidation.text.toString() != password.text.toString()) {
                Toast.makeText(this, "kata sandi yang anda inputkan tidak cocok", Toast.LENGTH_LONG)
                    .show()
            } else if (fileNameKTP.isEmpty()) {
                Toast.makeText(this, "Foto KTP tidak boleh kosong", Toast.LENGTH_LONG).show()
            } else if (fileNameSelfAndKTP.isEmpty()) {
                Toast.makeText(this, "Foto Diri dan KTP tidak boleh kosong", Toast.LENGTH_LONG)
                    .show()
            } else {
                loading.show()
                Timer().schedule(5000) {
                    val statusKTP = uploadImageToServer(filePathKTP)
                    if (statusKTP) {
                        val statusSelfAndKTP = uploadImageToServer(filePathSelfAndKTP)
                        Timer().schedule(5000) {
                            if (statusSelfAndKTP) {
                                Timer().schedule(5000) {
                                    val response = RegisterController.RegisterUser(
                                        phone.text.toString(),
                                        email.text.toString(),
                                        name.text.toString(),
                                        password.text.toString(),
                                        fileNameKTP,
                                        fileNameSelfAndKTP
                                    ).execute().get()
                                    if (response["Status"].toString() == "0") {
                                        session!!.saveString("phone", response["nohp"].toString())
                                        session!!.saveString("email", response["email"].toString())
                                        session!!.saveString("name", response["nama"].toString())
                                        session!!.saveString("pin", response["password"].toString())
                                        session!!.saveInteger("status", 0)
                                        session!!.saveInteger("type", 1)

                                        User.setPhone(response["nohp"].toString())
                                        User.setEmail(response["email"].toString())
                                        User.setName(response["nama"].toString())
                                        User.setPin(response["password"].toString())
                                        User.setType(1)
                                        User.setStatus(0)
                                        runOnUiThread {
                                            loading.dismiss()
                                            Handler().postDelayed({
                                                val goTo = Intent(
                                                    applicationContext,
                                                    MainActivity::class.java
                                                )
                                                startActivity(goTo)
                                                finish()
                                            }, 2000)
                                        }
                                    } else {
                                        runOnUiThread {
                                            Handler().postDelayed({
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Pendaftaran Tidak Valid mohon cek data yang anda kirim",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                loading.dismiss()
                                            }, 500)
                                        }
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    Handler().postDelayed({
                                        Toast.makeText(
                                            applicationContext,
                                            "Foto Diri dengan KTP bermasalah mohon ulangi lagi",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        loading.dismiss()
                                    }, 500)
                                }
                            }
                        }
                    } else {
                        runOnUiThread {
                            Handler().postDelayed({
                                Toast.makeText(
                                    applicationContext,
                                    "Foto KTP bermasalah tolong ulangi lagi",
                                    Toast.LENGTH_LONG
                                ).show()
                                loading.dismiss()
                            }, 500)
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, "code yang anda masukan tidak cocok", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadImageToServer(getFile: String): Boolean {
        return try {
            val image =
                MultipartUploadRequest(this, "http://picotele.com/neomitra/javacoin/mpay.php")
                    .addFileToUpload(getFile, "file")
                    //.addParameter("parameter", "content parameter")
                    .setMaxRetries(0)
                    .setAutoDeleteFilesAfterSuccessfulUpload(true)
                    .startUpload()
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    private fun getRealPathFromImageURI(contentUri: Uri?): String {
        val data: Array<String> = Array(100) { MediaStore.Images.Media.DATA }
        val cursor = managedQuery(contentUri, data, null, null, null)
        val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val camera: ImageView
        if (imageSwitcher == 0) {
            camera = findViewById(R.id.KTP)
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            filePathKTP = getRealPathFromImageURI(imageKTP)
                            val convertArray = filePathKTP.split("/").toTypedArray()
                            fileNameKTP = convertArray.last()
                            val thumbnails =
                                MediaStore.Images.Media.getBitmap(contentResolver, imageKTP)
                            val bitmap = Bitmap.createScaledBitmap(thumbnails, 150, 150, true)
                            runOnUiThread {
                                Handler().postDelayed({
                                    camera.setImageBitmap(bitmap)
                                }, 1000)
                            }
                        }
                    }, 1000)
                } catch (ex: Exception) {
                    Toast.makeText(this, "Ada Kesalah saat mengambil gambar", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(this, "Anda belum mengisi gambar dokumentasi", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            camera = findViewById(R.id.selfAndKTP)
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            filePathSelfAndKTP = getRealPathFromImageURI(imageSelfAndKTP)
                            val convertArray = filePathSelfAndKTP.split("/").toTypedArray()
                            fileNameSelfAndKTP = convertArray.last()
                            val thumbnails =
                                MediaStore.Images.Media.getBitmap(contentResolver, imageSelfAndKTP)
                            val bitmap = Bitmap.createScaledBitmap(thumbnails, 150, 150, true)
                            runOnUiThread {
                                Handler().postDelayed({
                                    camera.setImageBitmap(bitmap)
                                }, 1000)
                            }
                        }
                    }, 1000)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Toast.makeText(this, "Ada Kesalah saat mengambil gambar", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(this, "Anda belum mengisi gambar dokumentasi", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}
