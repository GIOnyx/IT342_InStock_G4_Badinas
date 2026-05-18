package com.example.instock.features.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.instock.databinding.ItemAdminUserBinding

class AdminUserAdapter(private var users: List<UserSummaryDTO>) :
    RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemAdminUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemAdminUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        with(holder.binding) {
            tvUserName.text = user.fullName ?: "Unnamed User"
            tvUserEmail.text = user.email ?: "No email"
            tvUserId.text = "ID: ${user.id}"

            // Role badge — mirror web platform color semantics
            val role = user.role ?: "USER"
            tvUserRole.text = role
            if (role == "ADMIN") {
                tvUserRole.setBackgroundColor(android.graphics.Color.parseColor("#FFF3E0"))
                tvUserRole.setTextColor(android.graphics.Color.parseColor("#E65100"))
            } else {
                tvUserRole.setBackgroundColor(android.graphics.Color.parseColor("#E0F2F1"))
                tvUserRole.setTextColor(android.graphics.Color.parseColor("#00796B"))
            }
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<UserSummaryDTO>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
