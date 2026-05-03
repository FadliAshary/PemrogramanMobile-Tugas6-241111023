package com.example.penyimpanandataimplementasisqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "FinanceDB", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        // Table Transactions
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, amount INTEGER, type TEXT, date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
        // Table Users
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, email TEXT, fullname TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        if (old < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, email TEXT, fullname TEXT)")
            // Update transactions table to include type if needed, or recreate
            db.execSQL("DROP TABLE IF EXISTS transactions")
            db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, amount INTEGER, type TEXT, date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
        }
    }

    // --- User Functions ---
    fun registerUser(username: String, pass: String, email: String, fullname: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", pass)
            put("email", email)
            put("fullname", fullname)
        }
        return db.insert("users", null, values)
    }

    fun loginUser(username: String, pass: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", arrayOf(username, pass))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getUserData(username: String): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE username=?", arrayOf(username))
        if (cursor.moveToFirst()) {
            val data = mapOf(
                "username" to cursor.getString(1),
                "email" to cursor.getString(3),
                "fullname" to cursor.getString(4)
            )
            cursor.close()
            return data
        }
        cursor.close()
        return null
    }

    fun updateProfile(username: String, fullname: String, email: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("fullname", fullname)
            put("email", email)
        }
        return db.update("users", values, "username=?", arrayOf(username))
    }

    // --- Transaction Functions ---
    fun insertTransaction(title: String, amount: Int, type: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("amount", amount)
            put("type", type)
        }
        return db.insert("transactions", null, values)
    }

    fun getTotalBalance(): Int {
        var balance = 0
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT amount, type FROM transactions", null)
        if (cursor.moveToFirst()) {
            do {
                val amount = cursor.getInt(0)
                val type = cursor.getString(1)
                if (type == "INCOME") balance += amount else balance -= amount
            } while (cursor.moveToNext())
        }
        cursor.close()
        return balance
    }

    fun getAllTransactions(): List<Transaction> {
        val list = mutableListOf<Transaction>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM transactions ORDER BY date DESC", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(Transaction(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}

data class Transaction(val id: Int, val title: String, val amount: Int, val type: String, val date: String)
