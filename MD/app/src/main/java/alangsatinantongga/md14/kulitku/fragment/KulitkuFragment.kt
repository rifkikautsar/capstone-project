package alangsatinantongga.md14.kulitku.fragment

import alangsatinantongga.md14.kulitku.activity.AddPhotoActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import alangsatinantongga.md14.kulitku.adapter.ScanAdapter
import alangsatinantongga.md14.kulitku.databinding.FragmentKulitkuBinding
import alangsatinantongga.md14.kulitku.db.ScanHelper
import alangsatinantongga.md14.kulitku.entity.Scan
import alangsatinantongga.md14.kulitku.helper.MappingHelper
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class KulitkuFragment :Fragment() {
    private lateinit var binding : FragmentKulitkuBinding
    private lateinit var adapter: ScanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKulitkuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvScan.setHasFixedSize(true)
        binding.rvScan.layoutManager = LinearLayoutManager(requireContext())
        adapter = ScanAdapter(object : ScanAdapter.OnItemClickCallback {
            override fun onItemClicked(selectedScan: Scan?, position: Int?) {
                val intent = Intent(this@KulitkuFragment.requireActivity(), AddPhotoActivity::class.java)
            }
        })
        binding.rvScan.adapter = adapter

        loadScansAsync(requireContext())

    }

    private fun loadScansAsync(context: Context) {
        lifecycleScope.launch {
            val scanHelper = ScanHelper.getInstance(requireContext())
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

    override fun onResume() {
        super.onResume()
        binding.rvScan.adapter = adapter
    }

}