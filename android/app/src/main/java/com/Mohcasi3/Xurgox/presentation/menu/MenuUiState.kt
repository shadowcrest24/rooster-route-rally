package com.Mohcasi3.Xurgox.presentation.menu

import com.Mohcasi3.Xurgox.R

data class MenuUiState(
    val bestScore: Int = 0,
    val badgesCollected: Int = 0,
    val totalBadges: Int = 0,
    val stagesUnlocked: Int = 1,
    val stageCount: Int = 4,
    val selectedStage: Int = 1,
    val selectedStageNameRes: Int = R.string.stage_dawn,
    val activeMissions: Int = 0,
    val claimedTiers: Int = 0,
    val tierCount: Int = 0,
    val routePoints: Int = 0,
    val tutorialSeen: Boolean = false
)
