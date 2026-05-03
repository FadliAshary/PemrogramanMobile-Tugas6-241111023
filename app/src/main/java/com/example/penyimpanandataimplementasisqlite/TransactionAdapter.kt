package com.example.penyimpanandataimplementasisqlite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.penyimpanandataimplementasisqlite.databinding.ItemTransactionBinding

class TransactionAdapter(private val transactions: List<Transaction>, private val onItemClick: (Transaction) -> Unit) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.itemView.context
        
        holder.binding.tvTitle.text = transaction.title
        holder.binding.tvDate.text = transaction.date
        
        if (transaction.type == "INCOME") {
            holder.binding.tvAmount.text = "+ Rp ${transaction.amount}"
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.income))
            holder.binding.ivType.setImageResource(R.drawable.ic_arrow_upward)
            holder.binding.ivType.setColorFilter(ContextCompat.getColor(context, R.color.income))
            holder.binding.ivType.setBackgroundResource(R.drawable.bg_icon_income)
        } else {
            holder.binding.tvAmount.text = "- Rp ${transaction.amount}"
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.expense))
            holder.binding.ivType.setImageResource(R.drawable.ic_arrow_downward)
            holder.binding.ivType.setColorFilter(ContextCompat.getColor(context, R.color.expense))
            holder.binding.ivType.setBackgroundResource(R.drawable.bg_icon_expense)
        }

        holder.itemView.setOnClickListener { onItemClick(transaction) }
    }

    override fun getItemCount() = transactions.size
}
