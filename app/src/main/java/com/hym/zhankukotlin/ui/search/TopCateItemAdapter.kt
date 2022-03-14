package com.hym.zhankukotlin.ui.search

import com.google.android.material.button.MaterialButton
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.ui.NameValueAdapter

class TopCateItemAdapter(
    private val mPageViewModel: SearchContentPageViewModel
) : NameValueAdapter<String, TopCate?>() {
    override fun getOnCheckedChangeListener(holder: ViewHolder, position: Int)
            : MaterialButton.OnCheckedChangeListener {
        return MaterialButton.OnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                mPageViewModel.setTopCate(mNameValues[position].value)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.button.text = mNameValues[position].key
        if (null == mNameValues[position].value) {
            holder.button.isChecked = true
        }
    }
}