package com.namazguide.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.namazguide.data.model.domain.PrayerConfig
import com.namazguide.ui.components.SelectionButton

@Composable
fun SetupScreen(onBegin: (PrayerConfig) -> Unit) {
    var rakah by remember { mutableIntStateOf(2) }
    var duration by remember { mutableIntStateOf(5) }
    var customRakah by remember { mutableIntStateOf(0) }
    var customDuration by remember { mutableIntStateOf(0) }
    var speedMultiplier by remember { mutableFloatStateOf(1.0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("No. of Rak‘ah")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(2, 3, 4).forEach {
                SelectionButton(text = "$it", selected = rakah == it && customRakah == 0) { rakah = it }
            }
        }
        OutlinedTextField(
            value = if (customRakah == 0) "" else customRakah.toString(),
            onValueChange = {
                customRakah = it.toIntOrNull() ?: 0
                if (customRakah > 0) rakah = customRakah
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Custom Rak‘ah") }
        )

        Text("Namaz Duration")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5, 10, 15).forEach {
                SelectionButton(text = "$it min", selected = duration == it && customDuration == 0) { duration = it }
            }
        }
        OutlinedTextField(
            value = if (customDuration == 0) "" else customDuration.toString(),
            onValueChange = {
                customDuration = it.toIntOrNull() ?: 0
                if (customDuration > 0) duration = customDuration
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Custom duration (minutes)") }
        )

        Text("Namaz Speed: ${"%.2f".format(speedMultiplier)}x")
        Slider(
            value = speedMultiplier,
            onValueChange = { speedMultiplier = it },
            valueRange = 0.7f..1.4f,
            steps = 6
        )

        Button(
            onClick = {
                onBegin(
                    PrayerConfig(
                        rakahCount = rakah,
                        durationMinutes = duration,
                        speedMultiplier = speedMultiplier
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Begin Prayer")
        }
    }
}
