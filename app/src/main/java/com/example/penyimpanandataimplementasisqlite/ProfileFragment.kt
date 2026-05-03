package com.example.penyimpanandataimplementasisqlite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.penyimpanandataimplementasisqlite.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        loadUserData()

        binding.tvUsername.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnLogout.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                clear()
                apply()
            }
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun loadUserData() {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""
        
        val userData = dbHelper.getUserData(username)
        if (userData != null) {
            binding.tvUsername.text = userData["fullname"]
            binding.tvEmail.text = userData["email"]
        }
    }

    private fun showEditProfileDialog() {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""
        val userData = dbHelper.getUserData(username)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Profil")
        
        val view = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etName = view.findViewById<EditText>(R.id.etEditFullname)
        val etEmail = view.findViewById<EditText>(R.id.etEditEmail)
        
        etName.setText(userData?.get("fullname"))
        etEmail.setText(userData?.get("email"))
        
        builder.setView(view)
        builder.setPositiveButton("Simpan") { _, _ ->
            val newName = etName.text.toString()
            val newEmail = etEmail.text.toString()
            
            if (newName.isNotEmpty() && newEmail.isNotEmpty()) {
                dbHelper.updateProfile(username, newName, newEmail)
                loadUserData()
                Toast.makeText(context, "Profil diperbarui", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
