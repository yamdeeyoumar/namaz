package com.namazguide.domain.logic

import com.namazguide.data.model.domain.QuranAyahContent
import com.namazguide.data.model.domain.QuranSurahContent
import kotlin.math.abs
import kotlin.random.Random

data class SelectedAyahGroup(
    val surah: QuranSurahContent,
    val ayahs: List<QuranAyahContent>,
    val estimatedSeconds: Int
)

class RandomSurahSelector(
    private val estimator: RecitationTimingEstimator,
    private val random: Random = Random.Default
) {
    fun selectAyahGroup(
        surahs: List<QuranSurahContent>,
        targetSeconds: Int,
        usedSurahNumbers: Set<Int>
    ): SelectedAyahGroup {
        val candidates = surahs.filter { it.surahNumber != 1 }
        val nonRepeating = candidates.filterNot { usedSurahNumbers.contains(it.surahNumber) }
        val source = if (nonRepeating.isNotEmpty()) nonRepeating else candidates

        val allGroups = source.flatMap { surah -> buildGroups(surah) }
        val tolerance = (targetSeconds * 0.35f).toInt().coerceAtLeast(8)
        val nearTarget = allGroups.filter { abs(it.estimatedSeconds - targetSeconds) <= tolerance }

        return when {
            nearTarget.isNotEmpty() -> nearTarget.random(random)
            allGroups.isNotEmpty() -> allGroups.minBy { abs(it.estimatedSeconds - targetSeconds) }
            else -> SelectedAyahGroup(surahs.first(), surahs.first().ayahs.take(1), estimator.estimateAyahsSeconds(surahs.first().ayahs.take(1)))
        }
    }

    private fun buildGroups(surah: QuranSurahContent): List<SelectedAyahGroup> {
        val ayahs = surah.ayahs
        if (ayahs.isEmpty()) return emptyList()
        val groups = mutableListOf<SelectedAyahGroup>()

        for (start in ayahs.indices) {
            val maxEndExclusive = minOf(ayahs.size, start + 12)
            for (end in (start + 1)..maxEndExclusive) {
                val group = ayahs.subList(start, end)
                groups += SelectedAyahGroup(
                    surah = surah,
                    ayahs = group,
                    estimatedSeconds = estimator.estimateAyahsSeconds(group)
                )
            }
        }
        return groups
    }
}
