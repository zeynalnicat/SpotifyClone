package com.example.spotifyclone.ui.fragments.home.music.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ItemHomeMusicBinding
import com.example.spotifyclone.model.deezer.DeezerTrack
import com.example.spotifyclone.model.dto.LikedSongs

class TrackAdapter(
    private val setBottom: (LikedSongs) -> Unit = {},
    private val setMusicLayout: (Int) -> Unit = {},
    private val saveSharedPreference: (key: String, value: String) -> Unit,
    private val saveSharedPreferenceBool: (value: Boolean) -> Unit = {},
    private val isInSP: (value: String) -> Boolean = { false },
    private val isPlaying: () -> Boolean
) : RecyclerView.Adapter<TrackAdapter.ViewHolder>() {


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

            current.isPlayed = isInSP(current.title)

            Glide.with(binding.root)
                .load(current.album.cover_xl)
                .into(binding.imgTrack)

            Glide.with(binding.root)
                .load(current.artist.picture)
                .into(binding.imgAlbum)

            binding.imgPlay.setImageResource(if (isInSP(current.title) && isPlaying()) R.drawable.icon_music_view_pause else R.drawable.icon_music_view_resume)

            binding.txtTrack.setTextColor(
                if (current.isPlayed) ContextCompat.getColor(
                    itemView.context,
                    R.color.green
                ) else ContextCompat.getColor(itemView.context, R.color.white)
            )

            binding.txtDate.text = current.release_date.substring(0, 4)
            binding.txtArtistName.text = current.artist.name
            binding.txtType.text = current.type

            itemView.setOnClickListener {
                itemView.animate()
                    .scaleX(0.6f)
                    .scaleY(0.6f)
                    .setDuration(600)
                    .withEndAction {
                        itemView.scaleX = 1.0f
                        itemView.scaleY = 1.0f
                    }
                    .start()
                saveSharedPreference("PlayingMusicImg", current.album.cover_xl)
                saveSharedPreference("PlayingMusic", current.title)
                saveSharedPreference("PlayingMusicArtist", current.title)
                saveSharedPreference("PlayingMusicUri", current.preview)
                saveSharedPreferenceBool(true)
                current.isPlayed = true
                notifyDataSetChanged()
                setMusicLayout(layoutPosition)
            }
        }

    }

    fun submitList(list: List<DeezerTrack>) {
        diffUtil.submitList(list)
    }
}