package com.Mohcasi3.Xurgox.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.Mohcasi3.Xurgox.R
import com.Mohcasi3.Xurgox.core.theme.AppPalette
import com.Mohcasi3.Xurgox.core.ui.HudPanel
import com.Mohcasi3.Xurgox.core.ui.PrimaryButton
import com.Mohcasi3.Xurgox.core.ui.SecondaryButton

@Composable
fun ResetProgressDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        HudPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            accent = AppPalette.Magenta,
            fill = AppPalette.SurfaceDeep.copy(alpha = 0.97f),
            contentPadding = 18.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.reset_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppPalette.TextPrimary
                )
                Text(
                    text = stringResource(R.string.reset_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppPalette.TextMuted,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryButton(
                        text = stringResource(R.string.cta_cancel),
                        onClick = onCancel,
                        accent = AppPalette.TextMuted,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        text = stringResource(R.string.cta_reset),
                        onClick = onConfirm,
                        height = 50.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
