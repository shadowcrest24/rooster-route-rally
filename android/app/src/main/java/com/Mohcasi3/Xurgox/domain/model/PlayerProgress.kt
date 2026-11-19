package com.Mohcasi3.Xurgox.domain.model

data class PlayerProgress(
    val bestScore: Int,
    val bestStreak: Int,
    val routePoints: Int,
    val stagesUnlocked: Int,
    val selectedStage: Int,
    val collectedBadgeIds: Set<String>,
    val claimedTierIds: Set<String>,
    val completedMissionIds: Set<String>,
    val missionProgress: Map<String, Int>,
    val stageBestScores: Map<Int, Int>,
    val clearedStages: Set<Int>
)
