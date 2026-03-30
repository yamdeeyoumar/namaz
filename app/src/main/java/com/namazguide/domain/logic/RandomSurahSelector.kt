package com.namazguide.domain.logic

import com.namazguide.data.model.domain.QuranSurahContent
import kotlin.random.Random

class RandomSurahSelector(
    private val estimator: RecitationTimingEstimator,
    private val random: Random = Random.Default
) {
    fun selectSurah(
        surahs: List<QuranSurahContent>,
        targetSeconds: Int,
        usedSurahNumbers: Set<Int>
    ): QuranSurahContent {
        val candidates = surahs.filter { it.surahNumber != 1 }
        val nonRepeating = candidates.filterNot { usedSurahNumbers.contains(it.surahNumber) }
        val source = if (nonRepeating.isNotEmpty()) nonRepeating else candidates

        val withDuration = source.map { it to estimator.estimateSeconds(it.ayahs.joinToString(" ") { ayah -> ayah.arabicText }) }
        val tightWindow = withDuration.filter { (_, sec) -> kotlin.math.abs(sec - targetSeconds) <= 15 }

        return when {
            tightWindow.isNotEmpty() -> tightWindow.random(random).first
            withDuration.isNotEmpty() -> withDuration.minBy { (_, sec) -> kotlin.math.abs(sec - targetSeconds) }.first
            else -> surahs.first { it.surahNumber == 1 }
        }
    }
}
