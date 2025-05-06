package com.example.cjcodes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cjcodes.databinding.LinearLayoutBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: LinearLayoutBinding
    private val PICK_IMAGE_REQUEST = 1
    private var profileImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LinearLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Profile picture upload
        binding.btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Submit button
        binding.submitButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val age = binding.ageInput.text.toString()
            val email = binding.emailInput.text.toString()

            if (validateInputs(name, age, email)) {
                Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                // Here you would save to database
            }
        }

        // Bottom navigation - using binding instead of findViewById
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on home
                    true
                }
                R.id.nav_frame -> {
                    navigateTo(FrameLayoutActivity::class.java)
                    true
                }
                R.id.nav_constraint -> {
                    navigateTo(ConstraintLayoutActivity::class.java)
                    true
                }
                R.id.nav_relative -> {
                    navigateTo(RelativeLayoutActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                profileImageUri = uri
                binding.profileImage.setImageURI(uri)
            }
        }
    }

    private fun navigateTo(activity: Class<*>) {
        Intent(this, activity).apply {
            putExtra("name", binding.nameInput.text.toString())
            putExtra("age", binding.ageInput.text.toString())
            putExtra("email", binding.emailInput.text.toString())
            profileImageUri?.let { putExtra("profileUri", it.toString()) }
            startActivity(this)
        }
    }

    private fun validateInputs(name: String, age: String, email: String): Boolean {
        return when {
            name.isEmpty() -> {
                showToast("Please enter your name")
                false
            }
            age.isEmpty() -> {
                showToast("Please enter your age")
                false
            }
            email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Please enter a valid email")
                false
            }
            else -> true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}