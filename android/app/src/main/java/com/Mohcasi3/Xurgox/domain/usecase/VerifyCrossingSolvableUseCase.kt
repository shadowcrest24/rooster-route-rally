package com.Mohcasi3.Xurgox.domain.usecase

import com.Mohcasi3.Xurgox.core.config.GameConfig
import com.Mohcasi3.Xurgox.domain.model.CrossingLayout

class VerifyCrossingSolvableUseCase(private val traffic: StepTrafficUseCase) {

    fun isSolvable(layout: CrossingLayout): Boolean = secondsToCross(layout) >= 0f

    fun secondsToCross(layout: CrossingLayout): Float {
        val hop = GameConfig.HOP_DURATION_MS / 1000f
        val shift = GameConfig.SHIFT_DURATION_MS / 1000f
        val limit = GameConfig.SOLVE_LIMIT_MS / 1000f
        val step = 0.02f
        val dwell = 0.45f
        var time = 0f
        var lane = 0
        var column = GameConfig.COLUMN_COUNT / 2
        var waited = 0f
        while (time < limit) {
            if (lane >= GameConfig.GOAL_LANE) return time
            val target = lane + 1
            if (target >= GameConfig.GOAL_LANE) {
                time += hop
                lane = target
                continue
            }
            val laneModel = layout.lanes[target - 1]
            val landing = time + hop
            val margin = traffic.safeMargin(laneModel, column, landing)
            if (margin >= dwell) {
                time += hop
                lane = target
                waited = 0f
                continue
            }
            waited += step
            time += step
            if (waited >= 1.2f) {
                var bestColumn = column
                var bestMargin = margin
                for (candidate in 0 until GameConfig.COLUMN_COUNT) {
                    if (candidate == column) continue
                    val candidateMargin = traffic.safeMargin(laneModel, candidate, time + shift + hop)
                    if (candidateMargin > bestMargin) {
                        bestMargin = candidateMargin
                        bestColumn = candidate
                    }
                }
                if (bestColumn != column) {
                    column = bestColumn
                    time += shift
                }
                waited = 0f
            }
        }
        return -1f
    }
}
