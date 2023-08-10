package msp.gruppe3.wgmanager.ui.wg_root.recycler_view


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.enums.Features

// Tag for logging
private const val TAG = "WG My Feature Select Adapter"

/**
 * Adapter class for the recycler view to show feature options
 *
 * @author Marcello Alte
 */
   class MyFeatureSelectAdapterWithoutCheckbox(private val featureList: Array<Features>) : RecyclerView.Adapter<MyFeatureSelectAdapterWithoutCheckbox.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lists_without_checkbox, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = featureList[position]
        if (currentItem.name == Features.FINANCE.name) {
            holder.text.text = "- Finance"
        } else {
            if (currentItem.name == Features.SHOPPING_LIST.name) {
                holder.text.text = "- Shopping List"
            } else {
                if (currentItem.name == Features.CALENDAR.name) {
                    holder.text.text = "- Calender"
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return featureList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.list_without_checkbox_title)
    }

}