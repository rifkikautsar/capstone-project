package alangsatinantongga.md14.kulitku.fragment

import alangsatinantongga.md14.kulitku.activity.BottomNavigationActivity
import alangsatinantongga.md14.kulitku.R
import android.content.Intent
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


class LoginFragment : Fragment(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = Firebase.auth
        val btnBack : ImageButton = view.findViewById(R.id.btnBack)
        val btnLogin : Button = view.findViewById(R.id.btnLogin)
        btnBack.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val email: EditText? = view?.findViewById(R.id.emailEditSignIn)
        val pass : EditText? = view?.findViewById(R.id.passEditSignIn)
      
        if (v.id == R.id.btnBack) {
            activity?.onBackPressed()
        } else if (v.id == R.id.btnLogin) {
            val emails = email?.text.toString().trim()
            val password = pass?.text.toString().trim()
            if (TextUtils.isEmpty(emails) && TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@LoginFragment.requireActivity(),
                    "Mohon Masukkan Email atau Password",
                    Toast.LENGTH_SHORT
                ).show()
            }else if (TextUtils.isEmpty(emails)) {
                Toast.makeText(
                    this@LoginFragment.requireActivity(),
                    "Mohon Isi Kolom Email",
                    Toast.LENGTH_SHORT
                ).show()
            }else if (TextUtils.isEmpty(password)) {
                Toast.makeText(
                    this@LoginFragment.requireActivity(),
                    "Mohon Isi Kolom Password",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                mAuth.signInWithEmailAndPassword(emails, password)
                    .addOnCompleteListener { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@LoginFragment.requireActivity(), "Login Berhasil", Toast.LENGTH_SHORT)
                                .show()
                            val i = Intent(this@LoginFragment.requireActivity(), BottomNavigationActivity::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                        } else {
                            Toast.makeText(this@LoginFragment.requireActivity(), "Login Gagal", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }
    }
}