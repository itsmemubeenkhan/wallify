package com.retrytech.ledgeapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.retrytech.ledgeapp.R

class StatusSaverAdapter(
    private val onOpenClick: (StatusItem) -> Unit
) : RecyclerView.Adapter<StatusSaverAdapter.ItemHolder>() {

    private val items = ArrayList<StatusItem>()

    fun submitData(data: List<StatusItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_status_saver, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgStatus: ImageView = itemView.findViewById(R.id.img_status)
        private val tvVideo: TextView = itemView.findViewById(R.id.tv_video)
        private val btnSave: TextView = itemView.findViewById(R.id.btn_save)

        fun bind(item: StatusItem) {
            Glide.with(itemView.context).load(item.uri).into(imgStatus)
            tvVideo.visibility = if (item.isVideo) View.VISIBLE else View.GONE
            btnSave.text = itemView.context.getString(R.string.open)
            btnSave.setOnClickListener { onOpenClick(item) }
            itemView.setOnClickListener { onOpenClick(item) }
        }
    }
}

data class StatusItem(
    val uri: Uri,
    val name: String,
    val mimeType: String,
    val isVideo: Boolean,
    val lastModified: Long
)
