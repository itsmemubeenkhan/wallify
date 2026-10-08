package com.retrytech.ledgeapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.databinding.FragmentFavouriteBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.Const.toArrayList
import com.retrytech.ledgeapp.utils.Global
import com.retrytech.ledgeapp.utils.RetrofitClient
import com.retrytech.ledgeapp.viewmodel.FavouriteViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FavouriteFragment : BaseFragment() {

    lateinit var binding: FragmentFavouriteBinding
    lateinit var model: FavouriteViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false)
        model = ViewModelProvider(requireActivity())[FavouriteViewModel::class.java]



        doOnResume()
        binding.model = model
        return binding.root
    }

    private fun doOnResume() {
        var s = sessionManager.getStringValue(Const.Key.favourites)

        var list = Global.convertStringToLis(s)
        Log.i("TAG", "initView: $s")
        Log.i("TAG", "initView: $list")



        model.wallpaperAdapter.favList =
            Global.convertStringToLis(sessionManager.getStringValue(Const.Key.favourites))
                .toArrayList()

        if (list.isEmpty()) {
            model.wallpaperAdapter.clear()
            binding.rvImageByCategory.adapter = null
            binding.rvImageByCategory.adapter = model.wallpaperAdapter

            binding.loutNoFav.visibility = View.VISIBLE

        } else {
            getFavourites(s)

        }

        Log.i(" onnnn set fav", ": ${model.wallpaperAdapter.favList}")

    }

    var disposable = CompositeDisposable()

    private fun getFavourites(s: String) {

        model.wallpaperAdapter.clear()
        binding.rvImageByCategory.adapter = null
        binding.rvImageByCategory.adapter = model.wallpaperAdapter
        binding.sihmmer.visibility = View.VISIBLE
        binding.rvImageByCategory.visibility = View.GONE
        binding.loutNoFav.visibility = View.GONE

        val hashMap = HashMap<String, Any>()

        hashMap[Const.ApiParams.wallpaper_ids] = s


        disposable.add(
            RetrofitClient.service.fetchLikedWallpaper(
                hashMap
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe {
                }
                .doOnTerminate {
                    binding.sihmmer.visibility = View.GONE

                }
                .doOnError { throwable -> }
                .subscribe { wallByCat, throwable ->
                    if (wallByCat != null && wallByCat.status!! && wallByCat.data != null) {

                        if (wallByCat.data.isEmpty()) {

                            binding.loutNoFav.visibility = View.VISIBLE


                        } else {
                            binding.rvImageByCategory.visibility = View.VISIBLE
                            model.wallpaperAdapter.updateData(wallByCat.data as MutableList<SettingData.WallpapersItem>)

                        }


                    } else {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        )

    }

    override fun onResume() {
        super.onResume()
        doOnResume()
    }

    private fun refreshFavList() {

        doOnResume()


    }

}