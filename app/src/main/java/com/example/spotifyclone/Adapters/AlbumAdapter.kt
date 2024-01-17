package com.example.spotifyclone.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.spotifyclone.databinding.TrackListviewBinding
import com.example.spotifyclone.model.album.newrelease.Item

class AlbumAdapter(private val nav: (Bundle) -> Unit) :
    RecyclerView.Adapter<AlbumAdapter.TrackAdapterHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

    private val diffUtil = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackAdapterHolder {
        val binding =
            TrackListviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackAdapterHolder(binding)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: TrackAdapterHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }

    inner class TrackAdapterHolder(private val binding: TrackListviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current: Item) {
            Glide.with(binding.root)
                .load(current.images[0].url)
                .into(binding.imgArtist)

            binding.txtArtistName.text = current.name
            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            params.rightMargin = 30
            binding.root.layoutParams = params
            itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("albumId",current.id)
                nav(bundle)
            }
        }
    }

    fun submitList(list: List<Item>) {
        diffUtil.submitList(list)
    }

}