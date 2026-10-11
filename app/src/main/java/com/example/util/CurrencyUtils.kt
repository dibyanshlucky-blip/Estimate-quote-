package com.example.util

import java.text.DecimalFormat
import java.util.Locale

object CurrencyUtils {

    fun formatInr(amount: Double, includeDecimals: Boolean = false): String {
        val absVal = kotlin.math.abs(amount)
        val wholePart = absVal.toLong()
        val decimalPart = kotlin.math.round((absVal - wholePart) * 100).toInt()

        val wholeStr = wholePart.toString()
        val formattedWhole = if (wholeStr.length <= 3) {
            wholeStr
        } else {
            val lastThree = wholeStr.takeLast(3)
            val rest = wholeStr.dropLast(3)
            val chunked = rest.reversed().chunked(2).joinToString(",").reversed()
            "$chunked,$lastThree"
        }

        val sign = if (amount < 0) "-" else ""
        return if (includeDecimals && decimalPart > 0) {
            "$sign₹$formattedWhole.${String.format(Locale.US, "%02d", decimalPart)}"
        } else {
            "$sign₹$formattedWhole"
        }
    }

    private val units = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    )

    private val tens = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    private fun convertTwoDigits(n: Int): String {
        return when {
            n < 20 -> units[n]
            n % 10 == 0 -> tens[n / 10]
            else -> "${tens[n / 10]} ${units[n % 10]}"
        }
    }

    private fun convertThreeDigits(n: Int): String {
        val hundred = n / 100
        val rest = n % 100
        return when {
            hundred > 0 && rest > 0 -> "${units[hundred]} Hundred and ${convertTwoDigits(rest)}"
            hundred > 0 -> "${units[hundred]} Hundred"
            else -> convertTwoDigits(rest)
        }
    }

    fun numberToWordsInr(amount: Double): String {
        var num = amount.toLong()
        if (num == 0L) return "Zero Rupees Only"

        val sb = StringBuilder()

        // Crores
        val crores = num / 10000000L
        if (crores > 0) {
            sb.append("${convertThreeDigits(crores.toInt())} Crore ")
            num %= 10000000L
        }

        // Lakhs
        val lakhs = num / 100000L
        if (lakhs > 0) {
            sb.append("${convertTwoDigits(lakhs.toInt())} Lakh ")
            num %= 100000L
        }

        // Thousands
        val thousands = num / 1000L
        if (thousands > 0) {
            sb.append("${convertTwoDigits(thousands.toInt())} Thousand ")
            num %= 1000L
        }

        // Hundreds and rest
        if (num > 0) {
            sb.append(convertThreeDigits(num.toInt()))
        }

        val result = sb.toString().trim()
        return "Rupees $result Only"
    }

    fun formatSqft(sqft: Double): String {
        val df = DecimalFormat("#,##0.##")
        return "${df.format(sqft)} sq.ft"
    }
}
