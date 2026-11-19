package com.Mohcasi3.Xurgox.presentation.collection

import androidx.lifecycle.ViewModel
import com.Mohcasi3.Xurgox.data.sample.SampleData
import com.Mohcasi3.Xurgox.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BadgeTile(
    val id: String,
    val nameRes: Int,
    val stage: Int,
    val lane: Int,
    val collected: Boolean
)

data class CollectionUiState(
    val tiles: List<BadgeTile> = emptyList(),
    val collected: Int = 0,
    val total: Int = 0,
    val stagesTouched: Int = 0,
    val stageCount: Int = 0,
    val selectedId: String = ""
)

class CollectionViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionUiState())
    val state: StateFlow<CollectionUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val progress = progressRepository.progress()
        val tiles = SampleData.badges.map { badge ->
            BadgeTile(
                id = badge.id,
                nameRes = badge.nameRes,
                stage = badge.stage,
                lane = badge.lane,
                collected = progress.collectedBadgeIds.contains(badge.id)
            )
        }
        _state.update {
            it.copy(
                tiles = tiles,
                collected = tiles.count { tile -> tile.collected },
                total = tiles.size,
                stagesTouched = tiles.filter { tile -> tile.collected }.map { tile -> tile.stage }.distinct().size,
                stageCount = SampleData.stages.size,
                selectedId = ""
            )
        }
    }

    fun select(id: String) {
        _state.update { it.copy(selectedId = if (it.selectedId == id) "" else id) }
    }
}
