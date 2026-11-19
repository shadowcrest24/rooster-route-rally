package com.Mohcasi3.Xurgox.domain.usecase

import com.Mohcasi3.Xurgox.core.config.GameConfig
import com.Mohcasi3.Xurgox.domain.model.Lane
import com.Mohcasi3.Xurgox.domain.model.LaneDirection
import kotlin.math.ceil
import kotlin.math.floor

class StepTrafficUseCase {

    private val overlap = GameConfig.VEHICLE_HALF_WIDTH + GameConfig.ROOSTER_HALF_WIDTH

    fun vehicleCenters(lane: Lane, timeSec: Float): List<Float> {
        val progress = fraction(lane.phase + timeSec * lane.speed / lane.spacing)
        val count = ceil((GameConfig.SPAN_END - GameConfig.SPAN_START) / lane.spacing).toInt() + 1
        val centers = ArrayList<Float>(count)
        for (k in 0 until count) {
            val travelled = (progress + k) * lane.spacing
            val x = if (lane.direction == LaneDirection.RIGHT) {
                GameConfig.SPAN_START + travelled
            } else {
                GameConfig.SPAN_END - travelled
            }
            if (x >= GameConfig.SPAN_START - lane.spacing && x <= GameConfig.SPAN_END + lane.spacing) {
                centers.add(x)
            }
        }
        return centers
    }

    fun isBlocked(lane: Lane, column: Int, timeSec: Float): Boolean =
        phaseWithinBody(lane, column, timeSec) < bodyWidth(lane)

    fun secondsUntilBlocked(lane: Lane, column: Int, timeSec: Float): Float {
        val width = bodyWidth(lane)
        val offset = phaseWithinBody(lane, column, timeSec)
        if (offset < width) return 0f
        val period = lane.spacing / lane.speed
        return (1f - offset) * period
    }

    fun safeMargin(lane: Lane, column: Int, timeSec: Float): Float =
        if (isBlocked(lane, column, timeSec)) 0f else secondsUntilBlocked(lane, column, timeSec)

    private fun bodyWidth(lane: Lane): Float = (2f * overlap) / lane.spacing

    private fun phaseWithinBody(lane: Lane, column: Int, timeSec: Float): Float {
        val columnCenter = column + 0.5f
        val entry = if (lane.direction == LaneDirection.RIGHT) {
            (columnCenter - overlap - GameConfig.SPAN_START) / lane.spacing
        } else {
            (GameConfig.SPAN_END - columnCenter - overlap) / lane.spacing
        }
        val progress = lane.phase + timeSec * lane.speed / lane.spacing
        return fraction(progress - entry)
    }

    private fun fraction(value: Float): Float {
        val f = value - floor(value)
        return if (f < 0f) f + 1f else f
    }
}
