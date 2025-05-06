package com.example.cjcodes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cjcodes.data.StudentDao
import com.example.cjcodes.databinding.ActivityConstraintBinding

class ConstraintLayoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConstraintBinding
    private lateinit var studentDao: StudentDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConstraintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database helper
        studentDao = StudentDao(this)

        // Form submission
        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString()
            val age = binding.etAge.text.toString()
            val email = binding.etEmail.text.toString()
            val phone = binding.etPhone.text.toString()

            if (validateForm(name, age, email, phone)) {
                val id = studentDao.addStudent(name, email, phone, age)
                if (id != -1L) {
                    showSuccess("Student saved! ID: $id")
                    clearForm()
                } else {
                    showError("Failed to save student")
                }
            }
        }

        // Clear form
        binding.btnClear.setOnClickListener {
            clearForm()
        }

        setupBottomNavigation()
    }

    private fun validateForm(name: String, age: String, email: String, phone: String): Boolean {
        return when {
            name.isEmpty() -> {
                showError("Please enter name")
                false
            }
            age.isEmpty() -> {
                showError("Please enter age")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showError("Please enter valid email")
                false
            }
            phone.isEmpty() -> {
                showError("Please enter phone number")
                false
            }
            else -> true
        }
    }

    private fun clearForm() {
        binding.etName.text?.clear()
        binding.etAge.text?.clear()
        binding.etEmail.text?.clear()
        binding.etPhone.text?.clear()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.selectedItemId = R.id.nav_constraint

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navigateTo(MainActivity::class.java)
                    true
                }
                R.id.nav_frame -> {
                    navigateTo(FrameLayoutActivity::class.java)
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

    private fun navigateTo(activity: Class<*>) {
        Intent(this, activity).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}