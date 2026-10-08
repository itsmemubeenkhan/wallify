package com.retrytech.ledgeapp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.activity.ViewWallpapersActivity
import com.retrytech.ledgeapp.databinding.ItemHomeImageByCatBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.Global
import com.retrytech.ledgeapp.utils.SessionManager
import java.util.Locale

class WallpaperAdapter() : RecyclerView.Adapter<WallpaperAdapter.ItemHolder>() {

    var mList: MutableList<SettingData.WallpapersItem> = mutableListOf()
    var favList: ArrayList<Int> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_image_by_cat, parent, false)
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
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun loadMore(data: MutableList<SettingData.WallpapersItem>) {
        if (data.isEmpty()) return
        val start = mList.size
        mList.addAll(data)
        notifyItemRangeInserted(start, data.size)
    }

    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }

    fun refreshData(newList: List<Int>) {

        val oldListOfFav = ArrayList<Int>()
        oldListOfFav.addAll(favList)
        favList = ArrayList()
        favList.addAll(newList)
        Log.i("TAG", "refreshData: " + oldListOfFav)
        Log.i("TAG", "refreshData: fav list " + favList)

        for (i in 0..<mList.size) {
            if (oldListOfFav.contains(mList[i].id) || favList.contains(mList[i].id)) {
                Log.i("TAG", "refreshData: " + mList[i].id)
                notifyItemChanged(i)

            }

        }
    }

    inner class ItemHolder
        (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemHomeImageByCatBinding
        var sessionManager: SessionManager

        init {
            binding = DataBindingUtil.bind(itemView)!!
            sessionManager = SessionManager(itemView.context)

        }

        fun setModal(position: Int) {


            val item = mList[position]


            binding.isFav = favList.contains(item.id)
            binding.tvDownloadCount.text = formatDownloads(item.downloadCount ?: 0)





            binding.icFavYes.setOnClickListener(View.OnClickListener {

                if (favList.contains(item.id)) {
                    favList.remove(item.id!!)
                } else {
                    favList.add(item.id!!)
                }
                sessionManager.saveStringValue(
                    Const.Key.favourites,
                    Global.listOfIntegerToString(favList)
                )
                notifyItemChanged(position)

            })


            binding.root.setOnClickListener(View.OnClickListener {

                var intent = Intent(itemView.context, ViewWallpapersActivity::class.java)

                intent.putExtra(Const.Key.dataList, Gson().toJson(mList))
                intent.putExtra(Const.Key.position, position)
                itemView.context.startActivity(intent)

            })


            binding.model = item
        }

        private fun formatDownloads(value: Int): String {
            return when {
                value >= 1_000_000 -> String.format(Locale.US, "%.1fM downloads", value / 1_000_000f)
                value >= 1_000 -> String.format(Locale.US, "%.1fK downloads", value / 1_000f)
                else -> "$value downloads"
            }
        }
    }
}
