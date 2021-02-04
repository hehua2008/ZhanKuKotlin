package com.hym.zhankukotlin.network

import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ItemConverter<T>(private val mType: Type) : Converter<ResponseBody, T?> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? {
        val html = value.string()
        if (mType is Class<*>) {
            if (mType === DetailItem::class.java) {
                return ItemParser.getDetailItem(html) as T
            } else if (mType === PreviewResult::class.java) {
                val previewItems = ItemParser.getPreviewItems(html)
                val catagoryItem = ItemParser.getCurrentCatagoryItem(html)
                val pagedArr = ItemParser.getPaged(html)
                val previewResult = PreviewResult(previewItems, catagoryItem, pagedArr)
                return previewResult as T
            }
        } else if (mType is ParameterizedType) {
            val clazz = mType.actualTypeArguments[0] as Class<*>
            if (clazz === CatagoryItem::class.java) {
                val catagoryItems = ItemParser.getAllCatagoryItems(html)
                return catagoryItems as T
            }
        }
        throw IOException("Can't parse type $mType")
    }
}