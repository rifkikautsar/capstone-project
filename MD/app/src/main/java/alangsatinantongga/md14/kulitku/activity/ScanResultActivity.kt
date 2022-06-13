package alangsatinantongga.md14.kulitku.activity

import alangsatinantongga.md14.kulitku.R
import alangsatinantongga.md14.kulitku.adapter.ScanAdapter
import alangsatinantongga.md14.kulitku.databinding.ActivityScanResultBinding
import alangsatinantongga.md14.kulitku.db.ScanHelper
import alangsatinantongga.md14.kulitku.entity.Scan
import alangsatinantongga.md14.kulitku.helper.MappingHelper
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.TextureView
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ScanResultActivity : AppCompatActivity() {
    private lateinit var binding : ActivityScanResultBinding
    private lateinit var adapter: ScanAdapter
    private var list = ArrayList<Scan>()
    private val listScan = MutableLiveData<ArrayList<Scan>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvScan.setHasFixedSize(true)
        binding.rvScan.layoutManager = LinearLayoutManager(this)
        adapter = ScanAdapter(object : ScanAdapter.OnItemClickCallback {
            override fun onItemClicked(selectedScan: Scan?, position: Int?) {
                val intent = Intent(this@ScanResultActivity, AddPhotoActivity::class.java)
            }
        })
        binding.rvScan.adapter = adapter

        loadScansAsync(this)
    }

    private fun loadScansAsync(context: Context) {
        lifecycleScope.launch {
            val scanHelper = ScanHelper.getInstance(applicationContext)
            scanHelper.open()
            val deferredScans = async(Dispatchers.IO) {
                val cursor = scanHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val scans = deferredScans.await()
            if (scans.size > 0) {
                adapter.listScan = scans
            } else {
                adapter.listScan = ArrayList()
            }
            scanHelper.close()
        }
    }

    private  fun setDrugs () {
        val drug: TextView = findViewById<TextView>(R.id.drugList)
        drug.text = getString(R.string.user)
        val predict: TextView = findViewById(R.id.scanPredict)

        // val res = resources
        // if (predict.text == "Melasma") {
        //    drug.text = res.getStringArray(R.array.Melasma).toString()
    }
}
