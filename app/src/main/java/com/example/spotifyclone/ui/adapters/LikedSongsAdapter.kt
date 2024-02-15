package com.example.spotifyclone.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ItemLibraryAlbumBinding
import com.example.spotifyclone.network.db.likedsongs.LikedSongsEntity
import com.example.spotifyclone.model.dto.LikedSongs

class LikedSongsAdapter() : RecyclerView.Adapter<LikedSongsAdapter.ViewHolder>() {


    private val diffCallBack = object : DiffUtil.ItemCallback<LikedSongs>() {
        override fun areItemsTheSame(
            oldItem: LikedSongs,
            newItem: LikedSongs
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: LikedSongs,
            newItem: LikedSongs
        ): Boolean {
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
        fun bind(track: LikedSongs) {
            binding.cardView.setCardBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.txt_gray
                )
            )
            binding.txtAlbumName.text = track.name
            Glide.with(binding.root)
                .load(track.imgUri)
                .into(binding.imgAlbum)

            binding.album.text = track.artist
        }
    }

    fun submitList(list: List<LikedSongs>) {
        diffUtil.submitList(list)
    }

}