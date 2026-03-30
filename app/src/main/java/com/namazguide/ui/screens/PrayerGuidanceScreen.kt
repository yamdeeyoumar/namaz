package com.namazguide.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.namazguide.ui.components.ProgressCard
import com.namazguide.ui.components.TextCard
import com.namazguide.ui.state.PrayerUiState

@Composable
fun PrayerGuidanceScreen(
    state: PrayerUiState.Success,
    onNext: () -> Unit,
    onToggleTransliteration: () -> Unit,
    onComplete: () -> Unit
) {
    val currentRakah = state.currentRakahIndex + 1
    val step = state.currentStep

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
