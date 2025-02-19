package com.example.gps.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.random.Random

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "transportDatabase.db"
        const val DATABASE_VERSION = 2

        // Table: User (For authentication)
        const val TABLE_USER = "User"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"

        // Table: Driver (For driver details)
        const val TABLE_DRIVER = "Driver"
        const val COLUMN_DRIVER_ID = "driver_id"
        const val COLUMN_DRIVER_NAME = "name"
        const val COLUMN_VEHICLE_TYPE = "vehicle_type"
        const val COLUMN_CONTACT = "contact"
        const val COLUMN_EXPERIENCE = "experience"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = "CREATE TABLE $TABLE_USER (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT, " +
                "$COLUMN_EMAIL TEXT, " +
                "$COLUMN_PASSWORD TEXT)"

        val createDriverTable = "CREATE TABLE $TABLE_DRIVER (" +
                "$COLUMN_DRIVER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_DRIVER_NAME TEXT, " +
                "$COLUMN_VEHICLE_TYPE TEXT, " +
                "$COLUMN_CONTACT TEXT, " +
                "$COLUMN_EXPERIENCE INTEGER)"

        db?.execSQL(createUserTable)
        db?.execSQL(createDriverTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_DRIVER")
        onCreate(db)
    }

    // Insert new user
    fun insertUser(username: String, email: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        return db.insert(TABLE_USER, null, values)
    }

    // Check if user exists
    fun isUserExists(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Insert driver details
    fun insertDriver(name: String, vehicleType: String, contact: String, experience: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DRIVER_NAME, name)
            put(COLUMN_VEHICLE_TYPE, vehicleType)
            put(COLUMN_CONTACT, contact)
            put(COLUMN_EXPERIENCE, experience)
        }
        return db.insert(TABLE_DRIVER, null, values)
    }

    fun getDriverCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_DRIVER", null) // Correct table name
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }


    // Get a random driver by vehicle type
    fun getRandomDriverByVehicleType(vehicleType: String): Driver? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_DRIVER WHERE $COLUMN_VEHICLE_TYPE = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(vehicleType))

        if (cursor.count == 0) {
            cursor.close()
            return null
        }

        val randomIndex = Random.nextInt(cursor.count)
        cursor.moveToPosition(randomIndex)

        val driver = Driver(
            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DRIVER_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRIVER_NAME)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_TYPE)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT)),
            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPERIENCE))
        )

        cursor.close()
        return driver
    }
}

// Data class for Driver
data class Driver(
    val id: Int,
    val name: String,
    val vehicleType: String,
    val contact: String,
    val experience: Int
)
