package com.example.penyimpanandataimplementasisqlite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.penyimpanandataimplementasisqlite.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Cek apakah sudah login
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        if (sharedPref.contains("username")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullname.text.toString()
            val email = binding.etEmail.text.toString()
            val user = binding.etUsername.text.toString()
            val pass = binding.etPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && user.isNotEmpty() && pass.isNotEmpty()) {
                val result = dbHelper.registerUser(user, pass, email, name)
                if (result != -1L) {
                    Toast.makeText(this, "Registrasi Berhasil! Silakan Login", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Registrasi Gagal (Username mungkin sudah ada)", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
