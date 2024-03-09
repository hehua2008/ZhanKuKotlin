package com.hym.zhankucompose.compose.utils

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author hehua2008
 * @date 2024/3/9
 */
/**
 *     class VideoAudioDownloadModel {
 *         ...
 *         class Progress { ... }
 *
 *         private val _progress = MutableStateFlow(Progress())
 *         val progress: StateFlow<Progress> = _progress.asStateFlow()
 *         ...
 *     }
 *
 *     var downloadModel: VideoAudioDownloadModel? by remember {
 *         mutableStateOf(null)
 *     }
 *
 *     val downloadProgress: VideoAudioDownloadModel.Progress? by NullableDelegate(
 *         downloadModel?.run { progress.collectAsState() },
 *         androidx.compose.runtime.State<VideoAudioDownloadModel.Progress>::getValue
 *     )
 */
class NullableDelegate<T, V, O>(
    private val srcObj: O?,
    private val srcDelegate: (O.(T?, KProperty<*>) -> V)
) : ReadOnlyProperty<T, V?> {

    override operator fun getValue(thisRef: T, property: KProperty<*>): V? {
        srcObj ?: return null
        return srcDelegate.invoke(srcObj, thisRef, property)
    }
}
