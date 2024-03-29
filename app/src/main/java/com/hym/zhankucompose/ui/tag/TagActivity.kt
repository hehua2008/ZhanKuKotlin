package com.hym.zhankucompose.ui.tag

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.commitNow
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hym.zhankucompose.BaseActivity
import com.hym.zhankucompose.R
import com.hym.zhankucompose.databinding.TagActivityBinding
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.author.AuthorItemFragment
import com.hym.zhankucompose.ui.main.PreviewItemFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TagActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val author: CreatorObj? = intent.getParcelableExtra(AuthorItemFragment.AUTHOR)
        val topCate: TopCate? = intent.getParcelableExtra(PreviewItemFragment.TOP_CATE)
        val subCate: SubCate? = intent.getParcelableExtra(PreviewItemFragment.SUB_CATE)
        val mTitle = author?.username ?: topCate?.name ?: subCate?.name

        val binding = TagActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.actionBar.run {
            title = mTitle
            author?.let {
                Glide.with(this@TagActivity)
                    .load(it.avatar1x)
                    .optionalCircleCrop()
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable, transition: Transition<in Drawable>?
                        ) {
                            logo = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            logo = null
                        }
                    })
            }
            setNavigationOnClickListener { finish() }
        }

        if (savedInstanceState != null) return
        supportFragmentManager.commitNow {
            if (author != null) {
                replace(R.id.container, AuthorItemFragment.newInstance(author))
            } else {
                replace(R.id.container, PreviewItemFragment.newInstance(topCate, subCate))
            }
        }
    }
}