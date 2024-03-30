package com.example.spotifyclone.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyclone.databinding.ItemSettingsBinding

class SettingsAdapter(private val nav: (Int) -> Unit, private val logout: () -> Unit) :
    RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    private val diffCall = object : DiffUtil.ItemCallback<com.example.spotifyclone.domain.model.SettingItem>() {
        override fun areItemsTheSame(oldItem: com.example.spotifyclone.domain.model.SettingItem, newItem: com.example.spotifyclone.domain.model.SettingItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: com.example.spotifyclone.domain.model.SettingItem, newItem: com.example.spotifyclone.domain.model.SettingItem): Boolean {
            return oldItem == newItem
        }
    }

    private val diffUtil = AsyncListDiffer(this, diffCall)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemSettingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(diffUtil.currentList[position])
    }

    inner class ViewHolder(private val binding: ItemSettingsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current: com.example.spotifyclone.domain.model.SettingItem) {
            binding.txtSectionName.setText(current.name)
            if (current.canNavigate) {
                binding.cardView.setOnClickListener {
                    current.navTo?.let {
                        nav(it)
                    }

                }

            }

            if (current.isLogout) {
                binding.cardView.setOnClickListener {
                    logout()
                }
            }
        }
    }

    fun submitList(items: List<com.example.spotifyclone.domain.model.SettingItem>) {
        diffUtil.submitList(items)
    }
}