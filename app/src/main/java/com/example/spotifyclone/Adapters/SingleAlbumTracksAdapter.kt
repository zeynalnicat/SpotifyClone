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

class SingleAlbumTracksAdapter(private val img:String, private val setMusicLayout: ()->Unit,private val setMusicAttrs:(url:String,name:String)->Unit):RecyclerView.Adapter<SingleAlbumTracksAdapter.ViewHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem===newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
           return oldItem==newItem
        }
    }

    private val diffUtil = AsyncListDiffer(this,diffCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemAlbumTracksBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }

    inner class ViewHolder(private val binding:ItemAlbumTracksBinding):RecyclerView.ViewHolder(binding.root){
            fun bind(track:Item){
                    binding.txtTrackName.text = track.name
                    binding.txtAlbumName.text = track.artists[0].name
                    Glide.with(binding.root)
                        .load(img)
                        .into(binding.imgAlbum)

                itemView.setOnClickListener {
                    binding.musicIcon.visibility = View.VISIBLE
                    binding.txtTrackName.setTextColor(ContextCompat.getColor(itemView.context,R.color.green))
                    setMusicLayout()
                    setMusicAttrs(img,track.name)
                }
            }
    }

    fun submitList(tracks:List<Item>){
        diffUtil.submitList(tracks)
    }
}