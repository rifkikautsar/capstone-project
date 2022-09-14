package alangsatinantongga.md14.kulitku.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import alangsatinantongga.md14.kulitku.R
import alangsatinantongga.md14.kulitku.databinding.FragmentHasilPindaiDetailBinding
import alangsatinantongga.md14.kulitku.databinding.FragmentHomeBaseBinding
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.view.menu.MenuView

class HasilPindaiDetailFragment : Fragment() {
    private lateinit var binding : FragmentHasilPindaiDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHasilPindaiDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}