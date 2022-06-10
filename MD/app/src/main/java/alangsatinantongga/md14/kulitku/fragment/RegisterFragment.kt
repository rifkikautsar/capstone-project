package alangsatinantongga.md14.kulitku.fragment

import alangsatinantongga.md14.kulitku.R
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment(), View.OnClickListener{
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = Firebase.auth
        val btnBack : ImageButton = view.findViewById(R.id.btnBack)
        val btnRegister : Button = view.findViewById(R.id.btnRegister)
        btnBack.setOnClickListener(this)
        btnRegister.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val email : EditText? = view?.findViewById(R.id.emailEditRegister)
        val pass : EditText? = view?.findViewById(R.id.passEditRegister)
        val mLoginFragment = LoginFragment()
        val mFragmentManager = parentFragmentManager

        if (v.id == R.id.btnBack) {
            activity?.onBackPressed()
        } else if (v.id == R.id.btnRegister) {
            val emails = email?.text.toString().trim()
            val password = pass?.text.toString().trim()
            if (TextUtils.isEmpty(emails) && TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@RegisterFragment.requireActivity(),
                    "Mohon Masukkan Email atau Password",
                    Toast.LENGTH_SHORT
                ).show()
            }else if (TextUtils.isEmpty(emails)) {
                Toast.makeText(
                    this@RegisterFragment.requireActivity(),
                    "Mohon Isi Kolom Email",
                    Toast.LENGTH_SHORT
                ).show()
            }else if (TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@RegisterFragment.requireActivity(),
                    "Mohon Isi Kolom Password",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mAuth.createUserWithEmailAndPassword(emails, password)
                    .addOnCompleteListener { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@RegisterFragment.requireActivity(), "Registrasi Berhasil", Toast.LENGTH_SHORT)
                                .show()
                            mAuth.signOut()
                            mFragmentManager.beginTransaction().apply {
                                replace(R.id.frame_container, mLoginFragment, LoginFragment::class.java.simpleName)
                                mFragmentManager.popBackStack()
                                commit()
                            }
                        } else {
                            Toast.makeText(this@RegisterFragment.requireActivity(), "Registrasi Gagal", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }
    }

}