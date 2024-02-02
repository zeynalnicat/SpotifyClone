package com.example.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.currentComposer
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ItemAlbumTracksBinding
import com.example.spotifyclone.model.album.singlealbum.Item
import com.example.spotifyclone.model.album.singlealbum.Tracks
import com.example.spotifyclone.model.pseudo_models.MusicItem

class SingleAlbumTracksAdapter(
    private val img: String,
    private val setMusicLayout: () -> Unit,
    private val saveSharedPreference: (key: String, value: String) -> Unit,
    private val saveSharedPreferenceBool : (value:Boolean) -> Unit,
    private val isInSP: (value: String) -> Boolean,
    private val setBottom : (img:String , track:String , artist:String) -> Unit
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


            init {
                binding.imgMore.setOnClickListener {

                }
            }
        fun bind(track: MusicItem) {
            binding.txtTrackName.text = track.item.name
            binding.txtAlbumName.text = track.item.artists[0].name
            Glide.with(binding.root)
                .load(img)
                .into(binding.imgAlbum)

            track.isPlayed = isInSP(track.item.name)
            binding.txtTrackName.setTextColor(
                if (track.isPlayed) ContextCompat.getColor(
                    itemView.context,
                    R.color.green
                ) else ContextCompat.getColor(itemView.context, R.color.white)
            )
            binding.musicIcon.visibility = if (track.isPlayed) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                saveSharedPreference("PlayingMusic",track.item.name)
                saveSharedPreference("PlayingMusicArtist",track.item.artists[0].name)
                saveSharedPreference("PlayingMusicImg",img)
                saveSharedPreferenceBool(true)
                track.isPlayed = true
                setMusicLayout()
                notifyDataSetChanged()
            }

            binding.imgMore.setOnClickListener {
                setBottom(img,track.item.name,track.item.artists[0].name)
            }


        }
    }

    fun submitList(tracks: List<MusicItem>) {
        diffUtil.submitList(tracks)
    }
}