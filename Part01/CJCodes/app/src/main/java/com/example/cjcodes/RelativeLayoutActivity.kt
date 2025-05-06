package com.example.cjcodes

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.cjcodes.data.StudentContract
import com.example.cjcodes.data.StudentDao
import com.google.android.material.bottomnavigation.BottomNavigationView

class RelativeLayoutActivity : AppCompatActivity() {
    private lateinit var etSearch: EditText
    private lateinit var btnClearSearch: ImageButton
    private lateinit var studentListContainer: LinearLayout
    private lateinit var btnDelete: Button
    private lateinit var btnViewProfile: Button
    private lateinit var btnEdit: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var cardStudentDetails: CardView
    private lateinit var tvSelectedStudentName: TextView
    private lateinit var tvSelectedStudentEmail: TextView
    private lateinit var tvSelectedStudentPhone: TextView
    private lateinit var tvSelectedStudentAge: TextView
    private lateinit var btnSettings: ImageButton
    private lateinit var studentDao: StudentDao
    private var selectedStudentId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relative)

        // Initialize all UI components
        etSearch = findViewById(R.id.etSearch)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        studentListContainer = findViewById(R.id.llStudentContainer)
        btnDelete = findViewById(R.id.btnDelete)
        btnViewProfile = findViewById(R.id.btnViewProfile)
        btnEdit = findViewById(R.id.btnEdit)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)
        cardStudentDetails = findViewById(R.id.cardStudentDetails)
        tvSelectedStudentName = findViewById(R.id.tvSelectedStudentName)
        tvSelectedStudentEmail = findViewById(R.id.tvSelectedStudentEmail)
        tvSelectedStudentPhone = findViewById(R.id.tvSelectedStudentPhone)
        tvSelectedStudentAge = findViewById(R.id.tvSelectedStudentAge)
        btnSettings = findViewById(R.id.btnSettings)

        studentDao = StudentDao(this)
        displayStudents(studentDao.getAllStudents())

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else false
        }

        btnClearSearch.setOnClickListener {
            etSearch.text.clear()
            performSearch()
            btnClearSearch.visibility = View.GONE
        }

        etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && etSearch.text.isNotEmpty()) {
                btnClearSearch.visibility = View.VISIBLE
            } else if (!hasFocus && etSearch.text.isEmpty()) {
                btnClearSearch.visibility = View.GONE
            }
        }

        btnDelete.setOnClickListener {
            if (selectedStudentId != -1L) {
                showDeleteConfirmationDialog(selectedStudentId)
            }
        }

        btnViewProfile.setOnClickListener {
            if (selectedStudentId != -1L) {
                showStudentDetailsDialog(selectedStudentId)
            }
        }

        btnEdit.setOnClickListener {
            if (selectedStudentId != -1L) {
                val cursor = studentDao.getStudentById(selectedStudentId)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val name = it.getString(it.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_NAME))
                        val email = it.getString(it.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_EMAIL))
                        val phone = it.getString(it.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_PHONE))
                        val age = it.getString(it.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_AGE))
                        showEditDialog(name, email, phone, age, selectedStudentId)
                    }
                }
            }
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        setupBottomNavigation()
    }

    private fun performSearch() {
        progressBar.visibility = View.VISIBLE
        studentListContainer.removeAllViews()

        val query = etSearch.text.toString().trim()
        val cursor = if (query.isEmpty()) {
            studentDao.getAllStudents()
        } else {
            studentDao.searchStudents(query)
        }

        displayStudents(cursor)
        progressBar.visibility = View.GONE
    }

    private fun displayStudents(cursor: Cursor) {
        studentListContainer.removeAllViews()
        var found = false

        if (cursor.moveToFirst()) {
            do {
                found = true
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_NAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_EMAIL))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_PHONE))
                val age = cursor.getString(cursor.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_AGE))

                // Create a student item view
                val studentView = LayoutInflater.from(this).inflate(R.layout.item_student, null)
                studentView.findViewById<TextView>(R.id.tvStudentName).text = name
                studentView.findViewById<TextView>(R.id.tvStudentEmail).text = "Email: $email"
                studentView.findViewById<TextView>(R.id.tvStudentPhone).text = "Phone: $phone"
                studentView.findViewById<TextView>(R.id.tvStudentAge).text = "Age: $age"

                // Make the entire item clickable
                studentView.setOnClickListener {
                    selectedStudentId = id

                    // Update card with selected student details
                    tvSelectedStudentName.text = name
                    tvSelectedStudentEmail.text = "Email: $email"
                    tvSelectedStudentPhone.text = "Phone: $phone"
                    tvSelectedStudentAge.text = "Age: $age"
                    cardStudentDetails.visibility = View.VISIBLE

                    // Reset background of all items
                    for (i in 0 until studentListContainer.childCount) {
                        studentListContainer.getChildAt(i).setBackgroundResource(android.R.color.transparent)
                    }

                    // Highlight selected item
                    studentView.setBackgroundResource(R.drawable.selected_item_background)

                    highlightSelectedStudent(name)
                }

                studentListContainer.addView(studentView)

            } while (cursor.moveToNext())
        }

        if (!found) {
            tvEmpty.visibility = View.VISIBLE
            selectedStudentId = -1
            cardStudentDetails.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
        }

        cursor.close()
    }

    private fun highlightSelectedStudent(name: String) {
        Toast.makeText(this, "Selected: $name", Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog(studentId: Long) {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete this student?")
            .setPositiveButton("Delete") { _, _ -> deleteStudent(studentId) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteStudent(studentId: Long) {
        val rowsDeleted = studentDao.deleteStudent(studentId)
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show()
            selectedStudentId = -1
            cardStudentDetails.visibility = View.GONE
            performSearch()
        } else {
            Toast.makeText(this, "Failed to delete student", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showStudentDetailsDialog(studentId: Long) {
        val cursor = studentDao.getStudentById(studentId)
        cursor?.use {
            if (it.moveToFirst()) {
                val name = it.getString(it.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_NAME))
                val email = it.getString(it.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_EMAIL))
                val phone = it.getString(it.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_PHONE))
                val age = it.getString(it.getColumnIndexOrThrow(StudentContract.StudentEntry.COLUMN_AGE))

                showStudentDialog(name, email, phone, age, studentId)
            }
        }
    }

    private fun showStudentDialog(name: String, email: String, phone: String, age: String, studentId: Long) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_student_details, null)
        dialogView.findViewById<TextView>(R.id.tvName).text = name
        dialogView.findViewById<TextView>(R.id.tvEmail).text = email
        dialogView.findViewById<TextView>(R.id.tvPhone).text = phone
        dialogView.findViewById<TextView>(R.id.tvAge).text = age

        AlertDialog.Builder(this)
            .setTitle("Student Details")
            .setView(dialogView)
            .setPositiveButton("Edit") { _, _ ->
                showEditDialog(name, email, phone, age, studentId)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showEditDialog(name: String, email: String, phone: String, age: String, studentId: Long) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_student, null)

        val etName = dialogView.findViewById<EditText>(R.id.etName).apply { setText(name) }
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail).apply { setText(email) }
        val etPhone = dialogView.findViewById<EditText>(R.id.etPhone).apply { setText(phone) }
        val etAge = dialogView.findViewById<EditText>(R.id.etAge).apply { setText(age) }

        AlertDialog.Builder(this)
            .setTitle("Edit Student")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val updatedName = etName.text.toString().trim()
                val updatedEmail = etEmail.text.toString().trim()
                val updatedPhone = etPhone.text.toString().trim()
                val updatedAge = etAge.text.toString().trim()

                if (validateInput(updatedName, updatedEmail, updatedPhone, updatedAge)) {
                    updateStudent(studentId, updatedName, updatedEmail, updatedPhone, updatedAge)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateStudent(studentId: Long, name: String, email: String, phone: String, age: String) {
        val values = ContentValues().apply {
            put(StudentContract.StudentEntry.COLUMN_NAME, name)
            put(StudentContract.StudentEntry.COLUMN_EMAIL, email)
            put(StudentContract.StudentEntry.COLUMN_PHONE, phone)
            put(StudentContract.StudentEntry.COLUMN_AGE, age)
        }

        val rowsUpdated = studentDao.updateStudent(studentId, values)
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Student updated", Toast.LENGTH_SHORT).show()

            // Update card details if visible
            if (cardStudentDetails.visibility == View.VISIBLE) {
                tvSelectedStudentName.text = name
                tvSelectedStudentEmail.text = "Email: $email"
                tvSelectedStudentPhone.text = "Phone: $phone"
                tvSelectedStudentAge.text = "Age: $age"
            }

            performSearch()
        } else {
            Toast.makeText(this, "Failed to update student", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(name: String, email: String, phone: String, age: String): Boolean {
        return when {
            name.isEmpty() -> {
                toast("Name cannot be empty"); false
            }
            email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                toast("Enter a valid email address"); false
            }
            phone.isEmpty() -> {
                toast("Phone cannot be empty"); false
            }
            age.isEmpty() -> {
                toast("Age cannot be empty"); false
            }
            else -> true
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_relative

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navigateTo(MainActivity::class.java)
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

    override fun onDestroy() {
        studentDao.close()
        super.onDestroy()
    }
}