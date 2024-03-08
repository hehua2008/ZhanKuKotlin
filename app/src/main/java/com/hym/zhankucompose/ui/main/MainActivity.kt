package com.hym.zhankucompose.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.hym.zhankucompose.BaseActivity
import com.hym.zhankucompose.MyAppViewModel
import com.hym.zhankucompose.databinding.ActivityMainBinding
import com.hym.zhankucompose.getAppViewModel
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.TabReselectedCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity(), OnTabSelectedListener {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
    private val mTopCates: MutableList<TopCate> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Add callback before fragmentManager
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(false)
            }
        })
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.tabs.addOnTabSelectedListener(this)

        getAppViewModel<MyAppViewModel>().categoryItems.observe(this) { categoryItems ->
            mTopCates.clear()
            mTopCates.addAll(categoryItems)
            binding.viewPager.offscreenPageLimit = 1 + mTopCates.size
            sectionsPagerAdapter.setCategoryItems(mTopCates)
            binding.viewPager.currentItem = if (mTopCates.isEmpty()) 0 else 1
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        sectionsPagerAdapter.currentFragment?.let {
            if (it is TabReselectedCallback) {
                it.onTabReselected()
            }
        }
    }
}