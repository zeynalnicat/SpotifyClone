package com.example.spotifyclone.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.databinding.ArtistsListviewBinding
import com.example.spotifyclone.model.artist.Artist
import com.example.spotifyclone.ui.fragments.choose_artist.ChooseArtist

class ChooseArtistAdapter(val fragment: ChooseArtist) :
    RecyclerView.Adapter<ChooseArtistAdapter.ViewHolder>() {
    private val selectedArtists = mutableListOf<Artist>()

    private val diffCallBack = object : DiffUtil.ItemCallback<Artist>() {
        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem == newItem
        }
    }

    private val diffUtil = AsyncListDiffer(this, diffCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ArtistsListviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }

    inner class ViewHolder(private val binding: ArtistsListviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current: Artist) {
            binding.txtArtistName.text = current.name
            Glide.with(binding.root)
                .load(current.images?.get(0)?.url)
                .into(binding.imgArtist)

            var isClicked = false
            itemView.setOnClickListener {
                isClicked = !isClicked
                if (isClicked) {
                    binding.imgChecked.visibility = View.VISIBLE
                    selectedArtists.add(current)
                    fragment.updateButtonVisibility(selectedArtists.size)
                } else {
                    binding.imgChecked.visibility = View.GONE
                    selectedArtists.remove(current)
                    fragment.updateButtonVisibility(selectedArtists.size)

                }

            }

        }
    }

    fun submitList(list: List<Artist>) {
        diffUtil.submitList(list)
    }

    fun getSelectedArtists(): MutableList<Artist> {
        return selectedArtists
    }
}