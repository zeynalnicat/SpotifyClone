package com.example.spotifyclone.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.spotifyclone.databinding.TrackListviewBinding
import com.example.spotifyclone.model.dto.Album

class AlbumAdapter(private val nav: (Bundle) -> Unit) :
    RecyclerView.Adapter<AlbumAdapter.TrackAdapterHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
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
        fun bind(current: Album) {
            Glide.with(binding.root)
                .load(current.coverImg)
                .into(binding.imgArtist)

            binding.txtArtistName.text = current.name
            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            params.rightMargin = 30
            binding.root.layoutParams = params
            itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("album",current)
                nav(bundle)
            }
        }
    }

    fun submitList(list: List<Album>) {
        diffUtil.submitList(list)
    }

}