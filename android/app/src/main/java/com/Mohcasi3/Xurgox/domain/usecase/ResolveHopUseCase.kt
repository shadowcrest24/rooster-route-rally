package com.Mohcasi3.Xurgox.domain.usecase

import com.Mohcasi3.Xurgox.core.config.GameConfig
import com.Mohcasi3.Xurgox.domain.model.CrossingLayout

enum class HopVerdict { SAFE, NEAR_MISS, COLLISION }

class ResolveHopUseCase(private val traffic: StepTrafficUseCase) {

    fun verdict(layout: CrossingLayout, targetLane: Int, column: Int, landingSec: Float): HopVerdict {
        if (targetLane < 1 || targetLane > GameConfig.LANE_COUNT) return HopVerdict.SAFE
        val lane = layout.lanes[targetLane - 1]
        val margin = traffic.safeMargin(lane, column, landingSec)
        return when {
            margin < GameConfig.FATAL_MARGIN_MS / 1000f -> HopVerdict.COLLISION
            margin < GameConfig.NEAR_MISS_WINDOW_MS / 1000f -> HopVerdict.NEAR_MISS
            else -> HopVerdict.SAFE
        }
    }

    fun laneIsClear(layout: CrossingLayout, laneIndex: Int, column: Int, timeSec: Float): Boolean {
        if (laneIndex < 1 || laneIndex > GameConfig.LANE_COUNT) return true
        val lane = layout.lanes[laneIndex - 1]
        return traffic.safeMargin(lane, column, timeSec) >= GameConfig.SAFE_WINDOW_MS / 1000f
    }
}
