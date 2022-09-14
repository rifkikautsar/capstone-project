package alangsatinantongga.md14.kulitku.activity

import alangsatinantongga.md14.kulitku.R
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.bumptech.glide.Glide

class HasilPindaiActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_pindai)

        val prediksi: Button = findViewById(R.id.hasilPrediksi)
        val kembali : ImageButton = findViewById(R.id.btnBack)
        kembali.setOnClickListener(this)
        prediksi.setOnClickListener(this)

        val cl_dim : ConstraintLayout = findViewById(R.id.cl_dim)
        cl_dim.visibility = View.INVISIBLE

        val gambarPindai = intent?.getStringExtra("hasilPindai").toString()

        val gambar : ImageView = findViewById(R.id.hasil_Image)

        Glide.with(gambar)
            .load(gambarPindai)
            .into(gambar)

        supportActionBar?.hide()
    }

    @SuppressLint("InflateParams")
    override fun onClick(v: View?) {
        val cl_dim : ConstraintLayout = findViewById(R.id.cl_dim)

        when (v?.id) {
            R.id.btnBack -> {
                val moveIntent = Intent(this@HasilPindaiActivity, BottomNavigationActivity::class.java)
                moveIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(moveIntent)
                finish()
            }

            R.id.hasilPrediksi -> {
                cl_dim.visibility = View.VISIBLE
                val popupView: View = LayoutInflater.from(this).inflate(R.layout.fragment_hasil_pindai_detail_, null)
                val popupWindow = PopupWindow(
                    popupView,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,

                    )
                val btnDismiss: Button = popupView.findViewById(R.id.kembali)

                fillData(popupView)

                btnDismiss.setOnClickListener {
                    popupWindow.dismiss()
                    cl_dim.visibility = View.INVISIBLE
                }

                popupWindow.showAsDropDown(popupView, 0, 0)
            }
        }
    }

    fun fillData(p: View) {

        val tvPopupHasilPrediksi: TextView = p.findViewById(R.id.scanPredictDetail)
        val drug : TextView = p.findViewById(R.id.drugListDetail)
        val prediksi = intent?.getStringExtra("hasilPrediksi").toString()
        val res = resources


        tvPopupHasilPrediksi.setText(prediksi)
        if (tvPopupHasilPrediksi.text == "Jerawat") {
            drug.text = res.getStringArray(R.array.Jerawat).joinToString()

        } else if (tvPopupHasilPrediksi.text == "Melasma") {
            drug.text = res.getStringArray(R.array.Melasma).joinToString()

        } else if (tvPopupHasilPrediksi.text == "Kutil") {
            drug.text = res.getStringArray(R.array.Kutil).joinToString()

        } else if (tvPopupHasilPrediksi.text == "Milia") {
            drug.text = res.getStringArray(R.array.Milia).joinToString()

        }

    }
}