package alangsatinantongga.md14.kulitku.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import alangsatinantongga.md14.kulitku.R
import alangsatinantongga.md14.kulitku.activity.AddPhotoActivity
import alangsatinantongga.md14.kulitku.activity.BottomNavigationActivity
import alangsatinantongga.md14.kulitku.activity.CameraActivity
import alangsatinantongga.md14.kulitku.activity.HasilPindaiActivity
import alangsatinantongga.md14.kulitku.databinding.FragmentHomeBaseBinding
import alangsatinantongga.md14.kulitku.databinding.FragmentScanBinding
import alangsatinantongga.md14.kulitku.db.DatabaseContract
import alangsatinantongga.md14.kulitku.db.ScanHelper
import alangsatinantongga.md14.kulitku.entity.Scan
import alangsatinantongga.md14.kulitku.network.Retrofit
import alangsatinantongga.md14.kulitku.network.UploadResponse
import alangsatinantongga.md14.kulitku.network.rotateBitmap
import alangsatinantongga.md14.kulitku.network.uriToFile
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

class ScanFragment : Fragment(),View.OnClickListener {
    private var scan: Scan? = null
    private var position: Int = 0
    private lateinit var scanHelper: ScanHelper
    private lateinit var binding : FragmentScanBinding

    class Camera {
        companion object {
            const val CAMERA_X_RESULT = 200

            val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
            const val REQUEST_CODE_PERMISSIONS = 10

            const val EXTRA_NOTE = "extra_note"
            const val EXTRA_POSITION = "extra_position"
            const val RESULT_ADD = 101
            const val RESULT_UPDATE = 201
            const val RESULT_DELETE = 301
            const val ALERT_DIALOG_CLOSE = 10
            const val ALERT_DIALOG_DELETE = 20

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                Camera.REQUIRED_PERMISSIONS,
                Camera.REQUEST_CODE_PERMISSIONS
            )
        }

        val camera = binding.cameraAccess
        val gallery = binding.gallerryAccess
        val send = binding.btnSend
        val back = binding.btnBack

        camera.setOnClickListener(this)
        gallery.setOnClickListener(this)
        send.setOnClickListener(this)
        back.setOnClickListener(this)

        camera.performClick()

        scanHelper = ScanHelper.getInstance(this.requireContext())
        scanHelper.open()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Camera.REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this.requireActivity(),
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                activity?.finish()
            }
        }
    }

    private fun allPermissionsGranted() = Camera.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this.requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AddPhotoActivity.CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.ImagePrev.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this.requireActivity())
            getFile = myFile
            binding.ImagePrev.setImageURI(selectedImg)
        }
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
                                this@ScanFragment.requireActivity(),
                                "Foto berhasil di Upload",
                                Toast.LENGTH_SHORT
                            ).show()
                            Toast.makeText(
                                this@ScanFragment.requireActivity(),
                                responseBody.data.message,
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
                                activity?.setResult(Camera.RESULT_ADD, Intent())
                                activity?.finish()
                            } else {
                                Toast.makeText(this@ScanFragment.requireActivity(), "Gagal menambah data", Toast.LENGTH_SHORT).show()
                            }

                            val moveIntent = Intent(this@ScanFragment.requireActivity(), HasilPindaiActivity::class.java)
                            moveIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(moveIntent)
                            activity?.finish()
                        }
                    } else {
                        Toast.makeText(
                            this@ScanFragment.requireActivity(),
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ScanFragment.requireActivity(),
                        "Koneksi Tidak Stabil, Mohon Coba Lagi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        } else {
            Toast.makeText(
                this@ScanFragment.requireActivity(),
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val ei = ExifInterface(file.path)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val rotatedBitmap: Bitmap? = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(bitmap, 270)
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
                val intent = Intent(this@ScanFragment.requireActivity(), CameraActivity::class.java)
                launcherIntentCameraX.launch(intent)
            }
            R.id.gallerry_access -> {
                startGallery()
            }
            R.id.btnSend -> {
                uploadImage()
                Toast.makeText(
                    this@ScanFragment.requireActivity(),
                    "Sedang Memproses...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            R.id.btnBack -> {
                activity?.onBackPressed()
            }
        }
    }

}