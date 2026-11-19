package com.Mohcasi3.Xurgox.domain.model

enum class LaneDirection { LEFT, RIGHT }

data class Lane(
    val index: Int,
    val direction: LaneDirection,
    val speed: Float,
    val spacing: Float,
    val phase: Float
)
