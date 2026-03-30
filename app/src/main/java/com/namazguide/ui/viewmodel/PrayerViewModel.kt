package com.namazguide.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.namazguide.data.local.AssetQuranDataSource
import com.namazguide.data.model.domain.PrayerConfig
import com.namazguide.data.repository.QuranRepositoryImpl
import com.namazguide.data.repository.RepositoryResult
import com.namazguide.domain.logic.PrayerDurationAllocator
import com.namazguide.domain.logic.PrayerRecitationPlanner
import com.namazguide.domain.logic.RandomSurahSelector
import com.namazguide.domain.logic.RecitationTimingEstimator
import com.namazguide.ui.state.PrayerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrayerViewModel(app: Application) : AndroidViewModel(app) {
    private val estimator = RecitationTimingEstimator()
    private val planner = PrayerRecitationPlanner(
        estimator = estimator,
        allocator = PrayerDurationAllocator(),
        selector = RandomSurahSelector(estimator)
    )
    private val repository = QuranRepositoryImpl(AssetQuranDataSource(app.applicationContext))

    private val _uiState = MutableStateFlow<PrayerUiState>(PrayerUiState.Idle)
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    fun startPrayer(config: PrayerConfig) {
        viewModelScope.launch {
            _uiState.value = PrayerUiState.Loading
            when (val surahs = repository.getAllSurahs()) {
                is RepositoryResult.Error -> _uiState.value = PrayerUiState.Error(surahs.message)
                is RepositoryResult.Success -> {
                    val plan = planner.plan(config, surahs.data)
                    _uiState.value = PrayerUiState.Success(plan = plan, playbackSpeed = config.speedMultiplier)
                }
            }
        }
    }

    fun nextStep() {
        val current = _uiState.value as? PrayerUiState.Success ?: return
        val currentRakah = current.currentRakahIndex
        val currentStep = current.currentStepIndex
        val rakahSteps = current.plan.rakahPlans[currentRakah].steps

        _uiState.value = if (currentStep < rakahSteps.lastIndex) {
            current.copy(currentStepIndex = currentStep + 1)
        } else if (currentRakah < current.plan.rakahPlans.lastIndex) {
            current.copy(currentRakahIndex = currentRakah + 1, currentStepIndex = 0)
        } else {
            current
        }
    }

    fun toggleTransliteration() {
        val current = _uiState.value as? PrayerUiState.Success ?: return
        _uiState.value = current.copy(showTransliteration = !current.showTransliteration)
    }

    fun toggleAutoAdvance() {
        val current = _uiState.value as? PrayerUiState.Success ?: return
        _uiState.value = current.copy(autoAdvance = !current.autoAdvance)
    }

    fun setPlaybackSpeed(value: Float) {
        val current = _uiState.value as? PrayerUiState.Success ?: return
        _uiState.value = current.copy(playbackSpeed = value)
    }

    fun reset() {
        _uiState.value = PrayerUiState.Idle
    }
}
