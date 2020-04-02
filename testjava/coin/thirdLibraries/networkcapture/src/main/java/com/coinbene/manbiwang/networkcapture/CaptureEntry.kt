package com.coinbene.manbiwang.networkcapture

import java.io.Serializable

/**
 *Created by june
 *on 2019-12-20
 */

data class CaptureEntry(
        var requestMethod: String,
        var requestUrl: String,
        var requestHeader: String,
        var requestBody: String,
        var responseStatus: String,
        var responseHeader: String,
        var responseBody: String
) : Serializable

fun main() {

    data class Person(private var name:String)

    var p1 = Person("hello")
    var p2 = Person("hello")

    print(p1.equals(p2))
}