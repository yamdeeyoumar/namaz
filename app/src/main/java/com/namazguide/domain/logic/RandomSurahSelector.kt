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
        val tolerance = (targetSeconds * 0.25f).toInt().coerceAtLeast(8)
        val nearTarget = allGroups.filter { abs(it.estimatedSeconds - targetSeconds) <= tolerance }
        val closestCandidates = allGroups
            .sortedBy { abs(it.estimatedSeconds - targetSeconds) }
            .take(15)

        return when {
            nearTarget.isNotEmpty() -> nearTarget.random(random)
            closestCandidates.isNotEmpty() -> closestCandidates.random(random)
            allGroups.isNotEmpty() -> allGroups.random(random)
            else -> {
                val fallbackSurah = candidates.firstOrNull() ?: surahs.first()
                val fallbackAyahs = fallbackSurah.ayahs.take(1)
                SelectedAyahGroup(
                    fallbackSurah,
                    fallbackAyahs,
                    estimator.estimateAyahsSeconds(fallbackAyahs)
                )
            }
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
