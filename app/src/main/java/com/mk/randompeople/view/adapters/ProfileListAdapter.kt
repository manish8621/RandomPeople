package com.mk.randompeople.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mk.randompeople.databinding.FragmentHomeBinding
import com.mk.randompeople.databinding.ProfileItemLayoutBinding
import com.mk.randompeople.model.network.Profile

class ProfileListAdapter:ListAdapter<Profile,ProfileListAdapter.ItemViewHolder>(DiffUtilItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder.from(parent)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(getItem(position))
    class ItemViewHolder private constructor(val binding: ProfileItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(profile: Profile){
            loadImage(binding.profileIv,profile.picture.thumbnail)
            "${profile.name.title}.${profile.name.first} ${profile.name.last}".also { binding.nameTv.text = it }
        }
        companion object{
            fun from(parent: ViewGroup):ItemViewHolder{
                return LayoutInflater.from(parent.context).let {
                    ItemViewHolder(ProfileItemLayoutBinding.inflate(it))
                }
            }
        }
    }
    class DiffUtilItemCallback:DiffUtil.ItemCallback<Profile>(){
        override fun areItemsTheSame(oldItem: Profile, newItem: Profile) = (oldItem==newItem)
        override fun areContentsTheSame(oldItem: Profile, newItem: Profile) = (oldItem==newItem)

    }
}