package com.hym.zhankucompose.ui.detail

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.rememberGlidePreloadingData
import com.hym.zhankucompose.R
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.ui.photoviewer.UrlPhotoInfo
import com.hym.zhankucompose.util.getActivity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

/**
 * @author hehua2008
 * @date 2024/3/17
 */
@Composable
fun DetailContentLayout(
    detailContents: ImmutableList<DetailContent<*>>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    headerContent: @Composable ((modifier: Modifier) -> Unit)? = null
) {
    val loadingPainter = rememberVectorPainter(
        ImageVector.vectorResource(R.drawable.vector_image)
    )
    val failurePainter = rememberVectorPainter(
        ImageVector.vectorResource(R.drawable.vector_image_broken)
    )
    val sizeCache = remember(detailContents) {
        mutableMapOf<DetailImage, IntSize>()
    }

    val view = LocalView.current
    val activity = remember(view) { view.getActivity() }
    val imageList = remember(detailContents) {
        detailContents.filterIsInstance<DetailImage>().toImmutableList()
    }
    val photoInfos = remember(imageList) {
        imageList.map {
            UrlPhotoInfo(
                original = it.data.oriUrl,
                thumb = it.data.url,
                width = it.data.oriWidth,
                height = it.data.oriHeight
            )
        }.toImmutableList()
    }

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val bodyTextStyle = MaterialTheme.typography.bodyMedium.let {
        remember(it, onSurfaceColor) {
            it.copy(color = onSurfaceColor)
        }
    }

    BoxWithConstraints {
        val maxWidth = constraints.maxWidth

        val glidePreloadingData =
            rememberGlidePreloadingData(
                data = detailContents,
                preloadImageSize = Size(1f, 1f),
                numberOfItemsToPreload = 2
            ) { item, requestBuilder ->
                // preloadImageSize is applied for you, but .load() is not because determining the
                // model from the underlying data isn't trivial. Don't forget to call .load()!
                if (item is DetailImage) {
                    val size = sizeCache[item] ?: item.data.run {
                        if (maxWidth != Constraints.Infinity && width > 0 && height > 0) {
                            IntSize(
                                maxWidth, (maxWidth * height / width.toFloat()).roundToInt()
                            )
                        } else null
                    }
                    requestBuilder.load(item.data.url).run {
                        if (size == null) this
                        else override(size.width, size.height)
                    }
                } else {
                    // Do this will print log:
                    // Load failed for [null] with dimensions [0x0]
                    // class com.bumptech.glide.load.engine.GlideException: Received null model
                    // But we have to do this, or else throw "load() not called before into() error"
                    requestBuilder.load(null as Drawable?)
                }
            }

        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(COMMON_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (headerContent != null) {
                item(key = "HeaderContent") {
                    headerContent(Modifier)
                }
            }

            items(
                count = detailContents.size,
                key = { detailContents[it].id },
                contentType = {
                    when (detailContents[it]) {
                        is DetailImage -> DetailContent.CONTENT_IMAGE
                        is DetailVideo -> DetailContent.CONTENT_VIDEO
                        is DetailText -> DetailContent.CONTENT_TEXT
                    }
                }
            ) { index ->
                val (detailContent, preloadRequest) = glidePreloadingData[index]
                when (detailContent) {
                    is DetailImage -> {
                        val size = sizeCache[detailContent] ?: detailContent.data.run {
                            if (maxWidth != Constraints.Infinity && width > 0 && height > 0) {
                                IntSize(
                                    maxWidth, (maxWidth * height / width.toFloat()).roundToInt()
                                )
                            } else null
                        }

                        DetailContentImage(
                            detailImage = detailContent,
                            loadingPainter = loadingPainter,
                            failurePainter = failurePainter,
                            size = size,
                            onGetSize = { sizeCache[detailContent] = it }
                        ) { detailImage ->
                            val act = activity ?: return@DetailContentImage
                            if (act !is DetailActivity) return@DetailContentImage
                            // Get the item at this position every time since currentList may be changed
                            act.launchPhotoViewerActivity(
                                photoInfos, imageList.indexOf(detailImage)
                            )
                        }
                    }

                    is DetailVideo -> {
                        DetailContentVideo(detailVideo = detailContent)
                    }

                    is DetailText -> {
                        DetailContentText(
                            detailText = detailContent,
                            modifier = Modifier.padding(horizontal = COMMON_PADDING),
                            textStyle = bodyTextStyle
                        )
                    }
                }
            }

            item(key = "BottomContent") {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
        }
    }
}
