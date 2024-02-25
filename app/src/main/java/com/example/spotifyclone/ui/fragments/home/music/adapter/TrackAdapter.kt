package com.example.spotifyclone.ui.fragments.home.music.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.databinding.ItemHomeMusicBinding
import com.example.spotifyclone.model.deezer.DeezerTrack

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.ViewHolder>() {


    private val diffCallBack = object : DiffUtil.ItemCallback<DeezerTrack>() {
        override fun areItemsTheSame(oldItem: DeezerTrack, newItem: DeezerTrack): Boolean {
            return newItem === oldItem
        }

        override fun areContentsTheSame(oldItem: DeezerTrack, newItem: DeezerTrack): Boolean {
            return newItem == oldItem
        }

    }

    private val diffUtil = AsyncListDiffer(this, diffCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemHomeMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }

    inner class ViewHolder(private val binding: ItemHomeMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current: DeezerTrack) {
            binding.txtArtist.text = current.artist.name
            binding.txtTrack.text = current.title
            binding.txtTrackName.text = current.title

            Glide.with(binding.root)
                .load(current.album.cover_medium)
                .into(binding.imgTrack)

            Glide.with(binding.root)
                .load(current.artist.picture)
                .into(binding.imgAlbum)

            binding.txtDate.text = current.release_date.substring(0, 4)
            binding.txtArtistName.text = current.artist.name
            binding.txtType.text = current.type
        }
    }

    fun submitList(list: List<DeezerTrack>) {
        diffUtil.submitList(list)
    }
}