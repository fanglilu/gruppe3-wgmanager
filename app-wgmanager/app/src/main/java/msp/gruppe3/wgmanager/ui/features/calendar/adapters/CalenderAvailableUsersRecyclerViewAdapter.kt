package msp.gruppe3.wgmanager.ui.features.calendar.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.models.dtos.ReducedUserDto
import msp.gruppe3.wgmanager.models.dtos.RoleUserDto


class CalenderAvailableUsersRecyclerViewAdapter(
    private val context: Context,
    private val users: List<RoleUserDto>,
    private val selectedUsers: List<ReducedUserDto>,
    private val handler: (Set<ReducedUserDto>) -> Unit
) :
    RecyclerView.Adapter<CalenderAvailableUsersRecyclerViewAdapter.MyViewHolder>() {

    private var checkedUsers: List<ReducedUserDto> = selectedUsers

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_list_item, parent, false)
        Log.e("Available Adapter", "Adapter initialized with: $users")
        checkedUsers = selectedUsers
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = ReducedUserDto(users[position].id, users[position].name)

        if (checkedUsers.contains(currentItem)) println("yes") else println("no")
        if (!checkedUsers.contains(currentItem)) {
            holder.addButton.text = "+"
            //holder.itemView.setBackgroundColor(Color.GRAY)
        } else {
            holder.addButton.text = "x"
            holder.itemView.setBackgroundColor(Color.GREEN)
        }
        holder.userTextView.text = currentItem.name
        holder.addButton.setOnClickListener {
            if (!checkedUsers.contains(currentItem)) {
                checkedUsers = checkedUsers.plus(currentItem)
                holder.addButton.text = "x"
                holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.rounded_corners_background_wg_home_green)
            } else {
                checkedUsers = checkedUsers.minus(currentItem)
                holder.addButton.text = "+"
                holder.itemView.background = ContextCompat.getDrawable(context, R.drawable.rounded_corners_background_wg_home)
            }

            handler(checkedUsers.toSet())

        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userTextView: TextView = itemView.findViewById(R.id.username)
        val addButton: Button = itemView.findViewById(R.id.addUserButton)
    }


}