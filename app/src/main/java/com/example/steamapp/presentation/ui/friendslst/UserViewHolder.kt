package com.example.steamapp.presentation.ui.friendslst

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.steamapp.databinding.UserLstElemBinding
import com.example.steamapp.domain.model.UsersInfo

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