package com.example.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.model.SearchCard
import com.example.spotifyclone.databinding.ItemSearchCardBinding
import com.example.spotifyclone.model.categories.Item

class SearchCardAdapter : RecyclerView.Adapter<SearchCardAdapter.ViewHolder>() {

    val diffCallBack = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

    val diffUtil = AsyncListDiffer(this, diffCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemSearchCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }

    fun submitList(list: List<Item>) {
        diffUtil.submitList(list)
    }

    inner class ViewHolder(private val binding: ItemSearchCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current: Item) {
            binding.txtCardTitle.text = current.name
            Glide.with(binding.root)
                .load(current.icons[0].url)
                .into(binding.imgCard)

        }
    }
}