package com.Mohcasi3.Xurgox.domain.usecase

import com.Mohcasi3.Xurgox.core.config.GameConfig
import kotlin.math.roundToInt

class ScoreRunUseCase {

    fun finalScore(earnedPoints: Int, secondsLeft: Int, hardMode: Boolean): Int {
        val raw = earnedPoints + secondsLeft * GameConfig.SCORE_PER_SECOND
        val scaled = if (hardMode) raw * GameConfig.HARD_MODE_MULTIPLIER else raw.toFloat()
        return scaled.roundToInt()
    }

    fun routePoints(score: Int): Int = score / GameConfig.ROUTE_POINT_DIVISOR

    fun badgeValue(streak: Int): Int = GameConfig.SCORE_PER_BADGE * streak

    fun bankValue(streak: Int): Int = GameConfig.SCORE_PER_BANK * streak

    fun bankTimeMs(streak: Int): Long = when {
        streak >= GameConfig.MAX_STREAK -> GameConfig.TIME_BANK_X3_MS
        streak == 2 -> GameConfig.TIME_BANK_X2_MS
        else -> 0L
    }
}
