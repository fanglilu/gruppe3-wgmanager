package msp.gruppe3.wgmanager.ui.features.calendar.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.models.dtos.ReducedUserDto
import msp.gruppe3.wgmanager.models.dtos.RoleUserDto


class CalenderSelectedUsersRecyclerViewAdapter(
    private val selectedUsers: List<ReducedUserDto>
) :
    RecyclerView.Adapter<CalenderSelectedUsersRecyclerViewAdapter.MyViewHolder>() {

    private lateinit var checkedUsers: List<RoleUserDto>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_list_item, parent, false)
        Log.e("Select Adapter", "Adapter initialized with: $selectedUsers")
        checkedUsers = mutableListOf()
        return MyViewHolder(itemView)
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = selectedUsers[position]

        holder.userTextView.text = currentItem.name
        holder.addUserButton.visibility = View.INVISIBLE
    }

    override fun getItemCount(): Int {
        return selectedUsers.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userTextView: TextView = itemView.findViewById(R.id.username)
        val addUserButton: Button = itemView.findViewById(R.id.addUserButton)
    }


}