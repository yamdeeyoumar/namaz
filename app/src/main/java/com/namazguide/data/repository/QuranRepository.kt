package com.namazguide.data.repository

import com.namazguide.data.model.domain.QuranAyahContent
import com.namazguide.data.model.domain.QuranSurahContent

interface QuranRepository {
    suspend fun getAllSurahs(): RepositoryResult<List<QuranSurahContent>>
    suspend fun getSurah(surahNumber: Int): RepositoryResult<QuranSurahContent>
    suspend fun getAyah(surahNumber: Int, ayahNumber: Int): RepositoryResult<QuranAyahContent>
}
