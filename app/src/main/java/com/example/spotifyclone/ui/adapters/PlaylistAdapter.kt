package com.example.spotifyclone.ui.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ItemPlaylistViewBinding
import com.example.spotifyclone.domain.model.dto.PlaylistModel
import com.example.spotifyclone.ui.fragments.playlist.AddPlaylistFragment
import kotlin.math.log

class PlaylistAdapter(
    private val nav: (Bundle) -> Unit?,
    private val setBottom: (com.example.spotifyclone.domain.model.dto.PlaylistModel) -> Unit?
) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    private val diffCall = object : DiffUtil.ItemCallback<com.example.spotifyclone.domain.model.dto.PlaylistModel>() {
        override fun areItemsTheSame(oldItem: com.example.spotifyclone.domain.model.dto.PlaylistModel, newItem: com.example.spotifyclone.domain.model.dto.PlaylistModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: com.example.spotifyclone.domain.model.dto.PlaylistModel, newItem: com.example.spotifyclone.domain.model.dto.PlaylistModel): Boolean {
            return oldItem == newItem
        }
    }

    private val diffUtil = AsyncListDiffer(this, diffCall)
    private val selectedPlaylists = mutableListOf<com.example.spotifyclone.domain.model.dto.PlaylistModel>()

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


        fun bind(playlist: com.example.spotifyclone.domain.model.dto.PlaylistModel) {
            if (playlist.isLibrary) {
                binding.checkBox.visibility = View.GONE
                itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("id", playlist.id)
                    nav(bundle)
                }
            } else {
                binding.checkBox.visibility = View.VISIBLE

            }

            binding.checkBox.isChecked = playlist.isSelected
            binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                val playlist = diffUtil.currentList[layoutPosition]
                playlist.isSelected = isChecked
                if (playlist.isSelected) {
                    selectedPlaylists.add(playlist)
                    AddPlaylistFragment.selectedPlaylists.postValue(selectedPlaylists)
                } else {
                    selectedPlaylists.remove(playlist)
                    AddPlaylistFragment.selectedPlaylists.postValue(selectedPlaylists)
                }
            }

            itemView.setOnLongClickListener {
                itemView.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(300)
                    .alpha(0.6f)
                    .withEndAction {
                        itemView.scaleX = 1.0f
                        itemView.scaleY = 1.0f
                        itemView.alpha = 1.0f
                    }
                    .start()
                setBottom(playlist)

                true
            }

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


    fun submitList(list: List<com.example.spotifyclone.domain.model.dto.PlaylistModel>) {
        diffUtil.submitList(list)
    }

}
