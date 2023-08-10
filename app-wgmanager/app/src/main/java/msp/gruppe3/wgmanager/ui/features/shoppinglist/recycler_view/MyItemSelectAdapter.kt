package msp.gruppe3.wgmanager.ui.features.shoppinglist.recycler_view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.models.Item
import msp.gruppe3.wgmanager.models.dtos.DeleteItemDto
import msp.gruppe3.wgmanager.models.dtos.UpdateItemDto
import msp.gruppe3.wgmanager.services.ItemsService
import msp.gruppe3.wgmanager.ui.features.shoppinglist.ShoppingListViewModel
import msp.gruppe3.wgmanager.ui.features.shoppinglist.fragments.ShowItemDetailsFragment
import retrofit2.HttpException

// Recycler adapter for the items; Called from ShowItemFragment

class MyItemSelectAdapter(private val featureList: List<Item>, private val context: Context, private val navController: NavController, private val requireActivity: ViewModelStoreOwner) : RecyclerView.Adapter<MyItemSelectAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_items_with_checkbox_and_details, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        // Set the text of the current field in the recycler according to the current item
        val currentItem = featureList[position]
        holder.title_text.text = currentItem.name
        holder.description.text = currentItem.description
        holder.owner.text = context.getString(R.string.list_creator_name, currentItem.owner)
        holder.checkbox.isChecked = currentItem.isBought

        // Set click listener to recognize if the checkbox is clicked and update the bought status of the item
        holder.checkbox.setOnClickListener{
            val activity = context as Activity
            val token = TokenUtil.getTokenByActivity(activity)
            val itemService = ItemsService(token)

            // Create the DTO for the updated item
            val itemToUpdate = UpdateItemDto(
                id = currentItem.id,
                name = currentItem.name,
                description = currentItem.description,
                bought = holder.checkbox.isChecked.toString(),
            )

            // Starts the request from the server
            CoroutineScope(Dispatchers.IO).launch {
                itemService.updateItem(itemToUpdate)
                withContext(Dispatchers.Main) {
                    try {
                    } catch (e: HttpException) {
                        Log.e("HTTP", "Exception: Item Recycler update: ${e.message}")
                    } catch (e: Throwable) {
                        Log.e("Throwable", "Exception: Item Recycler update: ${e.toString()}")
                    }
                }
            }
        }

        // Listener for the long click on an item which activates the delete mode
        holder.delete_layout.setOnLongClickListener{
            // When the delete mode is active change the visibilities of the button to delete the item
            if (holder.delete_button.visibility == View.INVISIBLE) {
                holder.delete_layout.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.semi_transparent_Black
                    )
                )
                holder.delete_button.visibility = View.VISIBLE
                holder.checkbox.isClickable = false
                // If the delete mode is already active, change the visibility back so the delete mode is deactivated
            } else {
                holder.delete_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                holder.delete_button.visibility = View.INVISIBLE
                holder.checkbox.isClickable = true
            }
            true
        }

        // Detect when the button to delete an item is clicked
        holder.delete_button.setOnClickListener{
            // Exit the delete mode
            holder.delete_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            holder.delete_button.visibility = View.INVISIBLE
            holder.checkbox.isClickable = true

            // Create the DTO to delete the item
            val itemToDelete = DeleteItemDto(
                itemId = currentItem.id,
                listId = currentItem.listID
            )

            // Starts the delete request from the server
            val activity = context as Activity
            val token = TokenUtil.getTokenByActivity(activity)
            val itemService = ItemsService(token)
            CoroutineScope(Dispatchers.IO).launch {
                val response = itemService.deleteItem(itemToDelete)
                withContext(Dispatchers.Main) {
                    try {
                        if (response != null) {
                            val listViewModel = ViewModelProvider(requireActivity)[ShoppingListViewModel::class.java]
                            val index = listViewModel.items.value!!.indexOf(listViewModel.items.value!!.filter { it.id == itemToDelete.itemId }[0])
                            listViewModel.items.value =  listViewModel.items.value!!.toMutableList().apply {
                                removeAt(index)
                            }.toList()
                        }
                    } catch (e: HttpException) {
                        Log.e("HTTP", "Exception: Item Recycler delete: ${e.message}")
                    } catch (e: Throwable) {
                        Log.e("Throwable", "Exception: Item Recycler delete: ${e.toString()}")
                    }
                }
            }
        }

        // Enter the details screen (ShowItemDetailsFragment) if the layout is clicked
        holder.delete_layout.setOnClickListener {

            val bundle = Bundle()

            bundle.putString("currentItemId", currentItem.id.toString())
            bundle.putString("currentItemName", currentItem.name)
            bundle.putString("currentItemDescription", currentItem.description)
            bundle.putString("currentItemIsBought", currentItem.isBought.toString())
            bundle.putString("currentItemOwner", currentItem.owner)

            navController.navigate(R.id.showItemDetailsFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return featureList.size
    }

    // bind the different elements so they can be accessed
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title_text: TextView = itemView.findViewById(R.id.itemOnShoppingListTitle)
        val description: TextView = itemView.findViewById(R.id.itemOnShoppingListDetails)
        val owner: TextView = itemView.findViewById(R.id.itemOnShoppingListOwner)
        val checkbox: CheckBox = itemView.findViewById(R.id.itemOnShoppingChecked)
        val chevron_Button: Button = itemView.findViewById(R.id.itemOnShoppingListDetailsButton)
        val delete_layout: RelativeLayout = itemView.findViewById(R.id.shopping_list_items_delete)
        val delete_button: Button = itemView.findViewById(R.id.item_on_shopping_list_button_delete)
    }
}
