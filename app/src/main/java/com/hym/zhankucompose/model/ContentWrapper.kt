package com.hym.zhankucompose.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
{
"object": {...},
"objectType": 78
}
 */
@Keep
@Serializable
data class ContentWrapper(
    @SerialName("object")
    val content: Content,
    val objectType: Int
)