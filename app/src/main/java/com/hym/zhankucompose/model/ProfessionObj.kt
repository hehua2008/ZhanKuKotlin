package com.hym.zhankucompose.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
{
"id": 23,
"name": "教育工作者",
"nameEn": "Educators"
}
 */
@Keep
@Serializable
data class ProfessionObj(
    val id: Int,
    val name: String,
    val nameEn: String
)