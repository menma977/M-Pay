@file:Suppress("DEPRECATION")

package com.mp.user

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import com.mp.R
import com.mp.controller.RegisterController
import com.mp.model.Session
import com.mp.user.member.HomeMemberActivity
import com.mp.user.merchant.HomeMerchantActivity
import kotlinx.android.synthetic.main.activity_upload_image.*
import net.gotev.uploadservice.MultipartUploadRequest
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class UploadImageActivity : AppCompatActivity() {

    private var imageSwitcher : Int = 0
    private var imageKTP : Uri? = null
    private var filePathKTP : String = ""
    private var fileNameKTP : String = ""
    private var imageSelfAndKTP : Uri? = null
    private var filePathSelfAndKTP : String = ""
    private var fileNameSelfAndKTP : String = ""
    private var session : Session? = null

    override fun onBackPressed() {
        super.onBackPressed()
        val session = Session(this)
        if (session.getInteger("type") == 1) {
            val goTo = Intent(this, HomeMemberActivity::class.java)
            startActivity(goTo)
            finish()
        } else {
            val goTo = Intent(this, HomeMerchantActivity::class.java)
            startActivity(goTo)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        session = Session(this)

        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)

        ktp.setOnClickListener {
            try {
                imageSwitcher = 0
                val values  = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "KTP")
                values.put(MediaStore.Images.Media.DESCRIPTION, "Foto KTP")
                imageKTP = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageKTP)
                startActivityForResult(callCameraIntent,0)
            } catch (e : Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Terjadi Kesalahan saatmembuka kemera coba ulangi lagi", Toast.LENGTH_LONG).show()
            }
        }

        ktpAndUser.setOnClickListener {
            try {
                imageSwitcher = 1
                val values  = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "Self And KTP")
                values.put(MediaStore.Images.Media.DESCRIPTION, "Foto Diri dan KTP")
                imageSelfAndKTP = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageSelfAndKTP)
                startActivityForResult(callCameraIntent,0)
            } catch (e : Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Terjadi Kesalahan saatmembuka kemera coba ulangi lagi", Toast.LENGTH_LONG).show()
            }
        }

        uploadImage.setOnClickListener {
            loading.show()
            when {
                fileNameKTP.isEmpty() -> {
                    Toast.makeText(this, "Anda Belum Memfoto KTP", Toast.LENGTH_LONG).show()
                    loading.dismiss()
                }
                fileNameSelfAndKTP.isEmpty() -> {
                    Toast.makeText(this, "Anda Belum Memfoto KTP dan Diri", Toast.LENGTH_LONG).show()
                    loading.dismiss()
                }
                else -> try {
                    Timer().schedule(5000) {
                        val statusKTP = uploadImageToServer(filePathKTP)
                        if (statusKTP) {
                            runOnUiThread {
                                try {
                                    Timer().schedule(5000) {
                                        val statusSelfAndKIP = uploadImageToServer(filePathSelfAndKTP)
                                        if (statusSelfAndKIP) {
                                            runOnUiThread {
                                                Timer().schedule(5000) {
                                                    try {
                                                        val session = Session(applicationContext)
                                                        val response = RegisterController.ReUploadImage(session.getString("phone").toString(), fileNameKTP, fileNameSelfAndKTP).execute().get()
                                                        if (response["Status"].toString() == "0") {
                                                            runOnUiThread {
                                                                loading.dismiss()
                                                                if (session.getInteger("type") == 1) {
                                                                    val goTo = Intent(applicationContext, HomeMemberActivity::class.java)
                                                                    startActivity(goTo)
                                                                    finish()
                                                                } else {
                                                                    val goTo = Intent(applicationContext, HomeMerchantActivity::class.java)
                                                                    startActivity(goTo)
                                                                    finish()
                                                                }
                                                            }
                                                        } else {
                                                            runOnUiThread {
                                                                Toast.makeText(applicationContext, response["Pesan"].toString(), Toast.LENGTH_LONG).show()
                                                                loading.dismiss()
                                                            }
                                                        }
                                                    } catch (e : Exception) {
                                                        runOnUiThread {
                                                            Toast.makeText(applicationContext, "Update data gagal tolong ulangi lagi", Toast.LENGTH_LONG).show()
                                                            loading.dismiss()
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            runOnUiThread {
                                                Toast.makeText(applicationContext, "Upload Foto Diri dan KTP gagal tolong ulangi lagi", Toast.LENGTH_LONG).show()
                                                loading.dismiss()
                                            }
                                        }
                                    }
                                } catch (e : Exception) {
                                    Toast.makeText(applicationContext, "Upload Foto Diri dan KTP ada masalah tolong ulangi lagi", Toast.LENGTH_LONG).show()
                                    loading.dismiss()
                                }
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(applicationContext, "Upload KTP gagal tolong ulangi lagi", Toast.LENGTH_LONG).show()
                                loading.dismiss()
                            }
                        }
                    }
                } catch (e : Exception) {
                    Toast.makeText(this, "Upload KTP ada masalah tolong ulangi lagi", Toast.LENGTH_LONG).show()
                    loading.dismiss()
                }
            }
        }
    }

    private fun uploadImageToServer(getFile:String) : Boolean {
        return try {
            MultipartUploadRequest(this, "http://picotele.com/neomitra/javacoin/mpay.php")
                .addFileToUpload(getFile, "file")
                //.addParameter("parameter", "content parameter")
                .setMaxRetries(0)
                .setAutoDeleteFilesAfterSuccessfulUpload(true)
                .startUpload()
            true
        }catch (ex : Exception) {
            ex.printStackTrace()
            false
        }
    }

    private fun getRealPathFromImageURI(contentUri : Uri?) : String {
        val data : Array<String> = Array(100){ MediaStore.Images.Media.DATA}
        val cursor = managedQuery(contentUri, data, null, null, null)
        val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val camera : ImageView
        if (imageSwitcher == 0) {
            camera = findViewById(R.id.ktp)
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            filePathKTP = getRealPathFromImageURI(imageKTP)
                            val convertArray = filePathKTP.split("/").toTypedArray()
                            fileNameKTP = convertArray.last()
                            val thumbnails = MediaStore.Images.Media.getBitmap(contentResolver, imageKTP)
                            val bitmap = Bitmap.createScaledBitmap(thumbnails, 150, 150, true)
                            runOnUiThread{
                                Handler().postDelayed({
                                    camera.setImageBitmap(bitmap)
                                }, 1000)
                            }
                        }
                    }, 1000)
                } catch (ex : Exception) {
                    Toast.makeText(this, "Ada Kesalah saat mengambil gambar", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Anda belum mengisi gambar dokumentasi", Toast.LENGTH_LONG).show()
            }
        } else {
            camera = findViewById(R.id.ktpAndUser)
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            filePathSelfAndKTP = getRealPathFromImageURI(imageSelfAndKTP)
                            val convertArray = filePathSelfAndKTP.split("/").toTypedArray()
                            fileNameSelfAndKTP = convertArray.last()
                            val thumbnails = MediaStore.Images.Media.getBitmap(contentResolver, imageSelfAndKTP)
                            val bitmap = Bitmap.createScaledBitmap(thumbnails, 150, 150, true)
                            runOnUiThread{
                                Handler().postDelayed({
                                    camera.setImageBitmap(bitmap)
                                }, 1000)
                            }
                        }
                    }, 1000)
                } catch (ex : Exception) {
                    ex.printStackTrace()
                    Toast.makeText(this, "Ada Kesalah saat mengambil gambar", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Anda belum mengisi gambar dokumentasi", Toast.LENGTH_LONG).show()
            }
        }
    }
}
