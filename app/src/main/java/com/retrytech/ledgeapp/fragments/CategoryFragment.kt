package com.retrytech.ledgeapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.adapter.CategoryAdapter
import com.retrytech.ledgeapp.databinding.FragmentCategoryBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.viewmodel.CategoryViewModel

class CategoryFragment : BaseFragment() {

    lateinit var binding: FragmentCategoryBinding
    lateinit var viewModel: CategoryViewModel
    lateinit var allCat: List<SettingData.CategoriesItem>
    lateinit var liveCat: List<SettingData.CategoriesItem>
    lateinit var simpleCat: List<SettingData.CategoriesItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        viewModel = ViewModelProvider(requireActivity())[CategoryViewModel::class.java]
        viewModel.categoryAdapter = CategoryAdapter(requireActivity())


        initView()
        initListeners()
        initObserver()


        binding.model = viewModel




        return binding.root
    }

    private fun initObserver() {
        viewModel.selected.observe(viewLifecycleOwner, Observer {
            when (it) {
                0 -> viewModel.categoryAdapter.updateData(allCat)
                1 -> viewModel.categoryAdapter.updateData(simpleCat)
                2 -> viewModel.categoryAdapter.updateData(liveCat)
            }
            binding.nestedScrollView.scrollTo(0, 0)


            binding.model = viewModel
        })
    }

    private fun initListeners() {


    }

    private fun initView() {
        val widthDp = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val spanCount = if (widthDp >= 600f) 4 else 3
        binding.rvCat.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.rvCat.setHasFixedSize(true)

        allCat = sessionManager.categories
        simpleCat = sessionManager.categories.filter { categoriesItem -> categoriesItem.type == 0 }
        liveCat = sessionManager.categories.filter { categoriesItem -> categoriesItem.type == 1 }


    }


}
