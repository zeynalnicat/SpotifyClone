package com.example.spotifyclone.ui.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ItemLibraryAlbumBinding

class LikedSongsAdapter(
    private val setBottom: (com.example.spotifyclone.domain.model.dto.LikedSongs) -> Unit = {},
    private val setMusicLayout: (Int) -> Unit = {},
    private val saveSharedPreference: (key: String, value: String) -> Unit,
    private val saveSharedPreferenceBool: (value: Boolean) -> Unit = {},
    private val isInSP: (value: String) -> Boolean = { false },
) :
    RecyclerView.Adapter<LikedSongsAdapter.ViewHolder>() {


    private val diffCallBack = object : DiffUtil.ItemCallback<com.example.spotifyclone.domain.model.dto.LikedSongs>() {
        override fun areItemsTheSame(
            oldItem: com.example.spotifyclone.domain.model.dto.LikedSongs,
            newItem: com.example.spotifyclone.domain.model.dto.LikedSongs
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: com.example.spotifyclone.domain.model.dto.LikedSongs,
            newItem: com.example.spotifyclone.domain.model.dto.LikedSongs
        ): Boolean {
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
        fun bind(track: com.example.spotifyclone.domain.model.dto.LikedSongs) {


            if (track.isTopTracks) {
                binding.imgMore.visibility = View.GONE
                val layoutParams = binding.imgAlbum.layoutParams
                val desiredHeightInDp = 56
                val layoutTxt = binding.txtAlbumName.layoutParams as ConstraintLayout.LayoutParams
                layoutTxt.verticalBias = 0.3F
                binding.txtAlbumName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                binding.txtAlbumName.layoutParams = layoutTxt
                binding.txtAlbumName.setTextAppearance(R.style.BoldText)
                val displayMetrics = binding.root.resources.displayMetrics
                val pixels = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    desiredHeightInDp.toFloat(),
                    displayMetrics
                ).toInt()
                layoutParams.height = pixels
                layoutParams.width = pixels
                binding.album.visibility = View.GONE
                binding.container.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.filled_gray
                    )
                )
            } else if (track.isFromGenre) {
                val layoutParams = binding.imgAlbum.layoutParams
                val desiredHeightInDp = 64
                val displayMetrics = binding.root.resources.displayMetrics
                val pixels = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    desiredHeightInDp.toFloat(),
                    displayMetrics
                ).toInt()
                layoutParams.height = pixels
                layoutParams.width = pixels
                binding.imgMore.visibility = View.GONE
                binding.container.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.filled_gray
                    )
                )
            } else {
                binding.imgMore.visibility = View.VISIBLE
                binding.container.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.primary
                    )
                )
            }
            binding.album.text = track.artist


            track.isPlayed = isInSP(track.name)
            binding.txtAlbumName.text = track.name
            Glide.with(binding.root)
                .load(track.imgUri)
                .into(binding.imgAlbum)


            binding.txtAlbumName.setTextColor(
                if (track.isPlayed) ContextCompat.getColor(
                    itemView.context,
                    R.color.green
                ) else ContextCompat.getColor(itemView.context, R.color.white)
            )

            itemView.setOnClickListener {
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
                saveSharedPreference("PlayingMusicImg", track.imgUri)
                saveSharedPreference("PlayingMusic", track.name)
                saveSharedPreference("PlayingMusicArtist", track.artist)
                saveSharedPreference("PlayingMusicUri", track.uri)
                saveSharedPreferenceBool(true)
                track.isPlayed = true
                notifyDataSetChanged()
                setMusicLayout(layoutPosition)

            }
            binding.imgMore.setOnClickListener {

                setBottom(track)
            }

        }
    }

    fun submitList(list: List<com.example.spotifyclone.domain.model.dto.LikedSongs>) {
        diffUtil.submitList(list)
    }

}