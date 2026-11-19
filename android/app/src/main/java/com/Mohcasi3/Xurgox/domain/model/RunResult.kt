package com.Mohcasi3.Xurgox.domain.model

enum class RunOutcome { CLEARED, COLLISION, TIMEOUT }

data class RunResult(
    val outcome: RunOutcome,
    val stage: Int,
    val score: Int,
    val badges: Int,
    val lanes: Int,
    val crossings: Int,
    val bestStreak: Int,
    val secondsLeft: Int,
    val collisionLane: Int,
    val dashesUsed: Int,
    val stageCleared: Boolean,
    val collectedBadgeIds: List<String>
)
