package com.retrytech.ledgeapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.databinding.ItemHomeCatBinding
import com.retrytech.ledgeapp.model.SettingData

class HomeCatAdapter : RecyclerView.Adapter<HomeCatAdapter.ItemHolder>() {
    var lastSelected = 0
    var currantSelected = 0
    var mList: MutableList<SettingData.CategoriesItem> = mutableListOf()
    var onItemClick: OnItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_cat, parent, false)
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

    interface OnItemClick {
        fun onClick(item: SettingData.CategoriesItem)
    }

    fun updateData(list: MutableList<SettingData.CategoriesItem>, all: String) {


        var catAll = SettingData.CategoriesItem(
            null,
            null,
            null,
            0,
            all,
            0
        )
        mList = list
        mList.add(0, catAll)
        notifyDataSetChanged()
    }

    fun setToFirst(item: SettingData.CategoriesItem) {


    }


    inner class ItemHolder
        (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemHomeCatBinding

        init {
            binding = DataBindingUtil.bind(itemView)!!
        }

        fun setModal(position: Int) {
            val item = mList[position]


            item.title.let {
                binding.tvName.text = it

            }


            if (position == currantSelected) {
                binding.tvName.setBackgroundResource(R.drawable.bg_purple_gradient_500)

                binding.tvName.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.color_text_primary
                    )
                )

            } else {
                binding.tvName.setBackgroundResource(R.drawable.bg_home_tab_unselected)
                binding.tvName.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.color_text_secondary
                    )
                )
            }

            binding.tvName.setOnClickListener {

                if (position != currantSelected) {
                    lastSelected = currantSelected
                    currantSelected = position
                    notifyItemChanged(currantSelected)
                    notifyItemChanged(lastSelected)
                    onItemClick?.onClick(item)
                }
            }

        }
    }
}
