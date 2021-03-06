package com.hym.zhankukotlin.network

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object ItemParser {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val file = File("/Volumes/Personal/heganmin/26.html")
        val reader = BufferedReader(FileReader(file))
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        reader.close()
        val html = sb.toString()
        val catagoryItems = getAllCatagoryItems(html)
        println(catagoryItems)
        val pageArr = getPaged(html)
        println(pageArr.contentToString())
        val previewItems = getPreviewItems(html)
        println(previewItems)
        val catagoryItem = getCurrentCatagoryItem(html)
        println(catagoryItem)
    }

    fun getAllCatagoryItems(html: String): List<CatagoryItem> {
        val doc = Jsoup.parse(html)
        val ullis = doc.select("#body > main > div.classify-select > div > ul > li")
        val catagoryItems: MutableList<CatagoryItem> = mutableListOf()
        for (ulli in ullis) {
            val typea = ulli.selectFirst("div.classify-item-type > a") ?: continue
            val catagoryItem = getCatagoryItem(typea)
            val lias = ulli.select(
                    "div.type-combobox > div > div > div.item.item1 > ul > li.category > a"
            )
            for (lia in lias) {
                val subCatagoryItem = getCatagoryItem(lia)
                catagoryItem.addSubItem(subCatagoryItem)
            }
            val lia2s = ulli.select(
                    "div.type-combobox > div > div > div.item.item2 > ul > li.category > a"
            )
            for (lia2 in lia2s) {
                val sub2CatagoryItem = getCatagoryItem(lia2)
                catagoryItem.addSub2Item(sub2CatagoryItem)
            }
            catagoryItems.add(catagoryItem)
        }
        return catagoryItems
    }

    fun getCurrentCatagoryItem(html: String): CatagoryItem {
        return getCurrentCatagoryItem(html, false)
    }

    private fun getCurrentCatagoryItem(html: String, retry: Boolean): CatagoryItem {
        val doc = Jsoup.parse(html)
        val ullis = doc.select(
                "#body > main > div.classify-select > div > ul > li.parents-nodes.classify-item"
        )
        for (ulli in ullis) {
            val cur = ulli.selectFirst(
                    if (retry) "div.classify-item-type > a.classify-default"
                    else "div.classify-item-type > a.classify-default.current"
            ) ?: continue
            val catagoryItem = getCatagoryItem(cur)
            val lias = ulli.select(
                    "div.type-combobox > div > div > div.item.item1 > ul > li.category > a"
            )
            for (lia in lias) {
                val subCatagoryItem = getCatagoryItem(lia)
                catagoryItem.addSubItem(subCatagoryItem)
            }
            val lia2s = ulli.select(
                    "div.type-combobox > div > div > div.item.item2 > ul > li.category > a"
            )
            for (lia2 in lia2s) {
                val sub2CatagoryItem = getCatagoryItem(lia2)
                catagoryItem.addSub2Item(sub2CatagoryItem)
            }
            return catagoryItem
        }
        return if (!retry) {
            getCurrentCatagoryItem(html, true)
        } else CatagoryItem.ERROR
    }

    private fun getCatagoryItem(root: Element): CatagoryItem {
        val url = root.absUrl("href")
        val title = root.ownText()
        return CatagoryItem.createCatagoryItem(url, title)
    }

    fun getPreviewItems(html: String): List<PreviewItem> {
        val doc = Jsoup.parse(html)
        val elements = doc.select("#body > main > div > div > div")
        val previewItemList: MutableList<PreviewItem> = mutableListOf()
        for (cur in elements) {
            val cardImg = cur.selectFirst("div.card-img > a") ?: continue
            val targetUrl = cardImg.absUrl("href")
            val imageUrl = cardImg.selectFirst("img").absUrl("src")
            val cardInfo = cur.selectFirst("div.card-info") ?: continue
            val titleNode = cardInfo.selectFirst("p.card-info-title > a")
            val title = titleNode?.ownText() ?: ""
            val typeNode = cardInfo.selectFirst("p.card-info-type")
            val type = typeNode?.ownText() ?: ""
            val item = cardInfo.selectFirst("p.card-info-item") ?: continue
            val viewNode = item.selectFirst("span.statistics-view")
            val views = viewNode?.ownText() ?: ""
            val commentNode = item.selectFirst("span.statistics-comment")
            val comments = commentNode?.ownText() ?: ""
            val favoriteNode = item.selectFirst("span.statistics-tuijian")
            val favorites = favoriteNode?.ownText() ?: ""
            val authorNode = cur.selectFirst("div.card-item > span.user-avatar.showMemberCard > a")
            val author = authorNode?.ownText() ?: ""
            val timeNode = cur.selectFirst("div.card-item > span.time")
            val time = timeNode?.ownText() ?: ""
            val builder: PreviewItem.Builder = PreviewItem.Builder()
                    .imageUrl(imageUrl)
                    .targetUrl(targetUrl)
                    .title(title)
                    .views(views)
                    .comments(comments)
                    .favorites(favorites)
                    .author(author)
                    .time(time)
            val previewItem = builder.build()
            previewItemList.add(previewItem)
        }
        return previewItemList
    }

    fun getDetailItem(html: String): DetailItem {
        val doc = Jsoup.parse(html)
        val builder: DetailItem.Builder = DetailItem.Builder()
        val parent = doc.selectFirst(
                "#body > main > div.bc-fff.p-relative > div.work-details-wrap.border-bottom > div >"
                        + " div.left-details-head.border-right.left > div"
        )
        val titleNode = parent.selectFirst("h2")
        if (titleNode != null) {
            builder.title(titleNode.ownText())
        }
        val timeNode = parent.selectFirst("p > span")
        if (timeNode != null) {
            builder.time(timeNode.ownText())
        }
        val cats = parent.select("div > div.head-left > span > span > a")
        for (cat in cats) {
            val url = cat.absUrl("href")
            val title = cat.ownText()
            if (url.isNotEmpty()) {
                builder.addCatagory(CatagoryItem.createCatagoryItem(url, title))
            }
        }
        val views = parent.selectFirst("div > div.head-right > span > a.see.vertical-line")
        if (views != null) {
            builder.views(views.ownText())
        }
        val comments = parent.selectFirst("div > div.head-right > span > a.news.vertical-line")
        if (comments != null) {
            builder.comments(comments.ownText())
        }
        val favorites = parent.selectFirst("div > div.head-right > span > a.recommend-show")
        if (favorites != null) {
            builder.favorites(favorites.ownText())
        }
        val imgs = doc.select(
                "#body > main > div.bc-fff.p-relative >"
                        + " div.work-details-content > div.work-content-wrap > div >"
                        + " div.work-show-box.mt-40.js-work-content > div > div > img"
        )
        for (img in imgs) {
            val url = img.absUrl("src")
            if (url.isNotEmpty()) {
                builder.addImageUrl(url)
            }
        }
        return builder.build()
    }

    fun getPaged(html: String): IntArray {
        val doc = Jsoup.parse(html)
        val las = doc.select("#laypage_0 > a")
        val pageArr = intArrayOf(1, 1)
        for (a in las) {
            val clazzName = a.className()
            if ("laypage_next" == clazzName) {
                break
            }
            val number: Int = try {
                a.ownText().toInt()
            } catch (e: NumberFormatException) {
                // ignore
                continue
            }
            if ("laypage_curr" == a.className()) {
                pageArr[0] = number
            }
            if (pageArr[1] < number) {
                pageArr[1] = number
            }
        }
        return pageArr
    }

    fun getLoginInfo(html: String): String {
        val doc = Jsoup.parse(html)
        val body = doc.selectFirst("body")
        return body.ownText()
    }
}