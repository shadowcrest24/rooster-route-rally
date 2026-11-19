package com.Mohcasi3.Xurgox.presentation.rewards

import androidx.lifecycle.ViewModel
import com.Mohcasi3.Xurgox.data.sample.SampleData
import com.Mohcasi3.Xurgox.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TierRow(
    val id: String,
    val nameRes: Int,
    val iconRes: Int,
    val cost: Int,
    val claimed: Boolean,
    val affordable: Boolean,
    val missing: Int
)

data class RewardsUiState(
    val routePoints: Int = 0,
    val claimedCount: Int = 0,
    val tierCount: Int = 0,
    val nextTierCost: Int = 0,
    val nextTierProgress: Float = 0f,
    val rows: List<TierRow> = emptyList(),
    val justClaimedId: String = ""
)

class RewardsViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RewardsUiState())
    val state: StateFlow<RewardsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        publish("")
    }

    fun claim(tierId: String) {
        val tier = SampleData.rewardTiers.firstOrNull { it.id == tierId } ?: return
        progressRepository.claimTier(tier.id, tier.cost)
        publish(tier.id)
    }

    private fun publish(justClaimed: String) {
        val progress = progressRepository.progress()
        val rows = SampleData.rewardTiers.map { tier ->
            val claimed = progress.claimedTierIds.contains(tier.id)
            TierRow(
                id = tier.id,
                nameRes = tier.nameRes,
                iconRes = tier.iconRes,
                cost = tier.cost,
                claimed = claimed,
                affordable = !claimed && progress.routePoints >= tier.cost,
                missing = (tier.cost - progress.routePoints).coerceAtLeast(0)
            )
        }
        val next = rows.firstOrNull { !it.claimed }
        _state.update {
            it.copy(
                routePoints = progress.routePoints,
                claimedCount = rows.count { row -> row.claimed },
                tierCount = rows.size,
                nextTierCost = next?.cost ?: 0,
                nextTierProgress = if (next == null || next.cost == 0) {
                    1f
                } else {
                    (progress.routePoints.toFloat() / next.cost).coerceIn(0f, 1f)
                },
                rows = rows,
                justClaimedId = justClaimed
            )
        }
    }
}
