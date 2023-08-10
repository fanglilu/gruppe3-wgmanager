package msp.gruppe3.wgmanager.ui.wg_root.recycler_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.StringBuilderUtil.Companion.formatDate
import msp.gruppe3.wgmanager.models.dtos.RoleWgDto

/**
 * Adapter class for the recycler view to show wg list
 *
 * @author Marcello Alte
 */
class MyWgSelectAdapter(private val wgList: List<RoleWgDto>) :
    RecyclerView.Adapter<MyWgSelectAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_with_wg, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = wgList[position]

        holder.title.text = currentItem.name
        holder.date.text = buildString {
        append("Joined ")
        append(formatDate(currentItem.joinedAt))
    }
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id", currentItem.id.toString())

            holder.itemView.findNavController().navigate(R.id.wgDetailFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return wgList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.wg_list_item_name)
        val date: TextView = itemView.findViewById(R.id.wg_list_item_date)
    }
}