package com.example.cjcodes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cjcodes.databinding.ActivityFrameBinding

class FrameLayoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFrameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive data from MainActivity
        val name = intent.getStringExtra("name") ?: "User"
        val age = intent.getStringExtra("age") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val profileUriString = intent.getStringExtra("profileUri")

        // Update UI
        binding.tvWelcome.text = "Welcome, $name!"
        binding.tvDetails.text = "Age: $age\nEmail: $email"

        // Load profile image if exists
        profileUriString?.let {
            binding.ivProfile.setImageURI(Uri.parse(it))
        }

        binding.btnEdit.setOnClickListener {
            finish() // Return to MainActivity
        }

        // Setup bottom navigation
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish()
                    true
                }
                R.id.nav_frame -> {
                    // Already here
                    true
                }
                R.id.nav_constraint -> {
                    startActivity(Intent(this, ConstraintLayoutActivity::class.java).apply {
                        putExtras(intent.extras ?: Bundle())
                    })
                    finish()
                    true
                }
                R.id.nav_relative -> {
                    startActivity(Intent(this, RelativeLayoutActivity::class.java).apply {
                        putExtras(intent.extras ?: Bundle())
                    })
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}