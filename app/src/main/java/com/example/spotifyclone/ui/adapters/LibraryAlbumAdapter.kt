package com.example.spotifyclone.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.databinding.ItemLibraryAlbumBinding
import com.example.spotifyclone.model.dto.Album
import com.example.spotifyclone.model.dto.LibraryAlbum

class LibraryAlbumAdapter(private val nav :(Bundle)->Unit={}) : RecyclerView.Adapter<LibraryAlbumAdapter.ViewHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }

    }

    private val diffUtil = AsyncListDiffer(this, diffCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ItemLibraryAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }

    inner class ViewHolder(private val binding: ItemLibraryAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album) {
            Glide.with(binding.root)
                .load(album.coverImg)
                .into(binding.imgAlbum)
            binding.txtAlbumName.text = album.name

            itemView.setOnClickListener {
                itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putSerializable("album",album)
                    nav(bundle)
                }
            }
        }
    }

    fun submitList(list: List<Album>) {
        diffUtil.submitList(list)
    }


}