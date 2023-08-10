package msp.gruppe3.wgmanager.ui.features.shoppinglist.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.models.ShoppingListEntry
import java.util.*

// Recycler adapter for the lists; Called from ShowShoppingListFragment

class MyShoppingListSelectionAdapter(private val activity: FragmentActivity, private val featureList: List<ShoppingListEntry>) : RecyclerView.Adapter<MyShoppingListSelectionAdapter.MyViewHolder>() {
    // Array with checked features
    private var checkedFeatures = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lists_with_checkbox, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Set the text of the recycler to the correct one from the current item
        val currentItem = featureList[position]
        holder.title_text.text = currentItem.name

        holder.checkbox.setOnClickListener(){
            if (holder.checkbox.isChecked) {
                checkedFeatures.add(currentItem.id)
            } else {
                checkedFeatures.remove(currentItem.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return featureList.size
    }

    fun getCheckedFeatures(): List<String> {
        return this.checkedFeatures
    }

    // bind the different elements so they can be accessed
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title_text: TextView = itemView.findViewById(R.id.shopping_list_select_title)
        val checkbox: CheckBox = itemView.findViewById(R.id.shopping_list_select_checked)
    }
}