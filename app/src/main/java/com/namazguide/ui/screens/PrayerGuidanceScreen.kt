package com.namazguide.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.namazguide.ui.components.ProgressCard
import com.namazguide.ui.components.TextCard
import com.namazguide.ui.state.PrayerUiState
import kotlinx.coroutines.delay

@Composable
fun PrayerGuidanceScreen(
    state: PrayerUiState.Success,
    onNext: () -> Unit,
    onToggleTransliteration: () -> Unit,
    onToggleAutoAdvance: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onComplete: () -> Unit
) {
    val currentRakah = state.currentRakahIndex + 1
    val step = state.currentStep
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80) }
    var hasPlayedInitialStepTone by remember { mutableStateOf(false) }

    LaunchedEffect(state.currentRakahIndex, state.currentStepIndex) {
        if (hasPlayedInitialStepTone) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        } else {
            hasPlayedInitialStepTone = true
        }
    }

    LaunchedEffect(state.currentRakahIndex, state.currentStepIndex, state.autoAdvance, state.playbackSpeed) {
        if (!state.autoAdvance || state.isComplete) return@LaunchedEffect
        val adjustedMs = ((step.estimatedSeconds.toFloat() / state.playbackSpeed) * 1000).toLong().coerceAtLeast(1200L)
        delay(adjustedMs)
        onNext()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProgressCard(
            current = currentRakah,
            total = state.plan.config.rakahCount,
            stepTitle = step.title
        )

        TextCard(
            title = step.title,
            arabic = step.arabicText,
            translation = step.translationText,
            transliteration = if (state.showTransliteration) step.transliterationText else null
        )

        Text("Guidance speed: ${"%.2f".format(state.playbackSpeed)}x")
        Slider(
            value = state.playbackSpeed,
            onValueChange = onSpeedChange,
            valueRange = 0.7f..1.4f,
            steps = 6
        )

        Button(onClick = onToggleAutoAdvance, modifier = Modifier.fillMaxWidth()) {
            Text(if (state.autoAdvance) "Auto Scroll: ON" else "Auto Scroll: OFF")
        }

        Button(onClick = onToggleTransliteration, modifier = Modifier.fillMaxWidth()) {
            Text(if (state.showTransliteration) "Hide Transliteration" else "Show Transliteration")
        }

        Button(
            onClick = {
                if (state.isComplete) onComplete() else onNext()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isComplete) "Complete Prayer" else "Next")
        }
    }
}
