package com.chuckerteam.chucker.internal.support

import android.content.Context
import com.chuckerteam.chucker.internal.data.entity.Transaction
import java.util.regex.Matcher
import java.util.regex.Pattern
import okio.Buffer
import okio.Source

internal class TransactionCurlCommandSharable(
    private val transaction: Transaction,
) : Sharable {
    override fun toSharableContent(context: Context): Source = Buffer().apply {
        writeUtf8("grpcurl")

        val headers = transaction.getParsedRequestHeaders()
        if (headers?.isNotEmpty() == true) {
            writeUtf8(" -H ")

            writeUtf8("\"")
            headers.forEachIndexed { index, header ->
                writeUtf8("${header.name}: ${header.value}")
                if (index > 0) {
                    writeUtf8(", ")
                }
            }
            writeUtf8("\"")
        }

        var requestBody = (transaction.requestBody ?: "").replace("\n", "")
        if (requestBody.isNotEmpty()) {
            writeUtf8(" -d")
            writeUtf8(" \"{")

            val pattern: Pattern = Pattern.compile("[a-zA-Z]+")
            val matcher: Matcher = pattern.matcher(requestBody)
            var list = ArrayList<String>()
            while (matcher.find()) {
                list.add(matcher.group())
            }
            list = removeDuplicates(list)
            list.forEach { word->
                requestBody = requestBody.replace(word, "\\\"$word\\\"")
            }

            writeUtf8(requestBody)
            writeUtf8("}\"")
        }

        writeUtf8(" " + transaction.urlFormatted)
        writeUtf8(" " + transaction.method)
    }

    private fun <T> removeDuplicates(list: ArrayList<T>): ArrayList<T> {
        val newList = ArrayList<T>()

        for (element in list) {
            if (!newList.contains(element)) {
                newList.add(element)
            }
        }

        return newList
    }
}
