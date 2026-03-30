package com.namazguide.data.local

import android.content.Context
import com.namazguide.data.model.local.QuranLocalFile
import kotlinx.serialization.json.Json

class AssetQuranDataSource(
    private val context: Context,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    fun loadArabic(): QuranLocalFile = read("quran/quran_arabic.json")
    fun loadTranslation(): QuranLocalFile = read("quran/quran_translation_en.json")
    fun loadTransliteration(): QuranLocalFile = read("quran/quran_transliteration_en.json")

    private fun read(path: String): QuranLocalFile {
        val content = context.assets.open(path).bufferedReader().use { it.readText() }
        return json.decodeFromString(QuranLocalFile.serializer(), content)
    }
}
