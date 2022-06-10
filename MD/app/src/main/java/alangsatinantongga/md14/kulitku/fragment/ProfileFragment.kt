package alangsatinantongga.md14.kulitku.fragment

import alangsatinantongga.md14.kulitku.activity.MainActivity
import alangsatinantongga.md14.kulitku.databinding.FragmentProfileBinding
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding : FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logout : Button = binding.btnLogout

        mAuth = Firebase.auth

        logout.setOnClickListener {
            mAuth.signOut()
            val i = Intent(this@ProfileFragment.requireActivity(), MainActivity::class.java)
            startActivity(i)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity?.finish()
        }
    }

}