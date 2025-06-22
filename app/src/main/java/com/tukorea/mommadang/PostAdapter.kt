package com.tukorea.mommadang

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val postList: List<Triple<String, String, String>>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.itemTitle)
        val contentView: TextView = itemView.findViewById(R.id.itemContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val (title, content, author) = postList[position]
        holder.titleView.text = title
        holder.contentView.text = content

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PostDetailActivity::class.java).apply {
                putExtra("title", title)
                putExtra("content", content)
                putExtra("author", author) // 작성자 정보 전달
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = postList.size
}
