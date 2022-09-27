package com.example.steamapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.steamapp.databinding.ErrorElemBinding
import com.example.steamapp.databinding.UserLstElemBinding

class UserViewHolder(
    private val binding: UserLstElemBinding,
    private val onUserElemClicked: (InputItem.PlayerInfo) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: InputItem.PlayerInfo) {
        with(binding) {
            avatarPreviewImgv.load(item.avatarFull)
            nickTxtv.text = item.personaName

            root.setOnClickListener {
                onUserElemClicked(item)
            }
        }
    }

}

class ErrorViewHolder(
    binding: ErrorElemBinding,
    onTryAgainBtnClicked: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.tryAgainBtn.setOnClickListener {
            onTryAgainBtnClicked()
        }
    }
}

class UserAdapter(
    context: Context,
    private val onTryAgainBtnClicked: () -> Unit,
    private val onUserElemClicked: (InputItem.PlayerInfo) -> Unit
) : ListAdapter<InputItem, RecyclerView.ViewHolder>(DIFF_UTIL) {

    private val layoutInflater = LayoutInflater.from(context)

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is InputItem.PlayerInfo -> USER_ELEMENT
            InputItem.ErrorElement -> ERROR_ELEMENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            USER_ELEMENT -> UserViewHolder(
                binding = UserLstElemBinding.inflate(layoutInflater, parent, false),
                onUserElemClicked = onUserElemClicked
            )
            ERROR_ELEMENT -> ErrorViewHolder(
                binding = ErrorElemBinding.inflate(layoutInflater, parent, false),
                onTryAgainBtnClicked = onTryAgainBtnClicked

            )
            else -> error("Unknown input view")


        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = getItem(position)

        if (holder is UserViewHolder) {
            if (item !is InputItem.PlayerInfo) return
            holder.bind(item)
        }
    }

    companion object {

        private const val USER_ELEMENT = 0
        private const val ERROR_ELEMENT = 1

        private val DIFF_UTIL = object : DiffUtil.ItemCallback<InputItem>() {

            override fun areItemsTheSame(oldItem: InputItem, newItem: InputItem): Boolean {

                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: InputItem, newItem: InputItem): Boolean {

                //уточни тут усли будет много данных
                return oldItem == newItem
            }
        }
    }
}