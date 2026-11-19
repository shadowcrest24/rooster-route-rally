package com.Mohcasi3.Xurgox.presentation.gameover

import androidx.lifecycle.ViewModel
import com.Mohcasi3.Xurgox.data.sample.SampleData
import com.Mohcasi3.Xurgox.domain.model.RunResult
import com.Mohcasi3.Xurgox.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MissionLine(
    val titleRes: Int,
    val current: Int,
    val target: Int
)

data class GameOverUiState(
    val bound: Boolean = false,
    val newBest: Boolean = false,
    val bestScore: Int = 0,
    val routePointsEarned: Int = 0,
    val routePoints: Int = 0,
    val missionLines: List<MissionLine> = emptyList(),
    val stageUnlocked: Boolean = false
)

class GameOverViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    private var boundSeed: RunResult? = null

    fun bind(result: RunResult) {
        if (boundSeed == result) return
        boundSeed = result
        val reward = progressRepository.saveRun(result)
        val lines = reward.missionGains.mapNotNull { gain ->
            val mission = SampleData.missions.firstOrNull { it.id == gain.missionId } ?: return@mapNotNull null
            MissionLine(mission.titleRes, gain.current, gain.target)
        }.take(2)
        _state.update {
            it.copy(
                bound = true,
                newBest = reward.newBest,
                bestScore = reward.progress.bestScore,
                routePointsEarned = reward.routePointsEarned,
                routePoints = reward.progress.routePoints,
                missionLines = lines,
                stageUnlocked = result.stageCleared
            )
        }
    }
}
