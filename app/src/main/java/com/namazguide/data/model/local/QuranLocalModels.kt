package com.namazguide.data.model.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuranLocalFile(
    val surahs: List<SurahLocal>
)

@Serializable
data class SurahLocal(
    @SerialName("surahNumber") val surahNumber: Int,
    @SerialName("surahNameArabic") val surahNameArabic: String,
    @SerialName("surahNameEnglish") val surahNameEnglish: String,
    @SerialName("totalAyahs") val totalAyahs: Int,
    val ayahs: List<AyahLocal>
)

@Serializable
data class AyahLocal(
    @SerialName("surahNumber") val surahNumber: Int,
    @SerialName("ayahNumber") val ayahNumber: Int,
    val text: String
)
