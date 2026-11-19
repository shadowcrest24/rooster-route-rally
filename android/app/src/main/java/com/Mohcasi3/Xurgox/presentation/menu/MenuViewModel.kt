package com.Mohcasi3.Xurgox.presentation.menu

import androidx.lifecycle.ViewModel
import com.Mohcasi3.Xurgox.core.config.GameConfig
import com.Mohcasi3.Xurgox.data.sample.SampleData
import com.Mohcasi3.Xurgox.domain.repository.ProgressRepository
import com.Mohcasi3.Xurgox.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val progress = progressRepository.progress()
        val settings = settingsRepository.settings()
        val stage = SampleData.stageOf(progress.selectedStage)
        val active = SampleData.missions.count { !progress.completedMissionIds.contains(it.id) }
        _state.update {
            it.copy(
                bestScore = progress.bestScore,
                badgesCollected = progress.collectedBadgeIds.size,
                totalBadges = SampleData.badges.size,
                stagesUnlocked = progress.stagesUnlocked,
                stageCount = GameConfig.STAGE_COUNT,
                selectedStage = progress.selectedStage,
                selectedStageNameRes = stage.nameRes,
                activeMissions = active,
                claimedTiers = progress.claimedTierIds.size,
                tierCount = SampleData.rewardTiers.size,
                routePoints = progress.routePoints,
                tutorialSeen = settings.tutorialSeen
            )
        }
    }
}
