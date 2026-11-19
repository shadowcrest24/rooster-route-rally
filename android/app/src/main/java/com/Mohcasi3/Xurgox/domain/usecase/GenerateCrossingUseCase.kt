package com.Mohcasi3.Xurgox.domain.usecase

import com.Mohcasi3.Xurgox.core.config.GameConfig
import com.Mohcasi3.Xurgox.domain.model.BoardCell
import com.Mohcasi3.Xurgox.domain.model.CrossingLayout
import com.Mohcasi3.Xurgox.domain.model.Lane
import com.Mohcasi3.Xurgox.domain.model.LaneDirection
import com.Mohcasi3.Xurgox.domain.model.Stage
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class GenerateCrossingUseCase(private val verify: VerifyCrossingSolvableUseCase) {

    fun seedFor(stageIndex: Int, attempt: Int): Long =
        (stageIndex.toLong() * 7919L) xor (attempt.toLong() * 104729L)

    operator fun invoke(stage: Stage, crossingIndex: Int, attempt: Int, rushTier: Int): CrossingLayout {
        val baseSeed = seedFor(stage.index, attempt * 31 + crossingIndex)
        for (retry in 0 until GameConfig.MAX_GENERATION_ATTEMPTS) {
            val seed = baseSeed + retry * 7717L
            val layout = build(stage, crossingIndex, rushTier, seed)
            if (verify.isSolvable(layout)) return layout
        }
        return fallback(baseSeed)
    }

    private fun build(stage: Stage, crossingIndex: Int, rushTier: Int, seed: Long): CrossingLayout {
        val random = Random(seed)
        val tier = min(rushTier, GameConfig.RUSH_TIER_MAX)
        var speedScale = stage.speedScale
        var spacingScale = stage.spacingScale
        for (step in 1 until tier) {
            speedScale *= GameConfig.RUSH_SPEED_STEP
            spacingScale *= GameConfig.RUSH_SPACING_STEP
        }
        val activeLanes = min(GameConfig.LANE_COUNT, stage.activeLanes + crossingIndex)
        val quietLanes = HashSet<Int>()
        while (quietLanes.size < GameConfig.LANE_COUNT - activeLanes) {
            quietLanes.add(1 + random.nextInt(GameConfig.LANE_COUNT))
        }
        val lanes = ArrayList<Lane>(GameConfig.LANE_COUNT)
        for (index in 1..GameConfig.LANE_COUNT) {
            val quiet = quietLanes.contains(index)
            val speed = ((0.9f + random.nextFloat() * 1.5f) * speedScale * (if (quiet) 0.6f else 1f))
                .coerceIn(0.5f, 3.4f)
            val raw = (2.6f + random.nextFloat() * 1.4f) * spacingScale * (if (quiet) 1.35f else 1f)
            val spacing = max(raw, MIN_GAP_SECONDS * speed + BODY_SPAN).coerceIn(2.2f, 6.4f)
            lanes.add(
                Lane(
                    index = index,
                    direction = if (random.nextBoolean()) LaneDirection.RIGHT else LaneDirection.LEFT,
                    speed = speed,
                    spacing = spacing,
                    phase = random.nextFloat()
                )
            )
        }
        val badges = ArrayList<BoardCell>(3)
        for (laneIndex in intArrayOf(2, 4, 6)) {
            if (random.nextFloat() < 0.78f) {
                badges.add(BoardCell(laneIndex, random.nextInt(GameConfig.COLUMN_COUNT)))
            }
        }
        val perches = ArrayList<BoardCell>(2)
        val perchColumn = random.nextInt(GameConfig.COLUMN_COUNT)
        perches.add(BoardCell(GameConfig.MEDIAN_LANE, perchColumn))
        return CrossingLayout(
            seed = seed,
            lanes = lanes,
            badges = badges.filter { cell -> perches.none { it.lane == cell.lane && it.column == cell.column } },
            perches = perches,
            fallback = false
        )
    }

    private fun fallback(seed: Long): CrossingLayout {
        val lanes = (1..GameConfig.LANE_COUNT).map { index ->
            Lane(
                index = index,
                direction = if (index % 2 == 0) LaneDirection.LEFT else LaneDirection.RIGHT,
                speed = 1.0f,
                spacing = 4.2f,
                phase = (index * 0.17f) % 1f
            )
        }
        return CrossingLayout(
            seed = seed,
            lanes = lanes,
            badges = listOf(BoardCell(2, 1), BoardCell(6, 2)),
            perches = listOf(BoardCell(GameConfig.MEDIAN_LANE, 0)),
            fallback = true
        )
    }
}

private const val MIN_GAP_SECONDS = 0.95f
private const val BODY_SPAN = 2f * (GameConfig.VEHICLE_HALF_WIDTH + GameConfig.ROOSTER_HALF_WIDTH)
