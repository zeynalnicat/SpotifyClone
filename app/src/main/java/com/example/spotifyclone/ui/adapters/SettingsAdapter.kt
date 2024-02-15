package com.example.spotifyclone.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyclone.databinding.ItemSettingsBinding
import com.example.spotifyclone.model.SettingItem

class SettingsAdapter(private val nav:(Int)->Unit):RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    private val diffCall = object:DiffUtil.ItemCallback<SettingItem>(){
        override fun areItemsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
            return oldItem===newItem
        }

        override fun areContentsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
            return oldItem==newItem
        }
    }

    private val diffUtil = AsyncListDiffer(this,diffCall)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemSettingsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }

    inner class ViewHolder(private val binding:ItemSettingsBinding):RecyclerView.ViewHolder(binding.root){
           fun bind(current:SettingItem){
               binding.txtSectionName.text = current.name
               if(current.canNavigate){
                   binding.cardView.setOnClickListener{
                       nav(current.navTo!!)
                   }

               }
           }
    }

    fun submitList(items:List<SettingItem>){
        diffUtil.submitList(items)
    }
}