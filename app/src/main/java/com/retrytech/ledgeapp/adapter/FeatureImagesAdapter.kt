package com.retrytech.ledgeapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.activity.ViewWallpapersActivity
import com.retrytech.ledgeapp.databinding.ItemFeaturedBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const

class FeatureImagesAdapter : RecyclerView.Adapter<FeatureImagesAdapter.ItemHolder>() {
    var lastSelected = 0
    var currantSelected = 0
    var mList: List<SettingData.WallpapersItem> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_featured, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItemHolder, position: Int
    ) {
        holder.setModal(position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list: List<SettingData.WallpapersItem>) {
        mList = list
        notifyDataSetChanged()
    }

    fun scrollToPos(pos: Int) {
        lastSelected = currantSelected
        currantSelected = pos
        notifyItemChanged(currantSelected)
        notifyItemChanged(lastSelected)
    }

    inner class ItemHolder
        (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemFeaturedBinding

        init {
            binding = DataBindingUtil.bind(itemView)!!
        }

        fun setModal(position: Int) {
            val item = mList[position]
            binding.model = item


            binding.root.setOnClickListener {
                var intent = Intent(itemView.context, ViewWallpapersActivity::class.java)

                intent.putExtra(Const.Key.dataList, Gson().toJson(mList))
                intent.putExtra(Const.Key.position, position)
                itemView.context.startActivity(intent)
            }

        }
    }
}