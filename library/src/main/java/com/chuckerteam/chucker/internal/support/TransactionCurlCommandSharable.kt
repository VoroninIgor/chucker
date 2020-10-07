package com.chuckerteam.chucker.internal.support

import android.content.Context
import com.chuckerteam.chucker.internal.data.entity.Transaction
import okio.Buffer
import okio.Source

internal class TransactionCurlCommandSharable(
    private val transaction: Transaction,
) : Sharable {
    override fun toSharableContent(context: Context): Source = Buffer().apply {
        writeUtf8("grpcurl")

        val requestBody = transaction.requestBody ?: ""
        if (requestBody.isNotEmpty()) {
            writeUtf8(" -d")
            writeUtf8(" \"${requestBody.replace("\n", "\\n")}\"")
        }

        val headers = transaction.getParsedRequestHeaders()
        if (headers?.isNotEmpty() == true) {
            writeUtf8(" -H")

            writeUtf8("\"")
            headers.forEachIndexed { index, header ->
                writeUtf8("\"${header.name}: ${header.value}\"")
                if (index > 0) {
                    writeUtf8(", ")
                }
            }
            writeUtf8("\"")
        }

        writeUtf8(" " + transaction.urlFormatted)
        writeUtf8(" " + transaction.method)

    }
}
