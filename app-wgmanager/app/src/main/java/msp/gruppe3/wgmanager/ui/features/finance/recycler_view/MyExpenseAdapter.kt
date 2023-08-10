package msp.gruppe3.wgmanager.ui.features.finance.recycler_view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.models.dtos.finance.ReducedExpenseDto
import msp.gruppe3.wgmanager.common.StringBuilderUtil.Companion.formatDate

private const val currency = "€"

/**
 * Recycler view for expenses for finance feature
 *
 * @author Marcello Alte
 */
class MyExpenseAdapter(expensesListParam: List<ReducedExpenseDto>) :
    RecyclerView.Adapter<MyExpenseAdapter.MyViewHolder>() {

    var expensesList: List<ReducedExpenseDto> = expensesListParam

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_with_expense, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = expensesList[position]

        holder.name.text = currentItem.description
        holder.payer.text = currentItem.payer?.name
        holder.date.text = formatDate(currentItem.expenseDate.toString())
        holder.price.text = String.format("%.2f${currency}", currentItem.price)

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id", currentItem.id.toString())
            holder.itemView.findNavController().navigate(R.id.financeAddExpenseFragment, bundle)
        }

        // Highlight recurring items
        if (currentItem.recurring != null) {
            Log.e("MyExpenseAdapter", currentItem.toString())
            holder.itemView.isSelected = true
        } else {
            holder.itemView.isSelected = false
        }
        Log.e("MyExpenseAdapter", "item $position from $itemCount")
    }

    override fun getItemCount(): Int {
        return expensesList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.finance_list_item_expense_name)
        val payer: TextView = itemView.findViewById(R.id.finance_list_item_expense_payer)
        val date: TextView = itemView.findViewById(R.id.finance_list_item_expense_date)
        val price: TextView = itemView.findViewById(R.id.finance_list_item_expense_price)
    }
}