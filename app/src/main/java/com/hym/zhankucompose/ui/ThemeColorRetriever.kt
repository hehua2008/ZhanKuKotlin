package com.hym.zhankucompose.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hym.zhankucompose.MyApplication
import com.hym.zhankucompose.R
import com.hym.zhankucompose.util.MMCQ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ThemeColorRetriever {
    suspend fun getMainThemeColor(model: Any): MMCQ.ThemeColor? {
        val requestManager = Glide.with(MyApplication.INSTANCE)
        val (bitmap, target) = suspendCoroutine { continuation ->
            requestManager.asBitmap()
                .load(model)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap>?
                    ) {
                        continuation.resume(resource to this)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        continuation.resume(null to this)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit
                })
        }
        return try {
            if (bitmap != null) getMainThemeColor(bitmap) else null
        } finally {
            requestManager.clear(target)
        }
    }

    suspend fun getMainThemeColor(bitmap: Bitmap): MMCQ.ThemeColor? {
        val themeColors = withContext(Dispatchers.Default) {
            val mmcq = MMCQ(bitmap, 3)
            mmcq.quantize()
        }
        return if (themeColors.isEmpty()) null else themeColors[0]
    }

    @JvmStatic
    fun Activity.setThemeColor(themeColor: MMCQ.ThemeColor?) {
        if (themeColor == null) return
        window.statusBarColor = themeColor.color
        val toolbar = findViewById(R.id.action_bar) as? Toolbar ?: return
        toolbar.setBackgroundColor(themeColor.color)
        toolbar.setTitleTextColor(themeColor.titleTextColor)
        toolbar.setSubtitleTextColor(themeColor.titleTextColor)
    }
}