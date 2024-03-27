package com.hym.zhankucompose.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ArticleDetailsResponse(
    override val code: Int,

    @SerialName("data")
    override val dataContent: ArticleDetails?,

    override val msg: String
) : BaseResponse<ArticleDetails>()