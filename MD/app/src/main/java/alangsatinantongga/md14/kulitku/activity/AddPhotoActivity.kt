package alangsatinantongga.md14.kulitku.activity

import alangsatinantongga.md14.kulitku.R
import alangsatinantongga.md14.kulitku.databinding.ActivityAddPhotoBinding
import alangsatinantongga.md14.kulitku.db.DatabaseContract
import alangsatinantongga.md14.kulitku.db.ScanHelper
import alangsatinantongga.md14.kulitku.entity.Scan
import alangsatinantongga.md14.kulitku.network.Retrofit
import alangsatinantongga.md14.kulitku.network.UploadResponse
import alangsatinantongga.md14.kulitku.network.rotateBitmap
import alangsatinantongga.md14.kulitku.network.uriToFile
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddPhotoActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var binding: ActivityAddPhotoBinding
    private var scan: Scan? = null
    private var position: Int = 0
    private lateinit var scanHelper: ScanHelper

    companion object {
        const val CAMERA_X_RESULT = 200

        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val REQUEST_CODE_PERMISSIONS = 10
        const val REQUEST_CODE_MIGRATE = 69
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val RESULT_ADD = 101
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        val view = binding.root
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        setContentView(view)

        val camera = binding.cameraAccess
        val gallery = binding.gallerryAccess
        val send = binding.btnSend
        val back = binding.btnBack

        camera.setOnClickListener(this)
        gallery.setOnClickListener(this)
        send.setOnClickListener(this)
        back.setOnClickListener(this)

        camera.performClick()

        scanHelper = ScanHelper.getInstance(applicationContext)
        scanHelper.open()

    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, REQUEST_CODE_PERMISSIONS)
    }

    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile

            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.prevImage.setImageBitmap(result)
        }

        else if (it.resultCode == REQUEST_CODE_MIGRATE) {
            val myFile = it.data?.getSerializableExtra("picture") as File

            getFile = myFile

            val result = (
                BitmapFactory.decodeFile(getFile?.path)
            )

            binding.prevImage.setImageBitmap(result)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            REQUEST_CODE_PERMISSIONS -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        cropImage(uri)
                    }

                }
                else{
                    Log.e(TAG, "Image selection error: Couldn't select that image from memory." )
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    setImage(result.uri)
                    val myFile = uriToFile(result.uri, this@AddPhotoActivity)
                    getFile = myFile

                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e(TAG, "Crop error: ${result.getError()}" )
                }
            }
        }
    }

    private fun cropImage(uri: Uri) {
        CropImage.activity(uri)
            .setAspectRatio(350, 500)
            .start(this@AddPhotoActivity)
    }

    private fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(binding.prevImage)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    private fun uploadImage() {
        if (getFile != null) {
            val foto = reduceFileImage(getFile as File)
            val result = BitmapFactory.decodeFile(foto.path)
            val compressQuality = 100
            result.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(foto))

            val requestImageFile = foto.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "uploads",
                foto.name,
                requestImageFile
            )

            val service = Retrofit.getApiService().postImages(imageMultipart)
            service.enqueue(object : Callback<UploadResponse> {
                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            Toast.makeText(
                                this@AddPhotoActivity,
                                "Foto berhasil di Upload",
                                Toast.LENGTH_SHORT
                            ).show()

                            val image = responseBody.data.url.trim()
                            val date = getCurrentDate()
                            val prediction = responseBody.data.result.jsonMemberClass.trim()

                            scan?.image = image
                            scan?.date = date
                            scan?.predict = prediction

                            val values = ContentValues()
                                values.put(DatabaseContract.ScanColumns.IMAGE, image)
                                values.put(DatabaseContract.ScanColumns.DATE, date)
                                values.put(DatabaseContract.ScanColumns.PREDICTION, prediction)

                            val result = scanHelper.insert(values)
                            if (result > 0) {
                                scan?.id = result.toInt()
                                setResult(RESULT_ADD, intent)
                                finish()
                            } else {
                                Toast.makeText(this@AddPhotoActivity, "Gagal menambah data", Toast.LENGTH_SHORT).show()
                            }

                            val moveIntent = Intent(this@AddPhotoActivity, HasilPindaiActivity::class.java)
                            moveIntent.putExtra("hasilPindai", image)
                            moveIntent.putExtra("hasilPrediksi", prediction)
                            startActivity(moveIntent)

                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@AddPhotoActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddPhotoActivity,
                        "Koneksi Tidak Stabil, Mohon Coba Lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        } else {
            Toast.makeText(
                this@AddPhotoActivity,
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale(if (horizontal) (-1f) else 1f, if (vertical) (-1f) else 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true);
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val ei = ExifInterface(file.path)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val rotatedBitmap: Bitmap? = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            ExifInterface.ORIENTATION_TRANSVERSE -> rotateImage(bitmap, 270)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(bitmap, true, vertical = false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(bitmap, false, vertical = true)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }

        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        return file
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.camera_access -> {
                val intent = Intent(this, CameraActivity::class.java)
                launcherIntentCameraX.launch(intent)
            }
            R.id.gallerry_access -> {
                startGallery()
            }
            R.id.btnSend -> {
                uploadImage()
                Toast.makeText(
                    this@AddPhotoActivity,
                    "Sedang Memproses...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            R.id.btnBack -> {
                onBackPressed()
            }
        }
    }
}