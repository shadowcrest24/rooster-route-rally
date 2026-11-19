package com.Mohcasi3.Xurgox.domain.model

enum class MissionKind { BADGES, LANES_IN_RUN, TIME_LEFT, STREAK, DASH, CLEAR_STAGE }

data class Mission(
    val id: String,
    val titleRes: Int,
    val descriptionRes: Int,
    val kind: MissionKind,
    val target: Int,
    val reward: Int
)
