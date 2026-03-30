package com.namazguide.domain.logic

import com.namazguide.data.model.domain.QuranAyahContent
import kotlin.math.ceil

class RecitationTimingEstimator(
    private val secondsPerWord: Double = 30.0 / 27.0
) {
    fun wordCount(text: String): Int = text.trim().split(Regex("\\s+")).count { it.isNotBlank() }
    fun charCount(text: String): Int = text.filterNot { it.isWhitespace() }.length

    fun estimateSeconds(text: String): Int {
        val words = wordCount(text)
        return if (words > 0) {
            ceil(words * secondsPerWord).toInt().coerceAtLeast(1)
        } else {
            ceil(charCount(text) * 0.25).toInt().coerceAtLeast(1)
        }
    }

    fun estimateAyahsSeconds(ayahs: List<QuranAyahContent>): Int {
        return ayahs.sumOf { estimateSeconds(it.arabicText) }
    }
}
