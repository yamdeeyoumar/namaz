# Namaz Guide (Android, Offline)

Namaz Guide is a Kotlin + Jetpack Compose Android prototype that plans prayer recitation based on rak'ah count and target duration, fully offline.

## Features
- Landing, setup, prayer guidance, and completion screens.
- Offline Quran data from local JSON assets.
- Repository + clean domain logic for recitation timing and random surah selection.
- Duration-based planner aiming to match the selected prayer length.

## Stack
- Kotlin + Coroutines + StateFlow
- Jetpack Compose + Material 3 + Navigation Compose
- kotlinx.serialization JSON

## Offline data
Assets path: `app/src/main/assets/quran/`
- `quran_arabic.json`
- `quran_translation_en.json`
- `quran_transliteration_en.json`

## Run
Open the project in Android Studio (Hedgehog+), sync Gradle, and run on an emulator/device.
