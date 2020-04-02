package com.coinbene.manbiwang.networkcapture

import java.util.*
import java.util.Collections.synchronizedList




/**
 *Created by june
 *on 2019-12-20
 */

object NetworkDataManager {

    private const val LIMIT_SIZE = 20


    private var dataList = synchronizedList(LinkedList<CaptureEntry>())

    fun addCaptureInfo(entry: CaptureEntry) {
        if (dataList.size >= LIMIT_SIZE) {
            dataList.removeAt(0)
        }
        dataList.add(entry)
    }

    fun getData() : List<CaptureEntry> {
        return dataList.reversed()
    }
}