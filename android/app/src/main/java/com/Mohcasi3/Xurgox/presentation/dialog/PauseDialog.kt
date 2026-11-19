package com.Mohcasi3.Xurgox.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.Mohcasi3.Xurgox.R
import com.Mohcasi3.Xurgox.core.theme.AppPalette
import com.Mohcasi3.Xurgox.core.ui.HudPanel
import com.Mohcasi3.Xurgox.core.ui.PrimaryButton
import com.Mohcasi3.Xurgox.core.ui.SecondaryButton

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        HudPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            accent = AppPalette.Cyan,
            fill = AppPalette.SurfaceDeep.copy(alpha = 0.97f),
            contentPadding = 18.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.pause_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppPalette.TextPrimary
                )
                Text(
                    text = stringResource(R.string.pause_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppPalette.TextMuted
                )
                PrimaryButton(
                    text = stringResource(R.string.cta_resume),
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryButton(
                    text = stringResource(R.string.cta_retry_run),
                    onClick = onRestart,
                    accent = AppPalette.Amber,
                    contentColor = AppPalette.Amber,
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryButton(
                    text = stringResource(R.string.cta_menu),
                    onClick = onMenu,
                    accent = AppPalette.Cyan,
                    leadingIcon = R.drawable.icon_home,
                    iconIsPhoto = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
