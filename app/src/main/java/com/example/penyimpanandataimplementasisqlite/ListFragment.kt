package com.example.penyimpanandataimplementasisqlite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.penyimpanandataimplementasisqlite.databinding.FragmentListBinding
import java.text.NumberFormat
import java.util.Locale

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        
        setupRecyclerView()
        updateHeroSection()
    }

    private fun updateHeroSection() {
        val transactions = dbHelper.getAllTransactions()
        binding.tvTotalTransactions.text = "${transactions.size} Item"
        
        val balance = dbHelper.getTotalBalance()
        binding.tvSummaryBalance.text = "Rp ${formatNumber(balance)}"
    }

    private fun setupRecyclerView() {
        val transactions = dbHelper.getAllTransactions()
        val adapter = TransactionAdapter(transactions) { transaction ->
            showDetailDialog(transaction)
        }
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter
    }

    private fun formatNumber(number: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return formatter.format(number)
    }

    private fun showDetailDialog(transaction: Transaction) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Rincian Transaksi")
        
        val message = """
            Keterangan: ${transaction.title}
            Jumlah: Rp ${formatNumber(transaction.amount)}
            Tipe: ${if (transaction.type == "INCOME") "Pemasukan" else "Pengeluaran"}
            Tanggal: ${transaction.date}
        """.trimIndent()
        
        builder.setMessage(message)
        builder.setPositiveButton("Tutup", null)
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
