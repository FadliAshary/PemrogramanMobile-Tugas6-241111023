package com.example.penyimpanandataimplementasisqlite

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.penyimpanandataimplementasisqlite.databinding.FragmentHomeBinding
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""
        
        val userData = dbHelper.getUserData(username)
        binding.tvFullName.text = userData?.get("fullname") ?: "User"

        updateBalance()

        binding.btnIncomeCard.setOnClickListener {
            showTransactionDialog("INCOME")
        }

        binding.btnExpenseCard.setOnClickListener {
            showTransactionDialog("EXPENSE")
        }
    }

    private fun updateBalance() {
        val balance = dbHelper.getTotalBalance()
        binding.tvBalance.text = "Rp ${formatNumber(balance)}"
    }

    private fun formatNumber(number: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return formatter.format(number)
    }

    private fun showTransactionDialog(type: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(if (type == "INCOME") "Tambah Dana" else "Tarik Dana")
        
        val view = layoutInflater.inflate(R.layout.dialog_transaction, null)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        
        builder.setView(view)
        builder.setPositiveButton("Simpan") { _, _ ->
            val title = etTitle.text.toString()
            val amountStr = etAmount.text.toString()
            val amount = amountStr.toIntOrNull() ?: 0

            if (title.isNotEmpty() && amount > 0) {
                dbHelper.insertTransaction(title, amount, type)
                updateBalance()
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
