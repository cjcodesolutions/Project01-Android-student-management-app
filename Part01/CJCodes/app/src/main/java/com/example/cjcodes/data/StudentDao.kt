package com.example.cjcodes.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class StudentDao(context: Context) {
    private val dbHelper = StudentDbHelper(context)

    // CREATE - Add a new student
    fun addStudent(name: String, email: String, phone: String, age: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(StudentContract.StudentEntry.COLUMN_NAME, name)
            put(StudentContract.StudentEntry.COLUMN_EMAIL, email)
            put(StudentContract.StudentEntry.COLUMN_PHONE, phone)
            put(StudentContract.StudentEntry.COLUMN_AGE, age)
        }
        return db.insert(StudentContract.StudentEntry.TABLE_NAME, null, values)
    }

    // READ ALL - Get all students
    fun getAllStudents(): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            StudentContract.StudentEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${StudentContract.StudentEntry.COLUMN_NAME} ASC"
        )
    }



    // SEARCH - Find students by name or email
    fun searchStudents(query: String): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            StudentContract.StudentEntry.TABLE_NAME,
            null,
            "${StudentContract.StudentEntry.COLUMN_NAME} LIKE ? OR ${StudentContract.StudentEntry.COLUMN_EMAIL} LIKE ?",
            arrayOf("%$query%", "%$query%"),
            null,
            null,
            "${StudentContract.StudentEntry.COLUMN_NAME} ASC"
        )
    }

    // UPDATE - Update student info by ID
    fun updateStudent(id: Long, name: String, email: String, phone: String, age: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(StudentContract.StudentEntry.COLUMN_NAME, name)
            put(StudentContract.StudentEntry.COLUMN_EMAIL, email)
            put(StudentContract.StudentEntry.COLUMN_PHONE, phone)
            put(StudentContract.StudentEntry.COLUMN_AGE, age)
        }
        return db.update(
            StudentContract.StudentEntry.TABLE_NAME,
            values,
            "${StudentContract.StudentEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    // DELETE - Delete student by ID
    fun deleteStudent(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            StudentContract.StudentEntry.TABLE_NAME,
            "${StudentContract.StudentEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }
    fun updateStudent(id: Long, values: ContentValues): Int {
        val db = dbHelper.writableDatabase
        return db.update(
            StudentContract.StudentEntry.TABLE_NAME,
            values,
            "${StudentContract.StudentEntry.COLUMN_ID}=?",
            arrayOf(id.toString())
        )
    }
    fun getStudentById(id: Long): Cursor? {
        val db = dbHelper.readableDatabase
        return db.query(
            StudentContract.StudentEntry.TABLE_NAME,
            null,
            "${StudentContract.StudentEntry.COLUMN_ID}=?",
            arrayOf(id.toString()),
            null, null, null
        )
    }


    // Close DB helper when done
    fun close() {
        dbHelper.close()
    }
}
