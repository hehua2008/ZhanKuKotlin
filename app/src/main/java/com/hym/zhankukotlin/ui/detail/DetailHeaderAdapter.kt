package com.hym.zhankukotlin.ui.detail

import android.Manifest
import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.DetailHeaderLayoutBinding
import com.hym.zhankukotlin.model.WorkDetails
import com.hym.zhankukotlin.ui.BindingViewHolder
import com.hym.zhankukotlin.util.PermissionUtils
import com.hym.zhankukotlin.util.PictureUtils

class DetailHeaderAdapter(private val mTitle: String, private val mWorkId: String) :
    RecyclerView.Adapter<BindingViewHolder<DetailHeaderLayoutBinding>>() {
    private var binding: DetailHeaderLayoutBinding? = null

    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<DetailHeaderLayoutBinding> {
        val binding =
            DetailHeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.apply {
            detailTitle.text = mTitle
            detailLink.text = "https://www.zcool.com.cn/work/$mWorkId"

            val tagItemLayoutManager =
                FlexboxLayoutManager(parent.context, FlexDirection.ROW, FlexWrap.WRAP)
            tagItemLayoutManager.justifyContent = JustifyContent.SPACE_EVENLY
            tagItemRecycler.layoutManager = tagItemLayoutManager
            tagItemRecycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
                private val mOffset = parent.resources.getDimensionPixelSize(
                    R.dimen.button_item_horizontal_offset
                ) and 1.inv()
                private val mHalfOffset = mOffset shr 1

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val itemPosition =
                        (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                    val itemCount = state.itemCount
                    val left = if (itemPosition == 0) mOffset else mHalfOffset
                    val right = if (itemPosition == itemCount - 1) mOffset else mHalfOffset
                    outRect.set(left, 0, right, 0)
                }
            })
            tagItemRecycler.adapter = TagUrlItemAdapter()
        }
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<DetailHeaderLayoutBinding>,
        position: Int
    ) {
        binding = holder.binding
    }

    fun setDetailItem(workDetails: WorkDetails) {
        binding?.run {
            detailItem = workDetails

            (tagItemRecycler.adapter as TagUrlItemAdapter).setTagItems(workDetails.product.run {
                listOf(fieldCateObj, subCateObj)
            })

            downloadAll.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                    && !PermissionUtils.checkSelfPermission(
                        root.context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    Toast.makeText(root.context, R.string.permission_denied, Toast.LENGTH_LONG)
                        .show()
                    PermissionUtils.requestPermissions(
                        (root.context as Activity), Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    return@setOnClickListener
                }
                PictureUtils.downloadAll(workDetails.product.productImages.map { it.oriUrl })
            }
        }
    }
}