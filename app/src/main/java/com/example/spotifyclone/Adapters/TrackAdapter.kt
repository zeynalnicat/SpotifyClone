package com.example.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.spotifyclone.databinding.TrackListviewBinding
import com.example.spotifyclone.model.Tracks

class TrackAdapter(private val tracks: MutableList<Tracks>) :
    RecyclerView.Adapter<TrackAdapter.TrackAdapterHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackAdapterHolder {
        val binding =
            TrackListviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackAdapterHolder(binding)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackAdapterHolder, position: Int) {
        val current = tracks[position]
        return holder.bind(current)
    }

    inner class TrackAdapterHolder(private val binding: TrackListviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current: Tracks) {
            binding.imgArtist.setImageResource(current.imgSrc)
            binding.txtArtistName.text = current.name
            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            params.rightMargin = 30
            binding.root.layoutParams = params
        }
    }

}