package msp.gruppe3.wgmanager.ui.features.shoppinglist.recycler_view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.models.ShoppingListEntry
import msp.gruppe3.wgmanager.services.ListService
import msp.gruppe3.wgmanager.ui.features.shoppinglist.ShoppingListViewModel
import retrofit2.HttpException
import java.util.*

// Recycler adapter for the lists; Called from ShowShoppingListFragment

class MyListSelectAdapter(private val activity: FragmentActivity, private val featureList: List<ShoppingListEntry>, private val context: Context, private val navController: NavController, private val requireActivity: ViewModelStoreOwner) : RecyclerView.Adapter<MyListSelectAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_with_description, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        // Set the text of the recycler to the correct one from the current item
        val currentItem = featureList[position]
        holder.title_text.text = currentItem.name
        if (currentItem.numItems == 1){
            holder.no_entries.text = context.getString(R.string.item_on_list, currentItem.numItems)
        } else {
            holder.no_entries.text = context.getString(R.string.items_on_list, currentItem.numItems)
        }
        holder.owner.text = context.getString(R.string.list_creator_name, currentItem.creator)
        if (currentItem.private){
            holder.private_lock.visibility = View.VISIBLE
        } else {
            holder.private_lock.visibility = View.INVISIBLE
        }

        // Change to the item view (ShowItemFragment) if the chevron button is clicked
        holder.delete_layout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("listSelected", currentItem.id)
            navController.navigate(R.id.showItemFragment, bundle)
        }

        // Listener for the long click on an item which activates the delete mode
        holder.delete_layout.setOnLongClickListener{
            // When the delete mode is active change the visibilities of the button to delete the list
            if (holder.delete_button.visibility == View.INVISIBLE){
                holder.delete_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.semi_transparent_Black))
                holder.delete_button.visibility = View.VISIBLE
                // If the delete mode is already active, change the visibility back so the delete mode is deactivated
            } else {
                holder.delete_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                holder.delete_button.visibility = View.INVISIBLE
            }
            true
        }

        // Detect when the button to delete an item is clicked
        holder.delete_button.setOnClickListener{
            holder.delete_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            holder.delete_button.visibility = View.INVISIBLE

            // Starts the delete request from the server
            val activity = context as Activity
            val token = TokenUtil.getTokenByActivity(activity)
            val listService = ListService(token)
            CoroutineScope(Dispatchers.IO).launch {
                val response =  listService.deleteList(UUID.fromString(currentItem.id))
                withContext(Dispatchers.Main) {
                    try {
                        val listViewModel = ViewModelProvider(requireActivity)[ShoppingListViewModel::class.java]
                        val index = listViewModel.shoppingLists.value!!.indexOf(listViewModel.shoppingLists.value!!.filter { it.id == currentItem.id }[0])
                        listViewModel.shoppingLists.value =  listViewModel.shoppingLists.value!!.toMutableList().apply {
                            removeAt(index)
                        }.toList()
                    } catch (e: HttpException) {
                        Log.e("HTTP", "Exception: List Recycler delete: ${e.message}")
                    } catch (e: Throwable) {
                        Log.e("Throwable", "Exception: List Recycler delete: ${e.toString()}")
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
        val title_text: TextView = itemView.findViewById(R.id.shoppingListEntryName)
        val no_entries: TextView = itemView.findViewById(R.id.shoppingListEntryNumOfItems)
        val owner: TextView = itemView.findViewById(R.id.shoppingListEntryOwner)
        val chevron_Button: Button = itemView.findViewById(R.id.shoppingListEntryDetailButton)
        val delete_layout: RelativeLayout = itemView.findViewById(R.id.shopping_list_delete)
        val delete_button: Button = itemView.findViewById(R.id.shopping_list_button_delete)
        val private_lock: Button = itemView.findViewById(R.id.shopping_list_entry_private)
        val list: RelativeLayout = itemView.findViewById(R.id.shopping_list_holder)
        //val total_cost: TextView = itemView.findViewById(R.id.finance_list_item_expense_name)
        //val owner: TextView = itemView.findViewById(R.id.finance_list_item_expense_payer)
    }
}
