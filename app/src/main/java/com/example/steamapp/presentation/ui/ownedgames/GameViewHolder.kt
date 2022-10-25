package com.example.steamapp.presentation.ui.ownedgames

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.steamapp.databinding.GameLstElemBinding
import com.example.steamapp.domain.model.Game

class GameViewHolder(private val binding: GameLstElemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Game) {
        with(binding) {
            logoPreviewImgv.load("http://media.steampowered.com/steamcommunity/public/images/apps/${item.appid}/${item.imgIconUrl}.jpg")
            nameTxtv.text = item.name
        }
    }
}