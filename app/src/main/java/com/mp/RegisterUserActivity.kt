package com.mp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
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

class RegisterUserActivity : AppCompatActivity() {

    private var imageSwitcher : Int = 0
    private var imageKTP : Uri? = null
    private var filePathKTP : String = ""
    private var fileNameKTP : String = ""
    private var imageSelfAndKTP : Uri? = null
    private var filePathSelfAndKTP : String = ""
    private var fileNameSelfAndKTP : String = ""
    private var code = ""

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        val loading = ProgressDialog(this)
        loading.setTitle("Loading")
        loading.setMessage("Wait while loading...")
        loading.setCancelable(false)

        KTP.setOnClickListener {
            imageSwitcher = 0
            val values  = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "KTP")
            values.put(MediaStore.Images.Media.DESCRIPTION, "Foto KTP")
            imageKTP = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageKTP)
            startActivityForResult(callCameraIntent,0)
        }

        selfAndKTP.setOnClickListener {
            imageSwitcher = 1
            val values  = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "Self And KTP")
            values.put(MediaStore.Images.Media.DESCRIPTION, "Foto Diri dan KTP")
            imageSelfAndKTP = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageSelfAndKTP)
            startActivityForResult(callCameraIntent,0)
        }

        sendCode.setOnClickListener {
            loading.show()
            if (phone.text.isNotEmpty()) {
                val response = RegisterController.VerifiedPhone(phone.text.toString()).execute().get()
                if (response["Status"].toString() == "0") {
                    code = response["code_key"].toString()
                } else {
                    Toast.makeText(this, "Error : ${response["Pesan"]}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Nomor Tlefon tidak boleh kosong", Toast.LENGTH_LONG).show()
            }
            loading.dismiss()
        }

        registerButton.setOnClickListener {
            loading.show()
            if (code == codeValidation.text.toString() && code.isNotEmpty()) {
                if (filePathKTP.isEmpty()) {
                    Toast.makeText(this, "Foto KTP tidak boleh kosong", Toast.LENGTH_LONG).show()
                } else if (filePathSelfAndKTP.isEmpty()) {
                    Toast.makeText(this, "Foto Diri dengan KTP tidak boleh kosong", Toast.LENGTH_LONG).show()
                } else if (phone.text.isEmpty()) {
                    Toast.makeText(this, "nomor telfon tidak boleh kosong", Toast.LENGTH_LONG).show()
                } else if (email.text.isEmpty()) {
                    Toast.makeText(this, "email tidak boleh kosong", Toast.LENGTH_LONG).show()
                } else if (name.text.isEmpty()) {
                    Toast.makeText(this, "nama tidak boleh kosong", Toast.LENGTH_LONG).show()
                } else if (password.text.isEmpty()) {
                    Toast.makeText(this, "kata sandi tidak boleh kosong", Toast.LENGTH_LONG).show()
                } else if (password.text.length < 6) {
                    Toast.makeText(this, "kata sandi kurang dari 6", Toast.LENGTH_LONG).show()
                } else if (password.text.length > 6) {
                    Toast.makeText(this, "kata sandi tidak boleh lebih dari 6", Toast.LENGTH_LONG).show()
                } else if (passwordValidation.text.isEmpty() && passwordValidation.text.toString() != password.text.toString()) {
                    Toast.makeText(this, "kata sandi yang anda inputkan tidak cocok", Toast.LENGTH_LONG).show()
                } else {
                    val statusKTP = uploadImageToServer(filePathKTP)
                    if (statusKTP) {
                        val statusSelfAndKTP = uploadImageToServer(filePathSelfAndKTP)
                        if (statusSelfAndKTP) {
                            val response = RegisterController.RegisterUser(
                                phone.text.toString(),
                                email.text.toString(),
                                name.text.toString(),
                                password.text.toString(),
                                fileNameKTP,
                                fileNameSelfAndKTP
                            ).execute().get()
                            if (response["Status"].toString() == "0") {
                                val session = Session(this)
                                session.saveString("phoneUser", phone.text.toString())
                                session.saveString("nameUser", name.text.toString())
                                session.saveString("pinUser", password.text.toString())
                                session.saveInteger("typeUser", 1)
                                val goTo = Intent(this, MainActivity::class.java)
                                startActivity(goTo)
                                finish()
                            } else {
                                Toast.makeText(this, "Pendaftaran Tidak Valid mohon cek data yang anda kirim", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this, "Foto Diri dengan KTP bermasalah mohon ulangi lagi", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Foto KTP bermasalah tolong ulangi lagi", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "code yang anda masukan tidak cocok", Toast.LENGTH_LONG).show()
            }
            loading.dismiss()
        }
    }

    private fun uploadImageToServer(getFile:String) : Boolean {
        return try {
            val response = MultipartUploadRequest(this, "http://picotele.com/neomitra/javacoin/mpay.php")
                .addFileToUpload(getFile, "file")
                //.addParameter("parameter", "content parameter")
                .setMaxRetries(2)
                .startUpload()
            true
        }catch (ex : Exception) {
            ex.printStackTrace()
            false
        }
    }

    private fun getRealPathFromImageURI(contentUri : Uri?) : String {
        val data : Array<String> = Array(100){MediaStore.Images.Media.DATA}
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
            camera = findViewById(R.id.KTP)
            if (resultCode == Activity.RESULT_OK) {
                try {
                    filePathKTP = getRealPathFromImageURI(imageKTP)
                    val convertArray = filePathKTP.split("/").toTypedArray()
                    fileNameKTP = convertArray.last()
                    val thumbnails = MediaStore.Images.Media.getBitmap(contentResolver, imageKTP)
                    camera.setImageBitmap(thumbnails)
                } catch (ex : Exception) {
                    Toast.makeText(this, "Ada Kesalah saat mengambil gambar", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Anda belum mengisi gambar dokumentasi", Toast.LENGTH_LONG).show()
            }
        } else {
            camera = findViewById(R.id.selfAndKTP)
            if (resultCode == Activity.RESULT_OK) {
                try {
                    filePathSelfAndKTP = getRealPathFromImageURI(imageSelfAndKTP)
                    val convertArray = filePathSelfAndKTP.split("/").toTypedArray()
                    fileNameSelfAndKTP = convertArray.last()
                    val thumbnails = MediaStore.Images.Media.getBitmap(contentResolver, imageSelfAndKTP)
                    camera.setImageBitmap(thumbnails)
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
