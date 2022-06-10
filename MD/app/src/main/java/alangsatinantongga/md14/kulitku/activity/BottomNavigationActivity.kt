package alangsatinantongga.md14.kulitku.activity

import alangsatinantongga.md14.kulitku.R
import alangsatinantongga.md14.kulitku.databinding.ActivityBottomNavigationBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)


            val navView: BottomNavigationView = binding.navDashboard

            val navController = findNavController(R.id.nav_host_fragment_activity_bottom_nav)

            navView.setupWithNavController(navController)
        }
}