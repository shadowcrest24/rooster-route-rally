package com.Mohcasi3.Xurgox.presentation.levels

import androidx.lifecycle.ViewModel
import com.Mohcasi3.Xurgox.data.sample.SampleData
import com.Mohcasi3.Xurgox.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class StageState { CLEARED, OPEN, LOCKED }

data class StageNode(
    val index: Int,
    val nameRes: Int,
    val state: StageState,
    val bestScore: Int,
    val badgesCollected: Int,
    val badgeTotal: Int
)

data class LevelsUiState(
    val nodes: List<StageNode> = emptyList(),
    val selectedStage: Int = 1,
    val lockedNoticeStage: Int = 0
)

class LevelsViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LevelsUiState())
    val state: StateFlow<LevelsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val progress = progressRepository.progress()
        val nodes = SampleData.stages.map { stage ->
            val badges = SampleData.badgesForStage(stage.index)
            StageNode(
                index = stage.index,
                nameRes = stage.nameRes,
                state = when {
                    progress.clearedStages.contains(stage.index) -> StageState.CLEARED
                    stage.index <= progress.stagesUnlocked -> StageState.OPEN
                    else -> StageState.LOCKED
                },
                bestScore = progress.stageBestScores[stage.index] ?: 0,
                badgesCollected = badges.count { progress.collectedBadgeIds.contains(it.id) },
                badgeTotal = badges.size
            )
        }
        _state.update {
            it.copy(nodes = nodes, selectedStage = progress.selectedStage, lockedNoticeStage = 0)
        }
    }

    fun select(stage: Int) {
        val node = _state.value.nodes.firstOrNull { it.index == stage } ?: return
        if (node.state == StageState.LOCKED) {
            _state.update { it.copy(lockedNoticeStage = stage) }
            return
        }
        progressRepository.selectStage(stage)
        _state.update { it.copy(selectedStage = stage, lockedNoticeStage = 0) }
    }
}
