package com.example.spotifyclone.ui.adapters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.databinding.ItemSearchCardBinding

class SearchCardAdapter(private val nav : (Bundle) -> Unit) : RecyclerView.Adapter<SearchCardAdapter.ViewHolder>() {

    val diffCallBack = object : DiffUtil.ItemCallback<com.example.spotifyclone.domain.model.dto.Category>() {
        override fun areItemsTheSame(oldItem: com.example.spotifyclone.domain.model.dto.Category, newItem: com.example.spotifyclone.domain.model.dto.Category): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: com.example.spotifyclone.domain.model.dto.Category, newItem: com.example.spotifyclone.domain.model.dto.Category): Boolean {
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

    fun submitList(list: List<com.example.spotifyclone.domain.model.dto.Category>) {
        diffUtil.submitList(list)
    }

    inner class ViewHolder(private val binding: ItemSearchCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current: com.example.spotifyclone.domain.model.dto.Category) {
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

            itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("category",current)
                nav(bundle)
            }

        }

    }
}