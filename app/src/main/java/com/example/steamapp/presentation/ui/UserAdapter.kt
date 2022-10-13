package com.example.steamapp.presentation.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.steamapp.UsersInfo
import com.example.steamapp.databinding.ErrorElemBinding
import com.example.steamapp.databinding.UserLstElemBinding

class UserViewHolder(
    private val binding: UserLstElemBinding,
    private val onUserElemClicked: (UsersInfo) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: UsersInfo) {
        with(binding) {
            avatarPreviewImgv.load(item.avatar)
            nickTxtv.text = item.nickName

            root.setOnClickListener {
                onUserElemClicked(item)
            }
        }
    }

}

class UserAdapter(
    context: Context,
    private val onUserElemClicked: (UsersInfo) -> Unit
) : ListAdapter<UsersInfo, RecyclerView.ViewHolder>(DIFF_UTIL) {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserViewHolder(
                binding = UserLstElemBinding.inflate(layoutInflater, parent, false),
                onUserElemClicked = onUserElemClicked
            )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        if (holder is UserViewHolder) {
            if (item !is UsersInfo) return
            holder.bind(item)
        }
    }

    companion object {

        private val DIFF_UTIL = object : DiffUtil.ItemCallback<UsersInfo>() {
            override fun areItemsTheSame(oldItem: UsersInfo, newItem: UsersInfo): Boolean {
                return oldItem.steamId == newItem.steamId
            }

            override fun areContentsTheSame(oldItem: UsersInfo, newItem: UsersInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}