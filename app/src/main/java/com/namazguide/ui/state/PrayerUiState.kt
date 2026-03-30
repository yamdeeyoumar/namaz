package com.namazguide.ui.state

import com.namazguide.data.model.domain.PrayerPlan
import com.namazguide.data.model.domain.PrayerStepContent

sealed class PrayerUiState {
    data object Idle : PrayerUiState()
    data object Loading : PrayerUiState()
    data class Success(
        val plan: PrayerPlan,
        val currentRakahIndex: Int = 0,
        val currentStepIndex: Int = 0,
        val showTransliteration: Boolean = true
    ) : PrayerUiState() {
        val currentStep: PrayerStepContent
            get() = plan.rakahPlans[currentRakahIndex].steps[currentStepIndex]

        val isComplete: Boolean
            get() = currentRakahIndex == plan.rakahPlans.lastIndex &&
                currentStepIndex == plan.rakahPlans.last().steps.lastIndex
    }

    data class Error(val message: String) : PrayerUiState()
}
