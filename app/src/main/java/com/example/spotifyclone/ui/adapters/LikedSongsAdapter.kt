package com.example.spotifyclone.ui.adapters

import android.app.ActionBar.LayoutParams
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.text.font.Typeface
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ItemLibraryAlbumBinding

import com.example.spotifyclone.model.dto.LikedSongs
import com.example.spotifyclone.model.dto.MusicItem

class LikedSongsAdapter(
    private val setBottom: (LikedSongs) -> Unit={},
    private val setMusicLayout: (Int) -> Unit={},
    private val saveSharedPreference: (key: String, value: String) -> Unit,
    private val saveSharedPreferenceBool: (value: Boolean) -> Unit={},
    private val isInSP: (value: String) -> Boolean = {false},
) :
    RecyclerView.Adapter<LikedSongsAdapter.ViewHolder>() {


    private val diffCallBack = object : DiffUtil.ItemCallback<LikedSongs>() {
        override fun areItemsTheSame(
            oldItem: LikedSongs,
            newItem: LikedSongs
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: LikedSongs,
            newItem: LikedSongs
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
        fun bind(track: LikedSongs) {



            if(track.isTopTracks){
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
                binding.container.setBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.filled_gray))
            }
            else{
                binding.imgMore.visibility = View.VISIBLE
                binding.container.setBackgroundColor(ContextCompat.getColor(binding.root.context,R.color.primary))
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

    fun submitList(list: List<LikedSongs>) {
        diffUtil.submitList(list)
    }

}