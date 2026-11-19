package com.Mohcasi3.Xurgox.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Mohcasi3.Xurgox.core.theme.AppPalette

@Composable
fun StatCard(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    if (value.isBlank()) return
    HudPanel(
        modifier = modifier,
        accent = accent,
        bracketLength = 12.dp,
        contentPadding = 10.dp
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Spacer(
                modifier = Modifier
                    .size(8.dp)
                    .background(accent, CircleShape)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 22.sp),
                color = accent,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = AppPalette.TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatCardRow(
    cards: List<StatCardData>,
    modifier: Modifier = Modifier
) {
    val visible = cards.filter { it.value.isNotBlank() }
    if (visible.size < 2) return
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        visible.forEach { card ->
            StatCard(
                value = card.value,
                label = card.label,
                accent = card.accent,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

data class StatCardData(val value: String, val label: String, val accent: Color)
