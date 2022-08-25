package com.example.steamapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.steamapp.databinding.UserLstElemBinding

class UserViewHolder(private val binding:UserLstElemBinding):RecyclerView.ViewHolder(binding.root){

    fun bind (item:PlayerInfo){
        with(binding){
            avatarPreviewImgv.load(item.avatarFull)
            nickTxtv.text=item.personaName
        }
    }

}

class UserAdapter (context:Context):ListAdapter<PlayerInfo,UserViewHolder>(DIFF_UTIL){

    private val layoutInflater=LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        return UserViewHolder(

            binding = UserLstElemBinding.inflate(layoutInflater,parent,false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        holder.bind(getItem(position))
    }

    companion object{

        private val DIFF_UTIL=object :DiffUtil.ItemCallback<PlayerInfo>(){

            override fun areItemsTheSame(oldItem: PlayerInfo, newItem: PlayerInfo): Boolean {

                return oldItem.personaName==newItem.personaName
            }

            override fun areContentsTheSame(oldItem: PlayerInfo, newItem: PlayerInfo): Boolean {

                //уточни тут усли будет много данных
                return oldItem==newItem
            }
        }
    }
}