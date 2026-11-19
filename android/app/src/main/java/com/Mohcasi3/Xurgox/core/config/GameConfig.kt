package com.Mohcasi3.Xurgox.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L

    const val LANE_COUNT = 7
    const val COLUMN_COUNT = 3
    const val ROW_COUNT = LANE_COUNT + 2
    const val GOAL_LANE = LANE_COUNT + 1
    const val MEDIAN_LANE = 4
    const val CROSSINGS_PER_STAGE = 3
    const val STAGE_COUNT = 4

    const val CROSSING_TIME_MS = 60_000L
    const val TICK_MS = 16L
    const val HOP_DURATION_MS = 220L
    const val SHIFT_DURATION_MS = 160L
    const val DASH_DURATION_MS = 300L
    const val DASH_LANES = 2

    const val SAFE_WINDOW_MS = 650L
    const val NEAR_MISS_WINDOW_MS = 1100L
    const val FATAL_MARGIN_MS = 150L

    const val TIME_PER_LANE_MS = 2000L
    const val TIME_PER_BADGE_MS = 4000L
    const val TIME_BANK_X2_MS = 4000L
    const val TIME_BANK_X3_MS = 6000L

    const val SCORE_PER_LANE = 60
    const val SCORE_PER_CROSSING = 500
    const val SCORE_PER_BADGE = 120
    const val SCORE_PER_BANK = 200
    const val SCORE_PER_SECOND = 10
    const val HARD_MODE_MULTIPLIER = 1.25f

    const val MAX_STREAK = 3
    const val STREAK_IDLE_SEC = 6f
    const val DASH_CHARGE_COST = 3
    const val RUSH_TIER_MAX = 3
    const val RUSH_SPEED_STEP = 1.18f
    const val RUSH_SPACING_STEP = 0.88f

    const val BADGES_PER_STAGE = 6
    const val TOTAL_BADGES = 24
    const val ROUTE_POINT_DIVISOR = 20

    const val VEHICLE_HALF_WIDTH = 0.42f
    const val ROOSTER_HALF_WIDTH = 0.30f
    const val SPAN_START = -1.2f
    const val SPAN_END = COLUMN_COUNT + 1.2f

    const val SOLVE_LIMIT_MS = 45_000L
    const val MAX_GENERATION_ATTEMPTS = 20

    const val HINT_VISIBLE_MS = 5000L
    const val CELEBRATION_MS = 900L
    const val FLASH_MS = 350L
    const val GAIN_LABEL_MS = 700L
}
