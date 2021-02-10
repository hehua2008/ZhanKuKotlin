package com.hym.zhankukotlin.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hym.zhankukotlin.network.CatagoryItem

class SectionsPager2Adapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
    private var mCatagoryItems: List<CatagoryItem> = emptyList()

    override fun createFragment(position: Int): Fragment {
        return PreviewItemFragment.newInstance(mCatagoryItems[position])
    }

    override fun getItemCount(): Int {
        return mCatagoryItems.size
    }

    fun setCatagoryItems(catagoryItems: List<CatagoryItem>) {
        mCatagoryItems = catagoryItems
        notifyDataSetChanged()
    }
}