package com.example.spotifyclone.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ItemLibraryAlbumBinding
import com.example.spotifyclone.domain.model.dto.Album
import com.example.spotifyclone.domain.model.dto.LibraryAlbum

class LibraryAlbumAdapter(private val nav: (Bundle) -> Unit = {}) :
    RecyclerView.Adapter<LibraryAlbumAdapter.ViewHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<com.example.spotifyclone.domain.model.dto.Album>() {
        override fun areItemsTheSame(oldItem: com.example.spotifyclone.domain.model.dto.Album, newItem: com.example.spotifyclone.domain.model.dto.Album): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: com.example.spotifyclone.domain.model.dto.Album, newItem: com.example.spotifyclone.domain.model.dto.Album): Boolean {
            return oldItem == newItem
        }

    }

    private val diffUtil = AsyncListDiffer(this, diffCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ItemLibraryAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }

    inner class ViewHolder(private val binding: ItemLibraryAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(album: com.example.spotifyclone.domain.model.dto.Album) {

            if (!album.isLibrary) {
                binding.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.primary
                    )
                )
            }
            Glide.with(binding.root)
                .load(album.coverImg)
                .into(binding.imgAlbum)
            binding.txtAlbumName.text = album.name


            itemView.setOnClickListener {
                itemView.animate()
                    .scaleX(0.6f)
                    .scaleY(0.6f)
                    .setDuration(100)
                    .withEndAction {
                        itemView.scaleX = 1.0f
                        itemView.scaleY = 1.0f
                    }
                    .start()
                val bundle = Bundle()
                bundle.putSerializable("album", album)
                nav(bundle)
            }

        }
    }

    fun submitList(list: List<com.example.spotifyclone.domain.model.dto.Album>) {
        diffUtil.submitList(list)
    }


}