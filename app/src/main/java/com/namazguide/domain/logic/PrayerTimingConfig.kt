package com.namazguide.domain.logic

data class PrayerTimingConfig(
    val takbirTime: Int = 3,
    val rukuTime: Int = 10,
    val qawmahTime: Int = 5,
    val sujoodTime: Int = 10,
    val secondSujoodTime: Int = 10,
    val tashahhudTime: Int = 35,
    val tashahhudWithDuroodTime: Int = 60
)
