package com.nanda.post5_051.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.igclone.databinding.ItemStoryBinding

class StoryAdapter(private val stories: List<String>) : RecyclerView.Adapter<StoryAdapter.VH>() {
    inner class VH(val b: ItemStoryBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val name = if (position==0) "You" else "Friend ${position}"
        holder.b.tvStoryName.text = name
        // Simple placeholder, use glide if actual uri available
        Glide.with(holder.b.imgStory).load(android.R.drawable.sym_def_app_icon).into(holder.b.imgStory)
    }
    override fun getItemCount() = stories.size
}
