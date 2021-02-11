package com.hym.zhankukotlin.ui.main

import com.google.android.material.button.MaterialButton
import com.hym.zhankukotlin.network.CatagoryItem
import com.hym.zhankukotlin.ui.NameValueAdapter
import java.util.*

class CatagoryItemAdapter(private val mPageViewModel: PageViewModel) :
        NameValueAdapter<String, String>() {
    override fun getOnCheckedChangeListener(holder: ViewHolder, position: Int)
            : MaterialButton.OnCheckedChangeListener {
        return MaterialButton.OnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                mPageViewModel.setSubcat(mNameValues[position].value)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.button.text = mNameValues[position].key
    }

    fun setTitleSubcatMap(parentCatagoryItem: CatagoryItem) {
        val subItems = parentCatagoryItem.subItems
        val sub2Items = parentCatagoryItem.sub2Items
        val size = 1 + subItems.size + sub2Items.size
        if (size == 1) {
            return
        }
        val titleUrlMap: MutableMap<String, String> = LinkedHashMap(size)
        titleUrlMap[parentCatagoryItem.title] = getSubcat(parentCatagoryItem.url)
        for (subItem in subItems) {
            titleUrlMap[subItem.title] = getSubcat(subItem.url)
        }
        for (sub2Item in sub2Items) {
            titleUrlMap[sub2Item.title] = getSubcat(sub2Item.url)
        }
        setTitleSubcatMap(titleUrlMap)
    }

    fun setTitleSubcatMap(titleSubcatMap: Map<String, String>) {
        setNameValueMap(titleSubcatMap)
    }

    private companion object {
        fun getSubcat(url: String): String {
            val first = url.indexOf('!')
            if (first < 0 || first == url.length - 1) {
                return ""
            }
            val second = url.indexOf('!', first + 1)
            if (second < 0 || second == url.length - 1) {
                return ""
            }
            val third = url.indexOf('!', second + 1)
            return if (third < 0) {
                ""
            } else url.substring(first + 1, third + 1)
        }
    }
}