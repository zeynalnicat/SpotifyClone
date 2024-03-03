package com.example.spotifyclone.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.graphics.Color
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.databinding.ItemSearchCardBinding
import com.example.spotifyclone.model.dto.Category

class SearchCardAdapter : RecyclerView.Adapter<SearchCardAdapter.ViewHolder>() {

    val diffCallBack = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
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

    fun submitList(list: List<Category>) {
        diffUtil.submitList(list)
    }

    inner class ViewHolder(private val binding: ItemSearchCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current: Category) {
            val color = if (!current.color.isNullOrEmpty()) {
                Color.parseColor(current.color)
            } else {
                Color.WHITE
            }
            binding.cardView.setCardBackgroundColor(color)
            binding.txtCardTitle.text = current.name
            Glide.with(binding.root)
                .load(current.img)
                .into(binding.imgCard)

        }
    }
}