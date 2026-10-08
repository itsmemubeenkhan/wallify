package com.retrytech.ledgeapp.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData


open class MainViewModel : BaseViewModel() {


    var selectedTab: MutableLiveData<Int> = MutableLiveData(0)
    var selectedNav: MutableLiveData<Int> = MutableLiveData(-1)
    var navOpen: MutableLiveData<Boolean> = MutableLiveData(false)

    var onBackPress: MutableLiveData<Boolean> = MutableLiveData()
    var isNavCatOpen: ObservableBoolean = ObservableBoolean(false)
    var cordinated_expandeed: MutableLiveData<Boolean> = MutableLiveData(false)

    open fun onTabClick(i: Int) {
        if (i == 4) {
            navOpen.value = true
            return
        }
        if (selectedTab.value != i) {
            navOpen.value = false
            selectedTab.value = i
        }

    }

    open fun onNavItemClick(i: Int) {
        if (i == 2) {
            isNavCatOpen.set(!isNavCatOpen.get())
            return
        }
        selectedNav.value = i

    }


}
