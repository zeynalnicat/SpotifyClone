package com.example.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

import com.example.spotifyclone.databinding.ArtistsListviewBinding
import com.example.spotifyclone.databinding.RecentTracksListviewBinding
import com.example.spotifyclone.model.PlayedTracks

class RecentlyPlayedAdapter(
    private val recentlyList: MutableList<PlayedTracks>,
    private val nav: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_SPECIAL = 1
    private val VIEW_SIMPLE = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_SIMPLE) {
            val view =
                ArtistsListviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AdapterHolder(view)
        } else {
            val view = RecentTracksListviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            SpecialAdapterHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return recentlyList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val current = recentlyList[position]
        when (holder) {
            is AdapterHolder -> holder.bind(current)
            is SpecialAdapterHolder -> holder.bind(current)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (recentlyList[position].isSpecial) VIEW_SPECIAL else VIEW_SIMPLE
    }

    inner class AdapterHolder(private val binding: ArtistsListviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(current: PlayedTracks) {
            binding.txtArtistName.text = current.name
            binding.imgArtist.setImageResource(current.imgSrc)
            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            params.rightMargin = 30
            binding.root.layoutParams = params
            itemView.setOnClickListener {
                nav()
            }
        }
    }

    inner class SpecialAdapterHolder(private val binding: RecentTracksListviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(current: PlayedTracks) {
            binding.txtArtistName.text = current.name
            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            params.rightMargin = 30
            binding.root.layoutParams = params
            binding.imgArtist.setImageResource(current.imgSrc)
            itemView.setOnClickListener {
                nav()
            }
        }
    }
}
