package com.hym.zhankucompose.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET https://api.zcool.com.cn/v2/api/work/ZNTY3NDkxOTI=.html?app=android
 */
@Keep
@Serializable
data class WorkDetailsResponse(
    override val code: Int,

    @SerialName("data")
    override val dataContent: WorkDetails?,

    override val msg: String
) : BaseResponse<WorkDetails>()