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
import com.example.spotifyclone.databinding.ItemAlbumTracksBinding
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.musicplayer.MusicPlayer

class SingleAlbumTracksAdapter(
    private val img: String,
    private val setMusicLayout: (Int) -> Unit,
    private val saveSharedPreference: (key: String, value: String) -> Unit,
    private val saveSharedPreferenceBool: (value: Boolean) -> Unit,
    private val isInSP: (value: String) -> Boolean,
    private val setBottom: (MusicItem, String) -> Unit
) : RecyclerView.Adapter<SingleAlbumTracksAdapter.ViewHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<MusicItem>() {
        override fun areItemsTheSame(oldItem: MusicItem, newItem: MusicItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MusicItem, newItem: MusicItem): Boolean {
            return oldItem == newItem
        }
    }

    private val diffUtil = AsyncListDiffer(this, diffCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ItemAlbumTracksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }


    inner class ViewHolder(private val binding: ItemAlbumTracksBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: MusicItem) {
            binding.txtTrackName.text = track.name
            binding.txtAlbumName.text = track.artist
            Glide.with(binding.root)
                .load(img)
                .into(binding.imgAlbum)

            track.isPlayed = isInSP(track.name)
            binding.txtTrackName.setTextColor(
                if (track.isPlayed) ContextCompat.getColor(
                    itemView.context,
                    R.color.green
                ) else ContextCompat.getColor(itemView.context, R.color.white)
            )
            binding.musicIcon.visibility = if (track.isPlayed) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                saveSharedPreference("PlayingMusicImg", img)
                saveSharedPreference("PlayingMusic",track.name)
                saveSharedPreference("PlayingMusicArtist",track.artist)
                saveSharedPreference("PlayingMusicUri",track.trackUri)
                saveSharedPreferenceBool(true)
                track.isPlayed = true
                notifyDataSetChanged()
                setMusicLayout(layoutPosition)

            }
            binding.imgMore.setOnClickListener {
                val musicItem =
                    MusicItem(track.artist, track.id, track.name, track.trackUri, img)
                setBottom(musicItem, track.id)
            }
        }
    }

    fun submitList(tracks: List<MusicItem>) {
        diffUtil.submitList(tracks)
    }

    fun getTracks(): List<MusicItem> {
        return diffUtil.currentList
    }
}