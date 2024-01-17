package com.example.spotifyclone.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.databinding.ItemLibraryAlbumBinding
import com.example.spotifyclone.model.album.newrelease.Item
import com.example.spotifyclone.model.album.popularalbums.Album
import com.example.spotifyclone.model.pseudo_models.LibraryAlbum

class LibraryAlbumAdapter(private val nav :(Bundle)->Unit) : RecyclerView.Adapter<LibraryAlbumAdapter.ViewHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<LibraryAlbum>() {
        override fun areItemsTheSame(oldItem: LibraryAlbum, newItem: LibraryAlbum): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: LibraryAlbum, newItem: LibraryAlbum): Boolean {
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
        fun bind(album: LibraryAlbum) {
            Glide.with(binding.root)
                .load(album.albumImg)
                .into(binding.imgAlbum)
            binding.txtAlbumName.text = album.albumName

            itemView.setOnClickListener {
                itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("albumId",album.albumId)
                    nav(bundle)
                }
            }
        }
    }

    fun submitList(list: List<LibraryAlbum>) {
        diffUtil.submitList(list)
    }


}