package com.namazguide.data.model.domain

data class QuranAyahContent(
    val surahNumber: Int,
    val ayahNumber: Int,
    val arabicText: String,
    val translationText: String,
    val transliterationText: String?
)

data class QuranSurahContent(
    val surahNumber: Int,
    val surahNameArabic: String,
    val surahNameEnglish: String,
    val totalAyahs: Int,
    val ayahs: List<QuranAyahContent>
)

data class PrayerConfig(
    val rakahCount: Int,
    val durationMinutes: Int,
    val speedMultiplier: Float = 1.0f
)

enum class PrayerStep {
    TAKBIR,
    FATIHAH,
    ADDITIONAL_AYAHS,
    RUKU,
    QAWMAH,
    SUJOOD,
    SECOND_SUJOOD
}

data class PrayerStepContent(
    val step: PrayerStep,
    val title: String,
    val arabicText: String? = null,
    val translationText: String? = null,
    val transliterationText: String? = null,
    val estimatedSeconds: Int = 0
)

data class RakahProgress(
    val current: Int,
    val total: Int
)

data class RakahPlan(
    val rakahNumber: Int,
    val selectedSurah: QuranSurahContent,
    val selectedAyahRange: IntRange,
    val steps: List<PrayerStepContent>
)

data class PrayerPlan(
    val config: PrayerConfig,
    val rakahPlans: List<RakahPlan>,
    val estimatedTotalSeconds: Int,
    val targetTotalSeconds: Int
)
