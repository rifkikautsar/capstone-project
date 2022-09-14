package alangsatinantongga.md14.kulitku.fragment

import alangsatinantongga.md14.kulitku.activity.AddPhotoActivity
import alangsatinantongga.md14.kulitku.R
import alangsatinantongga.md14.kulitku.activity.ScanResultActivity
import alangsatinantongga.md14.kulitku.databinding.FragmentHomeBaseBinding
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeBaseFragment : Fragment(),View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding : FragmentHomeBaseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBaseBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.moveKubaca.setOnClickListener(this)
        binding.moveKulitku.setOnClickListener(this)
        mAuth = Firebase.auth

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.move_kubaca -> {
                val mKubuku = KubukuFragment()
                val mFragmentManager = parentFragmentManager
                mFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.nav_host_fragment_activity_bottom_nav,
                        mKubuku,
                        KubukuFragment::class.java.simpleName
                    )
                    addToBackStack(null)
                    commit()
                }
            }
            R.id.move_kulitku -> {
                val mKulitku = KulitkuFragment()
                val mFragmentManager = parentFragmentManager
                mFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.nav_host_fragment_activity_bottom_nav,
                        mKulitku,
                        KulitkuFragment::class.java.simpleName
                    )
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

}