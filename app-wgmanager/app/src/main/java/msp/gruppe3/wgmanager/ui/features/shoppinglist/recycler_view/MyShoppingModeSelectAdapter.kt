package msp.gruppe3.wgmanager.ui.features.shoppinglist.recycler_view

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.models.Item
import msp.gruppe3.wgmanager.models.dtos.UpdateItemDto
import msp.gruppe3.wgmanager.services.ItemsService
import retrofit2.HttpException


class MyShoppingModeSelectAdapter(private val featureList: List<Item>, private val context: Context) : RecyclerView.Adapter<MyShoppingModeSelectAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_items_with_checkbox_and_details_shopping_mode, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = featureList[position]
        holder.title_text.text = currentItem.name
        holder.description.text = currentItem.description
        holder.owner.text = context.getString(R.string.list_creator_name, currentItem.owner)
        holder.listName.text = context.getString(R.string.on_list, currentItem.listName)
        holder.checkbox.isChecked = currentItem.isBought

        // Recognise if the bought status has to be changed
        holder.checkbox.setOnClickListener{
            val activity = context as Activity
            val token = TokenUtil.getTokenByActivity(activity)
            val itemService = ItemsService(token)

            // Create the DTO of the item which has been changed to send it to the server
            val itemToUpdate = UpdateItemDto(
                id = currentItem.id,
                name = currentItem.name,
                description = currentItem.description,
                bought = holder.checkbox.isChecked.toString(),
            )

            // Starts the delete request from the server
            CoroutineScope(Dispatchers.IO).launch {
                itemService.updateItem(itemToUpdate)
                withContext(Dispatchers.Main) {
                    try {
                    } catch (e: HttpException) {
                        Log.e("HTTP", "Exception: Shopping Mode Recycler update: ${e.message}")
                    } catch (e: Throwable) {
                        Log.e("Throwable", "Exception: Shopping Mode Recycler update: ${e.toString()}")
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return featureList.size
    }

    // bind the different elements so they can be accessed
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title_text: TextView = itemView.findViewById(R.id.item_on_shopping_mode_name)
        val description: TextView = itemView.findViewById(R.id.item_on_shopping_mode_details)
        val owner: TextView = itemView.findViewById(R.id.item_on_shopping_mode_owner)
        val listName: TextView = itemView.findViewById(R.id.item_on_shopping_mode_list)
        val checkbox: CheckBox = itemView.findViewById(R.id.item_on_shopping_mode_checked)
    }
}