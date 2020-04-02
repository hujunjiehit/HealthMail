package com.coinbene.manbiwang.networkcapture

import com.lzy.okgo.request.base.ProgressRequestBody
import okhttp3.*
import okio.Buffer
import okio.BufferedSource
import java.io.EOFException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit


/**
 *Created by june
 *on 2019-12-20
 */

class CaptureInfoInterceptor : Interceptor {

    val utf8 = Charset.forName("UTF-8")


    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val captureEntity = CaptureEntry("","","","","","","")

        val requestBody = request.body()

        val hasRequestBody = requestBody != null

        val connection = chain.connection()

        val protocal = connection?.protocol() ?: Protocol.HTTP_1_1

        var requestStartMessage = request.method() + "   " + protocal

        if (hasRequestBody) {
            requestStartMessage += " (" + requestBody!!.contentLength() + "-byte body)"
        }

        captureEntity.requestMethod = requestStartMessage
        captureEntity.requestUrl = request.url().toString()

        val headerBuffer = StringBuffer()
        if (hasRequestBody) {
            if (requestBody!!.contentType() != null) {
                headerBuffer.append("Content-Type: " + requestBody.contentType()).append("\n");
            }
            if (requestBody!!.contentLength() != -1L) {
                headerBuffer.append("Content-Length: " + requestBody.contentLength()).append("\n");
            }
        }
        val headers = request.headers()

        for (headr in headers.names()) {
            if ("content-type" != headr.toLowerCase() && "content-length" != headr.toLowerCase()) {
                headerBuffer.append(headr + ": " + headers.get(headr)).append("\n")
            }
        }

        captureEntity.requestHeader = headerBuffer.toString()

        if (!bodyEncoded(headers)) {
            val buffer = Buffer()
            if (requestBody != null) {
                requestBody.writeTo(buffer)
                var charset: Charset? = utf8
                val contentType: MediaType? = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(utf8)
                }
                val requestBodyBuffer = StringBuffer()
                if (isPlaintext(buffer)) {
                    if (requestBody is FormBody) {
                        val formBody: FormBody = requestBody as FormBody
                        for (i in 0 until formBody.size()) {
                            requestBodyBuffer.append(formBody.name(i)).append(":")
                                    .append(formBody.value(i)).append("\n")
                        }
                    } else {
                        val field = ProgressRequestBody::class.java.getDeclaredField("requestBody")
                        field.isAccessible = true

                        val formBody: FormBody? = field.get(requestBody) as? FormBody
                        formBody?.let {
                            for (i in 0 until formBody.size()) {
                                requestBodyBuffer.append(formBody.name(i)).append(":")
                                        .append(formBody.value(i)).append("\n")
                            }
                        }
                    }
                    // requestBodyBuffer.append(buffer.readString(charset)).append("\n");
                    requestBodyBuffer.append(request.method()
                            + " (" + requestBody.contentLength() + "-byte body)").append("\n")
                } else {
                    requestBodyBuffer.append(request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)").append("\n")
                }
                captureEntity.requestBody = requestBodyBuffer.toString()
            }
        }

        val startNs = System.nanoTime()
        var response: Response

        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            captureEntity.responseBody = "HTTP FAILED:$e"
            //CacheUtils.getInstance().saveCapture(request.url().toString(), captureEntity)
            throw e
        }

        val tookMs: Long = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body()
        val contentLength = responseBody!!.contentLength()

        captureEntity.responseStatus = ("<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + "" + ')')

        val respHeaders = response.headers()
        val responseHeaderBuffer = StringBuffer()
        for(i in 0 until respHeaders.size() ) {
            responseHeaderBuffer.append(respHeaders.name(i).toString() + ": " + respHeaders.value(i)).append("\n")
        }

        captureEntity.responseHeader = responseHeaderBuffer.toString()

        if (!bodyEncoded(response.headers())) {
            val source: BufferedSource = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer: Buffer = source.buffer()
            var charset: Charset? = utf8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(utf8)
            }
            if (!isPlaintext(buffer)) { //captureData.append("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)").append("\n");
                captureEntity.responseBody = "非文本信息"
                NetworkDataManager.addCaptureInfo(captureEntity)
                return response
            }
            if (contentLength != 0L) {
                captureEntity.responseBody = buffer.clone().readString(charset)
            }
            captureEntity.responseStatus = captureEntity.responseStatus + "<-- END HTTP (" + buffer.size() + "-byte body)"
        }
        NetworkDataManager.addCaptureInfo(captureEntity)
        return response

    }

    private fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size() < 64) buffer.size() else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            false // Truncated UTF-8 sequence.
        }
    }

    private fun bodyEncoded(headers: Headers) : Boolean {
        val encoding = headers.get("Content-Encoding")
        return encoding != null && encoding.toLowerCase() != "identity"
    }

}