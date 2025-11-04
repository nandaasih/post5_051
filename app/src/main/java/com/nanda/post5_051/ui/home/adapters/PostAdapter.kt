package com.nanda.post5_051.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.igclone.data.db.Post
import com.example.igclone.databinding.ItemPostBinding
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(
    private val items: MutableList<Post>,
    private val onMore: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.VH>() {

    inner class VH(val b: ItemPostBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.b.tvCaption.text = p.caption
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        holder.b.tvTime.text = sdf.format(Date(p.timestamp))
        Glide.with(holder.b.imgPost.context).load(p.imageUri).into(holder.b.imgPost)
        holder.b.btnMore.setOnClickListener { onMore(p) }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<Post>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
