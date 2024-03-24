package com.example.spotifyclone.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.databinding.ArtistsListviewBinding

import com.example.spotifyclone.domain.model.artist.Artist

class ArtistAdapter(
    private val nav: (Bundle) -> Unit
) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<com.example.spotifyclone.domain.model.artist.Artist>() {
        override fun areItemsTheSame(oldItem: com.example.spotifyclone.domain.model.artist.Artist, newItem: com.example.spotifyclone.domain.model.artist.Artist): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: com.example.spotifyclone.domain.model.artist.Artist, newItem: com.example.spotifyclone.domain.model.artist.Artist): Boolean {
            return oldItem == newItem
        }

    }

    private val diffUtil = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ArtistsListviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }

    inner class ViewHolder(private val binding: ArtistsListviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current: com.example.spotifyclone.domain.model.artist.Artist) {
            Glide.with(binding.root)
                .load(current.images?.get(0)?.url)
                .into(binding.imgArtist)

            binding.txtArtistName.text = current.name
            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            params.rightMargin = 30
            binding.root.layoutParams = params
            itemView.setOnClickListener {
                val bundle = Bundle()
                val genres = current.genres?.joinToString { it + "" }
                bundle.putString("artistId", current.id)
                bundle.putString("img", current.images?.get(0)?.url)
                bundle.putString("popularity", current.popularity.toString())
                bundle.putString("followers", current.followers?.total.toString())
                bundle.putString("genres", genres)
                nav(bundle)
            }
        }
    }

    fun submitList(list: List<com.example.spotifyclone.domain.model.artist.Artist>) {
        diffUtil.submitList(list)
    }

}
