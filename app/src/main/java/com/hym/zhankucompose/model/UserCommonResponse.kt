package com.hym.zhankucompose.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET https://api.zcool.com.cn/v2/api/u/13580089/common?app=android
 */
@Keep
@Serializable
data class UserCommonResponse(
    override val code: Int,

    @SerialName("data")
    override val dataContent: UserCommon?,

    override val msg: String
) : BaseResponse<UserCommon>()