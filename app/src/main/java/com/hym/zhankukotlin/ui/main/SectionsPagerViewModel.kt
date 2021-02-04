package com.hym.zhankukotlin.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.network.CatagoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SectionsPagerViewModel : ViewModel() {
    val catagoryItems = MutableLiveData<List<CatagoryItem>>()

    fun getCatagoryItemsFromNetwork() {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                try {
                    return@withContext MyApplication.networkService.getCatagoryItemList()
                } catch (t: Throwable) {
                    Log.e(TAG, "getCatagoryItemList failed", t)
                    return@withContext null
                }
            }
            if (items != null) {
                catagoryItems.value = items
            }
        }
    }

    companion object {
        private val TAG = SectionsPagerViewModel::class.java.simpleName
    }
}