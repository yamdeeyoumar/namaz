package com.namazguide.domain.logic

data class DurationAllocation(
    val fixedActionsTotalSeconds: Int,
    val recitationTotalSeconds: Int,
    val perRakahRecitationBudgetSeconds: Int
)

class PrayerDurationAllocator(
    private val timingConfig: PrayerTimingConfig = PrayerTimingConfig()
) {
    fun allocate(totalDurationSeconds: Int, rakahCount: Int): DurationAllocation {
        val fixedPerRakah = timingConfig.takbirTime + timingConfig.rukuTime + timingConfig.qawmahTime +
            timingConfig.sujoodTime + timingConfig.secondSujoodTime + timingConfig.sittingTime

        val fixedTotal = fixedPerRakah * rakahCount
        val recitationBudget = (totalDurationSeconds - fixedTotal).coerceAtLeast(rakahCount * 20)
        val perRakah = (recitationBudget / rakahCount).coerceAtLeast(10)

        return DurationAllocation(
            fixedActionsTotalSeconds = fixedTotal,
            recitationTotalSeconds = recitationBudget,
            perRakahRecitationBudgetSeconds = perRakah
        )
    }
}
