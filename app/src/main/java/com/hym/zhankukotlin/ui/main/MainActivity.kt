package com.hym.zhankukotlin.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mSectionsPagerViewModel: SectionsPagerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mBinding.viewPager.adapter = sectionsPagerAdapter
        mBinding.tabs.setupWithViewPager(mBinding.viewPager)

        mSectionsPagerViewModel = ViewModelProvider(this).get(SectionsPagerViewModel::class.java)
        mSectionsPagerViewModel.catagoryItems.observe(this, { catagoryItems ->
            mBinding.viewPager.offscreenPageLimit = catagoryItems.size
            sectionsPagerAdapter.setCatagoryItems(catagoryItems)
        })

        mSectionsPagerViewModel.getCatagoryItemsFromNetwork()
    }
}