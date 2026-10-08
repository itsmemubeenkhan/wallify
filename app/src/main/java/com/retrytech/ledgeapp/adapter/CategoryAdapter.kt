package com.retrytech.ledgeapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.activity.MainActivity
import com.retrytech.ledgeapp.activity.WallpaperByCatActivity
import com.retrytech.ledgeapp.databinding.ItemCategoryBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const

class CategoryAdapter(requireActivity: FragmentActivity) :
    RecyclerView.Adapter<CategoryAdapter.ItemHolder>() {
    val activity = requireActivity
    var mList: List<SettingData.CategoriesItem> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
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

    fun updateData(list: List<SettingData.CategoriesItem>) {
        mList = list
        notifyDataSetChanged()
    }


    inner class ItemHolder
        (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemCategoryBinding

        init {
            binding = DataBindingUtil.bind(itemView)!!
        }

        fun setModal(position: Int) {
            val item = mList[position]

            binding.model = item
            if (activity is MainActivity) {

                activity.setBlur(binding.blurView, binding.rootLout)

            }

            binding.root.setOnClickListener {
                var i = Intent(itemView.context, WallpaperByCatActivity::class.java)
                i.putExtra(Const.Key.data, Gson().toJson(item))
                itemView.context.startActivity(i)

            }

        }
    }
}