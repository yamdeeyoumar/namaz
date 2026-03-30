package com.namazguide.domain.logic

import com.namazguide.data.model.domain.PrayerConfig
import com.namazguide.data.model.domain.PrayerPlan
import com.namazguide.data.model.domain.PrayerStep
import com.namazguide.data.model.domain.PrayerStepContent
import com.namazguide.data.model.domain.QuranSurahContent
import com.namazguide.data.model.domain.RakahPlan

class PrayerRecitationPlanner(
    private val estimator: RecitationTimingEstimator,
    private val allocator: PrayerDurationAllocator,
    private val selector: RandomSurahSelector,
    private val timingConfig: PrayerTimingConfig = PrayerTimingConfig()
) {
    fun plan(config: PrayerConfig, allSurahs: List<QuranSurahContent>): PrayerPlan {
        val totalTargetSeconds = config.durationMinutes * 60
        val allocation = allocator.allocate(totalTargetSeconds, config.rakahCount)
        val fatihah = allSurahs.first { it.surahNumber == 1 }
        val fatihahSeconds = estimator.estimateSeconds(fatihah.ayahs.joinToString(" ") { it.arabicText })

        val remaining = (allocation.recitationTotalSeconds - fatihahSeconds * config.rakahCount).coerceAtLeast(config.rakahCount * 5)
        val perRakahAdditionalTarget = (remaining / config.rakahCount).coerceAtLeast(5)

        val used = mutableSetOf<Int>()
        val rakahPlans = (1..config.rakahCount).map { rakahNumber ->
            val surah = selector.selectSurah(allSurahs, perRakahAdditionalTarget, used)
            used += surah.surahNumber

            val additionalArabic = surah.ayahs.joinToString(" ") { it.arabicText }
            val additionalTranslation = surah.ayahs.joinToString(" ") { it.translationText }
            val additionalTranslit = surah.ayahs.joinToString(" ") { it.transliterationText.orEmpty() }

            val steps = listOf(
                PrayerStepContent(PrayerStep.TAKBIR, "Takbir", estimatedSeconds = timingConfig.takbirTime),
                PrayerStepContent(
                    step = PrayerStep.FATIHAH,
                    title = "Al-Fatihah",
                    arabicText = fatihah.ayahs.joinToString(" ") { it.arabicText },
                    translationText = fatihah.ayahs.joinToString(" ") { it.translationText },
                    transliterationText = fatihah.ayahs.joinToString(" ") { it.transliterationText.orEmpty() },
                    estimatedSeconds = fatihahSeconds
                ),
                PrayerStepContent(
                    step = PrayerStep.ADDITIONAL_SURAH,
                    title = surah.surahNameEnglish,
                    arabicText = additionalArabic,
                    translationText = additionalTranslation,
                    transliterationText = additionalTranslit,
                    estimatedSeconds = estimator.estimateSeconds(additionalArabic)
                ),
                PrayerStepContent(PrayerStep.RUKU, "Ruku", estimatedSeconds = timingConfig.rukuTime),
                PrayerStepContent(PrayerStep.QAWMAH, "Qawmah", estimatedSeconds = timingConfig.qawmahTime),
                PrayerStepContent(PrayerStep.SUJOOD, "Sujood", estimatedSeconds = timingConfig.sujoodTime),
                PrayerStepContent(PrayerStep.SECOND_SUJOOD, "Second Sujood", estimatedSeconds = timingConfig.secondSujoodTime)
            )
            RakahPlan(rakahNumber, surah, steps)
        }

        val estimatedTotal = rakahPlans.sumOf { rp -> rp.steps.sumOf { it.estimatedSeconds } } +
            (timingConfig.sittingTime * config.rakahCount)

        return PrayerPlan(
            config = config,
            rakahPlans = rakahPlans,
            estimatedTotalSeconds = estimatedTotal,
            targetTotalSeconds = totalTargetSeconds
        )
    }
}
