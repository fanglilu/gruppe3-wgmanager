package msp.gruppe3.wgmanager.ui.features.finance.recycler_view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.StringBuilderUtil.Companion.buildPeriodString
import msp.gruppe3.wgmanager.models.dtos.finance.DebtDto
import java.util.*
import kotlin.collections.ArrayList

private const val currency = "€"

/**
 * Recycler view for debts for finance feature (settle up)
 *
 * @author Marcello Alte
 */
class MyDebtAdapter(private val debtAndReceivedList: List<DebtDto>, private val userId: UUID) :
    RecyclerView.Adapter<MyDebtAdapter.MyViewHolder>() {

    var settledUpDebtList = ArrayList<DebtDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_with_dept, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = debtAndReceivedList[position]

        if (currentItem.debtor.id == userId) {
            holder.payer.text = currentItem.receiver.name
            holder.price.text = String.format("%.2f${currency}", currentItem.debt)
            val periodString = "Period ${buildPeriodString(currentItem.invoicePeriod)}"
            holder.period.text = periodString
            holder.button.setBackgroundColor(Color.Green.hashCode())
            holder.button.text = "Settle up"

            holder.button.setOnClickListener {
                changeButtonText(holder, currentItem)
            }
        } else {
            holder.description.text = "You are owed by"
            holder.payer.text = currentItem.debtor.name
            holder.price.text = String.format("%.2f${currency}", currentItem.debt)
            val periodString = "Period ${buildPeriodString(currentItem.invoicePeriod)}"
            holder.period.text = periodString

            holder.button.visibility = GONE
        }
    }

    private fun changeButtonText(holder: MyViewHolder, currentItem: DebtDto) {
        if (!settledUpDebtList.contains(currentItem)) {
            settledUpDebtList.add(currentItem)
            holder.button.setBackgroundColor(Color.Red.hashCode())
            holder.button.text = "Undo"
        } else {
            settledUpDebtList.remove(currentItem)
            holder.button.setBackgroundColor(Color.Green.hashCode())
            holder.button.text = "Settle up"
        }
        Log.e("Recyc", "settledUpDebtList $settledUpDebtList")
    }

    override fun getItemCount(): Int {
        return debtAndReceivedList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.finance_list_item_debt_name)
        val payer: TextView = itemView.findViewById(R.id.finance_list_item_debt_payer)
        val price: TextView = itemView.findViewById(R.id.finance_list_item_debt_price)
        val period: TextView = itemView.findViewById(R.id.finance_list_item_period_date)
        val button: Button = itemView.findViewById(R.id.finance_list_item_debt_button)
    }
}