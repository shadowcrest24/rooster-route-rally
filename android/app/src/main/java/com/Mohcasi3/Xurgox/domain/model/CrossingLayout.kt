package com.Mohcasi3.Xurgox.domain.model

data class CrossingLayout(
    val seed: Long,
    val lanes: List<Lane>,
    val badges: List<BoardCell>,
    val perches: List<BoardCell>,
    val fallback: Boolean
)
