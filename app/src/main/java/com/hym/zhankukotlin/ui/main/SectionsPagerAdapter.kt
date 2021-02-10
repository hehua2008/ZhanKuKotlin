package com.hym.zhankukotlin.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hym.zhankukotlin.network.CatagoryItem

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var mCatagoryItems: List<CatagoryItem> = emptyList()

    override fun getItem(position: Int): Fragment {
        return PreviewItemFragment.newInstance(mCatagoryItems[position])
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mCatagoryItems[position].title
    }

    override fun getCount(): Int {
        return mCatagoryItems.size
    }

    fun setCatagoryItems(catagoryItems: List<CatagoryItem>) {
        mCatagoryItems = catagoryItems
        notifyDataSetChanged()
    }
}