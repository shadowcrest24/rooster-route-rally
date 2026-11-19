package com.Mohcasi3.Xurgox.presentation.settings

import androidx.lifecycle.ViewModel
import com.Mohcasi3.Xurgox.domain.model.ControlSide
import com.Mohcasi3.Xurgox.domain.repository.ProgressRepository
import com.Mohcasi3.Xurgox.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val highContrast: Boolean = false,
    val rhythmHints: Boolean = true,
    val controlSide: ControlSide = ControlSide.RIGHT,
    val resetDialogVisible: Boolean = false,
    val resetDone: Boolean = false
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val settings = settingsRepository.settings()
        _state.update {
            it.copy(
                highContrast = settings.highContrast,
                rhythmHints = settings.rhythmHints,
                controlSide = settings.controlSide,
                resetDialogVisible = false
            )
        }
    }

    fun setHighContrast(value: Boolean) {
        settingsRepository.setHighContrast(value)
        _state.update { it.copy(highContrast = value) }
    }

    fun setRhythmHints(value: Boolean) {
        settingsRepository.setRhythmHints(value)
        _state.update { it.copy(rhythmHints = value) }
    }

    fun setControlSide(side: ControlSide) {
        settingsRepository.setControlSide(side)
        _state.update { it.copy(controlSide = side) }
    }

    fun askReset() {
        _state.update { it.copy(resetDialogVisible = true) }
    }

    fun cancelReset() {
        _state.update { it.copy(resetDialogVisible = false) }
    }

    fun confirmReset() {
        progressRepository.reset()
        _state.update { it.copy(resetDialogVisible = false, resetDone = true) }
    }
}
