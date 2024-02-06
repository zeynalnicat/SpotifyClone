package com.example.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ItemPlaylistViewBinding
import com.example.spotifyclone.model.dto.PlaylistModel

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    private val diffCall = object : DiffUtil.ItemCallback<PlaylistModel>() {
        override fun areItemsTheSame(oldItem: PlaylistModel, newItem: PlaylistModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PlaylistModel, newItem: PlaylistModel): Boolean {
            return oldItem == newItem
        }

    }

    private val diffUtil = AsyncListDiffer(this, diffCall)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ItemPlaylistViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }

    inner class ViewHolder(private val binding: ItemPlaylistViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: PlaylistModel) {
            binding.txtPlaylistName.text = playlist.name
            binding.txtCountMusic.text = playlist.countTrack.toString()

            if (playlist.img.isEmpty()) {
                binding.imgPlaylist.setImageResource(R.drawable.playlist_image)
            } else {
                Glide.with(binding.root)
                    .load(playlist.img)
                    .into(binding.imgPlaylist)
            }
        }
    }

    fun submitList(list: List<PlaylistModel>) {
        diffUtil.submitList(list)
    }
}