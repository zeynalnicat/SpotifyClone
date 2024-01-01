package com.example.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyclone.model.SearchCard
import com.example.spotifyclone.databinding.ItemSearchCardBinding

class SearchCardAdapter : RecyclerView.Adapter<SearchCardAdapter.ViewHolder>() {

    val diffCallBack = object : DiffUtil.ItemCallback<SearchCard>() {
        override fun areItemsTheSame(oldItem: SearchCard, newItem: SearchCard): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: SearchCard, newItem: SearchCard): Boolean {
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

    fun submitList(list: List<SearchCard>) {
        diffUtil.submitList(list)
    }

    inner class ViewHolder(private val binding: ItemSearchCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current: SearchCard) {
            binding.txtCardTitle.text = current.title
            current.imgView?.let {
                binding.imgCard.setImageResource(it)
            }

        }
    }
}