package com.namazguide.data.repository

import com.namazguide.data.local.AssetQuranDataSource
import com.namazguide.data.model.domain.QuranAyahContent
import com.namazguide.data.model.domain.QuranSurahContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class QuranRepositoryImpl(
    private val dataSource: AssetQuranDataSource
) : QuranRepository {

    private val lock = Mutex()
    private var cachedSurahs: List<QuranSurahContent>? = null

    override suspend fun getAllSurahs(): RepositoryResult<List<QuranSurahContent>> = withContext(Dispatchers.IO) {
        lock.withLock {
            cachedSurahs?.let { return@withContext RepositoryResult.Success(it) }
            runCatching {
                val arabic = dataSource.loadArabic()
                val translation = dataSource.loadTranslation()
                val transliteration = dataSource.loadTransliteration()

                val merged = arabic.surahs.map { arabicSurah ->
                    val translationSurah = translation.surahs.first { it.surahNumber == arabicSurah.surahNumber }
                    val transliterationSurah = transliteration.surahs.first { it.surahNumber == arabicSurah.surahNumber }

                    QuranSurahContent(
                        surahNumber = arabicSurah.surahNumber,
                        surahNameArabic = arabicSurah.surahNameArabic,
                        surahNameEnglish = arabicSurah.surahNameEnglish,
                        totalAyahs = arabicSurah.totalAyahs,
                        ayahs = arabicSurah.ayahs.map { arabicAyah ->
                            val translationAyah = translationSurah.ayahs.first { it.ayahNumber == arabicAyah.ayahNumber }
                            val transliterationAyah = transliterationSurah.ayahs.first { it.ayahNumber == arabicAyah.ayahNumber }
                            QuranAyahContent(
                                surahNumber = arabicAyah.surahNumber,
                                ayahNumber = arabicAyah.ayahNumber,
                                arabicText = arabicAyah.text,
                                translationText = translationAyah.text,
                                transliterationText = transliterationAyah.text
                            )
                        }
                    )
                }

                cachedSurahs = merged
                RepositoryResult.Success(merged)
            }.getOrElse {
                RepositoryResult.Error("Failed to load Quran data", it)
            }
        }
    }

    override suspend fun getSurah(surahNumber: Int): RepositoryResult<QuranSurahContent> {
        return when (val result = getAllSurahs()) {
            is RepositoryResult.Error -> result
            is RepositoryResult.Success -> {
                result.data.firstOrNull { it.surahNumber == surahNumber }
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.Error("Surah $surahNumber not found")
            }
        }
    }

    override suspend fun getAyah(surahNumber: Int, ayahNumber: Int): RepositoryResult<QuranAyahContent> {
        return when (val surah = getSurah(surahNumber)) {
            is RepositoryResult.Error -> surah
            is RepositoryResult.Success -> {
                surah.data.ayahs.firstOrNull { it.ayahNumber == ayahNumber }
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.Error("Ayah $ayahNumber not found in surah $surahNumber")
            }
        }
    }
}
