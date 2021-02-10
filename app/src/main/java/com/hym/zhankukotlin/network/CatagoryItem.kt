package com.hym.zhankukotlin.network

import android.os.Parcel
import android.os.Parcelable

class CatagoryItem private constructor(
        val url: String,
        val title: String
) : Parcelable {
    private val mSubItems: MutableList<CatagoryItem> = mutableListOf()
    private val mSub2Items: MutableList<CatagoryItem> = mutableListOf()

    fun hasSubItems(): Boolean {
        return mSubItems.isNotEmpty()
    }

    fun hasSub2Items(): Boolean {
        return mSub2Items.isNotEmpty()
    }

    val subItems: List<CatagoryItem>
        get() = mSubItems

    val sub2Items: List<CatagoryItem>
        get() = mSub2Items

    fun addSubItem(subItem: CatagoryItem) {
        mSubItems.add(subItem)
    }

    fun addSubItem(subUrl: String, subTitle: String) {
        addSubItem(CatagoryItem(subUrl, subTitle))
    }

    fun addSub2Item(sub2Item: CatagoryItem) {
        mSub2Items.add(sub2Item)
    }

    fun addSub2Item(subUrl: String, subTitle: String) {
        addSub2Item(CatagoryItem(subUrl, subTitle))
    }

    override fun toString(): String {
        val sb = StringBuilder(title).append(" : ").append(url)
        if (hasSubItems()) {
            sb.append('\n').append(mSubItems)
        }
        if (hasSub2Items()) {
            sb.append('\n').append(mSub2Items)
        }
        sb.append('\n')
        return sb.toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
    }

    companion object CREATOR : Parcelable.Creator<CatagoryItem?> {
        private val URL_ITEM_MAP: MutableMap<String, CatagoryItem> = HashMap()
        val ERROR = CatagoryItem("ERROR", "ERROR")

        fun getCatagoryItem(url: String?): CatagoryItem? {
            return URL_ITEM_MAP[url]
        }

        fun createCatagoryItem(url: String, title: String): CatagoryItem {
            val cat = URL_ITEM_MAP[url]
            return cat ?: CatagoryItem(url, title)
        }

        override fun createFromParcel(parcel: Parcel): CatagoryItem? {
            val url = parcel.readString()
            return URL_ITEM_MAP[url]
        }

        override fun newArray(size: Int): Array<CatagoryItem?> {
            return arrayOfNulls(size)
        }
    }

    init {
        URL_ITEM_MAP[url] = this
    }
}