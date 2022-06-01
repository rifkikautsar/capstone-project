package alangsatinantongga.md14.kulitku

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeBaseActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_base)
        val logout : Button = findViewById(R.id.btnLogout)
        mAuth = Firebase.auth
        val addPhoto : ImageButton = findViewById(R.id.btnAddPhoto)
        addPhoto.setOnClickListener(this)

        logout.setOnClickListener {
            mAuth.signOut()
            val i = Intent(this@HomeBaseActivity, MainActivity::class.java)
            startActivity(i)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAddPhoto -> {
                val intent = Intent(this, AddPhotoActivity::class.java)
                startActivity(intent)
            }
        }
    }

}