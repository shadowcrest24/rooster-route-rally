package com.Mohcasi3.Xurgox.presentation.tutorial

import androidx.lifecycle.ViewModel
import com.Mohcasi3.Xurgox.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TutorialUiState(
    val step: Int = 0,
    val stepCount: Int = 4,
    val completed: Boolean = false
)

class TutorialViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TutorialUiState())
    val state: StateFlow<TutorialUiState> = _state.asStateFlow()

    fun refresh() {
        _state.update { it.copy(step = 0, completed = settingsRepository.settings().tutorialSeen) }
    }

    fun next() {
        val current = _state.value
        if (current.step >= current.stepCount - 1) {
            settingsRepository.setTutorialSeen(true)
            _state.update { it.copy(completed = true) }
            return
        }
        _state.update { it.copy(step = it.step + 1) }
    }

    fun previous() {
        _state.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }
    }
}
