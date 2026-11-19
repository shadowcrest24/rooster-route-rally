package com.Mohcasi3.Xurgox.presentation.missions

import androidx.lifecycle.ViewModel
import com.Mohcasi3.Xurgox.data.sample.SampleData
import com.Mohcasi3.Xurgox.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MissionCard(
    val id: String,
    val titleRes: Int,
    val descriptionRes: Int,
    val current: Int,
    val target: Int,
    val reward: Int,
    val ready: Boolean
)

data class MissionsUiState(
    val active: List<MissionCard> = emptyList(),
    val completed: List<MissionCard> = emptyList(),
    val routePoints: Int = 0
)

class MissionsViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MissionsUiState())
    val state: StateFlow<MissionsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val progress = progressRepository.progress()
        val cards = SampleData.missions.map { mission ->
            val current = (progress.missionProgress[mission.id] ?: 0).coerceAtMost(mission.target)
            MissionCard(
                id = mission.id,
                titleRes = mission.titleRes,
                descriptionRes = mission.descriptionRes,
                current = current,
                target = mission.target,
                reward = mission.reward,
                ready = current >= mission.target
            )
        }
        val done = progress.completedMissionIds
        _state.update {
            it.copy(
                active = cards.filter { card -> !done.contains(card.id) },
                completed = cards.filter { card -> done.contains(card.id) },
                routePoints = progress.routePoints
            )
        }
    }

    fun collect(missionId: String) {
        val mission = SampleData.missions.firstOrNull { it.id == missionId } ?: return
        val progress = progressRepository.progress()
        val current = progress.missionProgress[mission.id] ?: 0
        if (current < mission.target) return
        progressRepository.collectMission(mission.id, mission.reward)
        refresh()
    }
}
