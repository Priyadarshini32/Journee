package com.example.gps.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "userDatabase.db"
        const val DATABASE_VERSION = 1

        const val TABLE_USER = "User"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_USER (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT, " +
                "$COLUMN_EMAIL TEXT, " +
                "$COLUMN_PASSWORD TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    // Method to insert new user
    fun insertUser(username: String, email: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)

        return db.insert(TABLE_USER, null, values) // Returns the row ID of the inserted row, or -1 if an error occurred
    }

    // Method to check if user exists
    fun isUserExists(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_USERNAME = '$username' AND $COLUMN_PASSWORD = '$password'"
        val cursor = db.rawQuery(query, null)
        return cursor.count > 0
    }
}
