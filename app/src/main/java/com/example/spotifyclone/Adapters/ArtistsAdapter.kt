package com.example.spotifyclone.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyclone.Artists
import com.example.spotifyclone.ChooseArtistPage.ChooseArtist

import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ArtistsListviewBinding
import com.example.spotifyclone.databinding.FragmentChooseArtistBinding
import java.util.Currency

class ArtistsAdapter(private val artistList: MutableList<Artists> ,  private val fragment: Fragment) :
    RecyclerView.Adapter<ArtistsAdapter.AdapterHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        val binding =
            ArtistsListviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterHolder(binding,parent)
    }

    override fun getItemCount(): Int {
        return artistList.size
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        val current = artistList[position]
        return holder.bind(current)
    }


    inner class AdapterHolder(private val binding: ArtistsListviewBinding,val parent: ViewGroup) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(current: Artists) {
            binding.imgChecked.visibility = if (current.isSelected) View.VISIBLE else View.INVISIBLE
            binding.txtArtistName.text = current.name
            binding.imgArtist.setImageResource(current.imgSrc)
            binding.root.setOnClickListener {
                current.isSelected = !current.isSelected
                if (current.isSelected) {
                    ChooseArtist.count ++
                    Log.d("CountDebug", "Count: ${ChooseArtist.count}")
                }else{
                    ChooseArtist.count--
                }

                if (fragment is ChooseArtist) {
                    fragment.updateButtonVisibility()
                }
                notifyDataSetChanged()
            }


        }


    }
}