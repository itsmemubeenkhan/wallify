package com.retrytech.ledgeapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.islamkhsh.CardSliderAdapter
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.databinding.ItemViewWallpapersBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.Global
import com.retrytech.ledgeapp.utils.SessionManager
import java.util.Locale

class ViewWallpapersAdapter() : CardSliderAdapter<ViewWallpapersAdapter.ItemHolder>() {

    var mList: List<SettingData.WallpapersItem> = ArrayList()
    var favList: MutableList<Int> = arrayListOf()
    var lastSelected = 0
    var currantSelected = 0
    var onActionClick: OnActionClick? = null

    interface OnActionClick {
        fun onDownload(item: SettingData.WallpapersItem)
        fun onPreview(item: SettingData.WallpapersItem)
        fun onShare(item: SettingData.WallpapersItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view_wallpapers, parent, false)
        return ItemHolder(view)
    }


    override fun bindVH(holder: ItemHolder, position: Int) {
        holder.setModal(position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list: List<SettingData.WallpapersItem>) {
        mList = list
        notifyDataSetChanged()
    }


    inner class ItemHolder
        (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemViewWallpapersBinding
        var sessionManager: SessionManager

        init {
            binding = DataBindingUtil.bind(itemView)!!
            sessionManager = SessionManager(itemView.context)

        }

        fun setModal(position: Int) {
            val item = mList[position]

            binding.model = item
            binding.isFav = false
            if (item.id == -99999) {
                binding.tvDownloadCount.visibility = View.GONE
            } else {
                binding.tvDownloadCount.visibility = View.VISIBLE
                binding.tvDownloadCount.text = formatDownloads(item.downloadCount ?: 0)
            }






            for (i: Int in favList) {
                if (i == item.id) {
                    binding.isFav = true
                    break
                }
            }

            binding.icFavNo.setOnClickListener(View.OnClickListener {


                if (!favList.contains(item.id)) {
                    favList.add(item.id!!)
                    sessionManager.saveStringValue(
                        Const.Key.favourites,
                        Global.listOfIntegerToString(favList)
                    )
                    notifyItemChanged(position)
                }

            })

            binding.icFavYes.setOnClickListener(View.OnClickListener {


                if (favList.contains(item.id)) {
                    favList.remove(item.id!!)
                    sessionManager.saveStringValue(
                        Const.Key.favourites,
                        Global.listOfIntegerToString(favList)
                    )
                    notifyItemChanged(position)
                }

            })

            binding.btnDownload.setOnClickListener {
                onActionClick?.onDownload(item)
            }

            binding.btnPreview.setOnClickListener {
                onActionClick?.onPreview(item)
            }

            binding.btnShare.setOnClickListener {
                onActionClick?.onShare(item)
            }

            binding.rootLout.setOnClickListener {
                onActionClick?.onPreview(item)
            }


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
