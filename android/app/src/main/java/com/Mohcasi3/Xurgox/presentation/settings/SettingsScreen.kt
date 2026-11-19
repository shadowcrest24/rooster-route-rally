package com.Mohcasi3.Xurgox.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Mohcasi3.Xurgox.R
import com.Mohcasi3.Xurgox.core.di.ServiceLocator
import com.Mohcasi3.Xurgox.core.theme.AppPalette
import com.Mohcasi3.Xurgox.core.ui.HudPanel
import com.Mohcasi3.Xurgox.core.ui.HudShape
import com.Mohcasi3.Xurgox.core.ui.ScreenFrame
import com.Mohcasi3.Xurgox.core.ui.SecondaryButton
import com.Mohcasi3.Xurgox.domain.model.ControlSide
import com.Mohcasi3.Xurgox.presentation.dialog.ResetProgressDialog

@Composable
fun SettingsScreen(onMenu: () -> Unit) {
    val viewModel: SettingsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    SettingsContent(
        state = state,
        onHighContrast = viewModel::setHighContrast,
        onRhythmHints = viewModel::setRhythmHints,
        onControlSide = viewModel::setControlSide,
        onAskReset = viewModel::askReset,
        onCancelReset = viewModel::cancelReset,
        onConfirmReset = viewModel::confirmReset,
        onMenu = onMenu
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onHighContrast: (Boolean) -> Unit,
    onRhythmHints: (Boolean) -> Unit,
    onControlSide: (ControlSide) -> Unit,
    onAskReset: () -> Unit,
    onCancelReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onMenu: () -> Unit
) {
    ScreenFrame(
        title = stringResource(R.string.title_settings),
        subtitle = stringResource(R.string.subtitle_settings),
        onMenu = onMenu
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SwitchRow(
                titleRes = R.string.setting_contrast,
                hintRes = R.string.setting_contrast_hint,
                checked = state.highContrast,
                onChange = onHighContrast
            )
            SwitchRow(
                titleRes = R.string.setting_hints,
                hintRes = R.string.setting_hints_hint,
                checked = state.rhythmHints,
                onChange = onRhythmHints
            )
            HudPanel(modifier = Modifier.fillMaxWidth(), accent = AppPalette.Cyan, contentPadding = 14.dp) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.setting_side),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = AppPalette.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = stringResource(R.string.setting_side_hint),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = AppPalette.TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SegmentOption(
                            labelRes = R.string.cta_left,
                            selected = state.controlSide == ControlSide.LEFT,
                            onSelect = { onControlSide(ControlSide.LEFT) },
                            modifier = Modifier.weight(1f)
                        )
                        SegmentOption(
                            labelRes = R.string.cta_right,
                            selected = state.controlSide == ControlSide.RIGHT,
                            onSelect = { onControlSide(ControlSide.RIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            HudPanel(modifier = Modifier.fillMaxWidth(), accent = AppPalette.Magenta, contentPadding = 14.dp) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.setting_reset),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = AppPalette.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = stringResource(R.string.setting_reset_hint),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = AppPalette.TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SecondaryButton(
                        text = stringResource(R.string.cta_reset),
                        onClick = onAskReset,
                        height = 50.dp,
                        accent = AppPalette.Magenta,
                        contentColor = AppPalette.Magenta,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.settings_footer),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = AppPalette.TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
    if (state.resetDialogVisible) {
        ResetProgressDialog(onConfirm = onConfirmReset, onCancel = onCancelReset)
    }
}

@Composable
private fun SwitchRow(
    titleRes: Int,
    hintRes: Int,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val stateLabel = if (checked) stringResource(R.string.state_on) else stringResource(R.string.state_off)
    HudPanel(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        accent = if (checked) AppPalette.Cyan else AppPalette.TextMuted,
        contentPadding = 14.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = AppPalette.TextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = stringResource(hintRes),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = AppPalette.TextMuted
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AppPalette.BackgroundDeep,
                    checkedTrackColor = AppPalette.Cyan,
                    uncheckedThumbColor = AppPalette.TextMuted,
                    uncheckedTrackColor = AppPalette.Inactive
                ),
                modifier = Modifier.semantics { stateDescription = stateLabel }
            )
        }
    }
}

@Composable
private fun SegmentOption(
    labelRes: Int,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = if (selected) AppPalette.Cyan else AppPalette.TextMuted
    val stateLabel = if (selected) stringResource(R.string.state_on) else stringResource(R.string.state_off)
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .background(
                if (selected) AppPalette.Cyan.copy(alpha = 0.18f) else AppPalette.SurfaceAlt.copy(alpha = 0.7f),
                HudShape
            )
            .border(1.dp, accent.copy(alpha = 0.6f), HudShape)
            .clickable(onClick = onSelect)
            .semantics { stateDescription = stateLabel }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp),
            color = accent
        )
    }
}
