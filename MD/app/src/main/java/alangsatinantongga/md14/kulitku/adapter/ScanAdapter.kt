package alangsatinantongga.md14.kulitku.adapter

import alangsatinantongga.md14.kulitku.R
import alangsatinantongga.md14.kulitku.databinding.ItemScanBinding
import alangsatinantongga.md14.kulitku.entity.Scan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

class ScanAdapter(private val onItemClickCallback: OnItemClickCallback) :
    RecyclerView.Adapter<ScanAdapter.ScanViewHolder>() {
    var listScan = ArrayList<Scan>()
        set(listScan) {
            if (listScan.size > 0) {
                this.listScan.clear()
            }
            this.listScan.addAll(listScan)
        }

    fun addItem(scan: Scan) {
        this.listScan.add(scan)
        notifyItemInserted(this.listScan.size - 1)
    }

    fun updateItem(position: Int, scan: Scan) {
        this.listScan[position] = scan
        notifyItemChanged(position, scan)
    }

    fun removeItem(position: Int) {
        this.listScan.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listScan.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scan, parent, false)
        return ScanViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        holder.bind(listScan[position])
    }

    override fun getItemCount(): Int = this.listScan.size

    inner class ScanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemScanBinding.bind(itemView)
        fun bind(scan: Scan) {
            binding.apply {
                Glide.with(scanView)
                    .load(scan.image)
                    .into(imgItemPhoto)
                date.text = scan.date
                scanPredict.text = scan.predict
                scanView.setOnClickListener {
                    onItemClickCallback.onItemClicked(scan, adapterPosition)
                }

                val res = ScanViewHolder(itemView).itemView.resources
                val drug = binding.drugList
                if (scanPredict.text == "Jerawat") {
                    drug.text = res.getStringArray(R.array.Jerawat).joinToString()

                } else if (scanPredict.text == "Melasma") {
                    drug.text = res.getStringArray(R.array.Melasma).joinToString()

                } else if (scanPredict.text == "Kutil") {
                    drug.text = res.getStringArray(R.array.Kutil).joinToString()

                } else if (scanPredict.text == "Milia") {
                    drug.text = res.getStringArray(R.array.Milia).joinToString()

                }
            }

        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(selectedScan: Scan?, position: Int?)
    }
}