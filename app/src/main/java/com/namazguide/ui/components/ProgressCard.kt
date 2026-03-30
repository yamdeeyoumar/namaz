package com.namazguide.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressCard(current: Int, total: Int, stepTitle: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Rak‘ah $current of $total", style = MaterialTheme.typography.labelLarge)
            Text(stepTitle, style = MaterialTheme.typography.titleSmall)
            LinearProgressIndicator(
                progress = { current.toFloat() / total.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
