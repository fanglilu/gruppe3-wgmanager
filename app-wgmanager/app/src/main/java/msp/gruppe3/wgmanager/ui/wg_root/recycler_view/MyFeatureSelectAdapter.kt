package msp.gruppe3.wgmanager.ui.wg_root.recycler_view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.enums.Features
import msp.gruppe3.wgmanager.models.dtos.FeatureDto

// Tag for logging
private const val TAG = "WG My Feature Select Adapter"

/**
 * Adapter class for the recycler view to show feature options
 *
 * @author Marcello Alte
 */
class MyFeatureSelectAdapter(private val featureList: Array<Features>) :
    RecyclerView.Adapter<MyFeatureSelectAdapter.MyViewHolder>() {

    // Array with checked features
    private var checkedFeatures = ArrayList<Features>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_with_checkbox, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = featureList[position]
        if (currentItem.name == Features.FINANCE.name) {
            holder.checkBox.text = "Finance"
        } else {
            if (currentItem.name == Features.SHOPPING_LIST.name) {
                holder.checkBox.text = "Shopping List"
            } else {
                if (currentItem.name == Features.CALENDAR.name) {
                    holder.checkBox.text = "Calender"
                }
            }
        }

        // Set click listener to recognize if a checkbox is clicked
        holder.checkBox.setOnClickListener {
            if (holder.checkBox.isChecked) {
                Log.e(TAG, "Checkbox " + holder.checkBox.text + " checked")
                checkedFeatures.add(featureList[position])
            } else {
                Log.e(TAG, "Checkbox " + holder.checkBox.text + " UN-checked")
                checkedFeatures.remove(featureList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return featureList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.list_item_check_box)
    }

    fun getCheckedFeatures(): List<FeatureDto> {
        return this.checkedFeatures.map { FeatureDto(it) }
    }
}