package com.hym.zhankukotlin.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
{
"createTime": 1638836663000,
"creator": 15611957,
"description": "",
"device": "",
"height": 4725,
"id": 70089149,
"name": "01d60861aea9b511013e8cd07a2dee.jpg",
"orderNo": 0,
"pageUrl": "https://www.zcool.com.cn/workimage/ZMjgwMzU2NTk2.html",
"path": "community",
"productId": 14187298,
"proofCreateTime": 1638853970000,
"proofStatus": 0,
"proofUpdateTime": 1638853970000,
"property": "{}",
"url": "https://img.zcool.cn/community/01d60861aea9b511013e8cd07a2dee.jpg@1280w_1l_2o_100sh.jpg",
"width": 3150
}
 */
@Keep
data class ProductImage(
    val createTime: Long,
    val creator: Int,
    val description: String,
    val device: String,
    val height: Int,
    val id: Int,
    val name: String,
    val orderNo: Int,
    val pageUrl: String,
    val path: String,
    val productId: Int,
    val proofCreateTime: Long,
    val proofStatus: Int,
    val proofUpdateTime: Long,
    @SerializedName("property")
    val properties: String,
    val url: String,
    val width: Int
) {
    val oriUrl: String get() = url.replace(regex, "@${width}w")

    companion object {
        val regex = Regex("@\\d+w")
    }
}