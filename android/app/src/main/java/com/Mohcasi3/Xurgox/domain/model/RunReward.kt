package com.Mohcasi3.Xurgox.domain.model

data class RunReward(
    val progress: PlayerProgress,
    val routePointsEarned: Int,
    val newBest: Boolean,
    val missionGains: List<MissionGain>
)

data class MissionGain(
    val missionId: String,
    val current: Int,
    val target: Int,
    val completed: Boolean
)
