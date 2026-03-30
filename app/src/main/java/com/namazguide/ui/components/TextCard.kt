package com.namazguide.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TextCard(title: String, arabic: String?, translation: String?, transliteration: String?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (!arabic.isNullOrBlank()) {
                Text(
                    text = arabic,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (!translation.isNullOrBlank()) {
                Text(text = translation, style = MaterialTheme.typography.bodyMedium)
            }
            if (!transliteration.isNullOrBlank()) {
                Text(text = transliteration, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
