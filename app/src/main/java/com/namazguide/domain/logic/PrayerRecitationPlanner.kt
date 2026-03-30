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
        val fatihahSeconds = estimator.estimateAyahsSeconds(fatihah.ayahs)

        val remaining = (allocation.recitationTotalSeconds - fatihahSeconds * config.rakahCount)
            .coerceAtLeast(config.rakahCount * 6)
        val perRakahAdditionalTarget = (remaining / config.rakahCount).coerceAtLeast(6)

        val used = mutableSetOf<Int>()
        val rakahPlans = (1..config.rakahCount).map { rakahNumber ->
            val selection = selector.selectAyahGroup(allSurahs, perRakahAdditionalTarget, used)
            used += selection.surah.surahNumber

            val additionalArabic = selection.ayahs.joinToString(" ") { it.arabicText }
            val additionalTranslation = selection.ayahs.joinToString(" ") { it.translationText }
            val additionalTranslit = selection.ayahs.joinToString(" ") { it.transliterationText.orEmpty() }
            val range = selection.ayahs.first().ayahNumber..selection.ayahs.last().ayahNumber
            val isFinalRakah = rakahNumber == config.rakahCount
            val isMiddleTashahhud = config.rakahCount > 1 && rakahNumber == 2 && !isFinalRakah

            val steps = buildList {
                add(
                PrayerStepContent(
                    PrayerStep.TAKBIR,
                    "Takbir (Qiyam)",
                    arabicText = "اللّٰهُ أَكْبَرُ",
                    translationText = "Allah is the Greatest",
                    transliterationText = "Allahu Akbar",
                    estimatedSeconds = timingConfig.takbirTime
                ))
                add(
                PrayerStepContent(
                    step = PrayerStep.FATIHAH,
                    title = "Al-Fatihah",
                    arabicText = fatihah.ayahs.joinToString(" ") { it.arabicText },
                    translationText = fatihah.ayahs.joinToString(" ") { it.translationText },
                    transliterationText = fatihah.ayahs.joinToString(" ") { it.transliterationText.orEmpty() },
                    estimatedSeconds = fatihahSeconds
                ))
                add(
                PrayerStepContent(
                    step = PrayerStep.ADDITIONAL_AYAHS,
                    title = "${selection.surah.surahNameEnglish} (${range.first}-${range.last})",
                    arabicText = additionalArabic,
                    translationText = additionalTranslation,
                    transliterationText = additionalTranslit,
                    estimatedSeconds = selection.estimatedSeconds
                ))
                add(
                    PrayerStepContent(
                        PrayerStep.TAKBIR,
                        "Takbir (to Ruku)",
                        arabicText = "اللّٰهُ أَكْبَرُ",
                        translationText = "Allah is the Greatest",
                        transliterationText = "Allahu Akbar",
                        estimatedSeconds = timingConfig.takbirTime
                    )
                )
                add(
                PrayerStepContent(
                    PrayerStep.RUKU,
                    "Ruku",
                    arabicText = "سُبْحَانَ رَبِّيَ الْعَظِيمِ",
                    translationText = "Glory be to my Lord, the Magnificent",
                    transliterationText = "Subhana Rabbiyal Adheem",
                    estimatedSeconds = timingConfig.rukuTime
                ))
                add(
                PrayerStepContent(
                    PrayerStep.QAWMAH,
                    "Qawmah",
                    arabicText = "سَمِعَ اللّٰهُ لِمَنْ حَمِدَهُ رَبَّنَا وَلَكَ الْحَمْدُ",
                    translationText = "Allah hears those who praise Him. Our Lord, all praise is for You",
                    transliterationText = "Sami Allahu liman hamidah, Rabbana lakal hamd",
                    estimatedSeconds = timingConfig.qawmahTime
                ))
                add(
                    PrayerStepContent(
                        PrayerStep.TAKBIR,
                        "Takbir (to Sujood)",
                        arabicText = "اللّٰهُ أَكْبَرُ",
                        translationText = "Allah is the Greatest",
                        transliterationText = "Allahu Akbar",
                        estimatedSeconds = timingConfig.takbirTime
                    )
                )
                add(
                PrayerStepContent(
                    PrayerStep.SUJOOD,
                    "Sujood",
                    arabicText = "سُبْحَانَ رَبِّيَ الْأَعْلَى",
                    translationText = "Glory be to my Lord, the Most High",
                    transliterationText = "Subhana Rabbiyal A'la",
                    estimatedSeconds = timingConfig.sujoodTime
                ))
                add(
                    PrayerStepContent(
                        PrayerStep.TAKBIR,
                        "Takbir (between Sujood)",
                        arabicText = "اللّٰهُ أَكْبَرُ",
                        translationText = "Allah is the Greatest",
                        transliterationText = "Allahu Akbar",
                        estimatedSeconds = timingConfig.takbirTime
                    )
                )
                add(
                PrayerStepContent(
                    PrayerStep.SECOND_SUJOOD,
                    "Second Sujood",
                    arabicText = "سُبْحَانَ رَبِّيَ الْأَعْلَى",
                    translationText = "Glory be to my Lord, the Most High",
                    transliterationText = "Subhana Rabbiyal A'la",
                    estimatedSeconds = timingConfig.secondSujoodTime
                ))

                if (isMiddleTashahhud) {
                    add(
                        PrayerStepContent(
                            PrayerStep.ADDITIONAL_AYAHS,
                            "Tashahhud",
                            arabicText = "التَّحِيَّاتُ لِلّٰهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ",
                            translationText = "All compliments, prayers and pure words are due to Allah",
                            transliterationText = "Attahiyyatu lillahi was-salawatu wat-tayyibat",
                            estimatedSeconds = timingConfig.tashahhudTime
                        )
                    )
                }

                if (isFinalRakah) {
                    add(
                        PrayerStepContent(
                            PrayerStep.ADDITIONAL_AYAHS,
                            "Final Tashahhud + Durood",
                            arabicText = "التَّحِيَّاتُ لِلّٰهِ ... اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ",
                            translationText = "Tashahhud and sending blessings upon the Prophet ﷺ",
                            transliterationText = "Attahiyyatu ... Allahumma salli ala Muhammad",
                            estimatedSeconds = timingConfig.tashahhudWithDuroodTime
                        )
                    )
                }
            }
            RakahPlan(
                rakahNumber = rakahNumber,
                selectedSurah = selection.surah,
                selectedAyahRange = range,
                steps = steps
            )
        }

        val estimatedTotal = rakahPlans.sumOf { rp -> rp.steps.sumOf { it.estimatedSeconds } }

        return PrayerPlan(
            config = config,
            rakahPlans = rakahPlans,
            estimatedTotalSeconds = estimatedTotal,
            targetTotalSeconds = totalTargetSeconds
        )
    }
}
