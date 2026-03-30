package com.namazguide.domain.logic

data class PrayerTimingConfig(
    val takbirTime: Int = 4,
    val rukuTime: Int = 7,
    val qawmahTime: Int = 5,
    val sujoodTime: Int = 8,
    val secondSujoodTime: Int = 8,
    val sittingTime: Int = 4
)
