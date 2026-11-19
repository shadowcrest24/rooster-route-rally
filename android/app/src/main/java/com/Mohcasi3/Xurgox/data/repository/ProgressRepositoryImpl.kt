package com.Mohcasi3.Xurgox.data.repository

import com.Mohcasi3.Xurgox.core.config.GameConfig
import com.Mohcasi3.Xurgox.data.local.GamePreferences
import com.Mohcasi3.Xurgox.data.sample.SampleData
import com.Mohcasi3.Xurgox.domain.model.PlayerProgress
import com.Mohcasi3.Xurgox.domain.model.RunResult
import com.Mohcasi3.Xurgox.domain.model.RunReward
import com.Mohcasi3.Xurgox.domain.repository.ProgressRepository
import com.Mohcasi3.Xurgox.domain.usecase.ScoreRunUseCase
import com.Mohcasi3.Xurgox.domain.usecase.UpdateMissionsUseCase
import kotlin.math.max
import kotlin.math.min

class ProgressRepositoryImpl(
    private val preferences: GamePreferences,
    private val scoreRun: ScoreRunUseCase,
    private val updateMissions: UpdateMissionsUseCase
) : ProgressRepository {

    override fun progress(): PlayerProgress = PlayerProgress(
        bestScore = preferences.readInt(GamePreferences.KEY_BEST_SCORE, 0),
        bestStreak = preferences.readInt(GamePreferences.KEY_BEST_STREAK, 0),
        routePoints = preferences.readInt(GamePreferences.KEY_ROUTE_POINTS, 0),
        stagesUnlocked = preferences.readInt(GamePreferences.KEY_STAGES_UNLOCKED, 1)
            .coerceIn(1, GameConfig.STAGE_COUNT),
        selectedStage = preferences.readInt(GamePreferences.KEY_SELECTED_STAGE, 1)
            .coerceIn(1, GameConfig.STAGE_COUNT),
        collectedBadgeIds = preferences.readSet(GamePreferences.KEY_BADGES),
        claimedTierIds = preferences.readSet(GamePreferences.KEY_CLAIMED),
        completedMissionIds = preferences.readSet(GamePreferences.KEY_MISSIONS_DONE),
        missionProgress = decodeCounts(preferences.readString(GamePreferences.KEY_MISSION_PROGRESS, "")),
        stageBestScores = decodeCounts(preferences.readString(GamePreferences.KEY_STAGE_SCORES, ""))
            .mapKeys { entry -> entry.key.toIntOrNull() ?: 0 },
        clearedStages = preferences.readSet(GamePreferences.KEY_STAGES_CLEARED)
            .mapNotNull { it.toIntOrNull() }
            .toSet()
    )

    override fun saveRun(result: RunResult): RunReward {
        val current = progress()
        val newBest = result.score > current.bestScore
        val earnedPoints = scoreRun.routePoints(result.score)
        val badges = current.collectedBadgeIds + result.collectedBadgeIds
        val cleared = if (result.stageCleared) current.clearedStages + result.stage else current.clearedStages
        val unlocked = if (result.stageCleared) {
            min(GameConfig.STAGE_COUNT, max(current.stagesUnlocked, result.stage + 1))
        } else {
            current.stagesUnlocked
        }
        val stageScores = HashMap(current.stageBestScores)
        stageScores[result.stage] = max(stageScores[result.stage] ?: 0, result.score)
        val missions = updateMissions.apply(SampleData.missions, current, result)

        preferences.writeInt(GamePreferences.KEY_BEST_SCORE, max(current.bestScore, result.score))
        preferences.writeInt(GamePreferences.KEY_BEST_STREAK, max(current.bestStreak, result.bestStreak))
        preferences.writeInt(GamePreferences.KEY_ROUTE_POINTS, current.routePoints + earnedPoints)
        preferences.writeInt(GamePreferences.KEY_STAGES_UNLOCKED, unlocked)
        preferences.writeSet(GamePreferences.KEY_BADGES, badges)
        preferences.writeSet(GamePreferences.KEY_STAGES_CLEARED, cleared.map { it.toString() }.toSet())
        preferences.writeString(
            GamePreferences.KEY_STAGE_SCORES,
            encodeCounts(stageScores.mapKeys { entry -> entry.key.toString() })
        )
        preferences.writeString(GamePreferences.KEY_MISSION_PROGRESS, encodeCounts(missions.first))

        return RunReward(
            progress = progress(),
            routePointsEarned = earnedPoints,
            newBest = newBest,
            missionGains = missions.second
        )
    }

    override fun selectStage(stage: Int) {
        preferences.writeInt(
            GamePreferences.KEY_SELECTED_STAGE,
            stage.coerceIn(1, GameConfig.STAGE_COUNT)
        )
    }

    override fun claimTier(tierId: String, cost: Int): PlayerProgress {
        val current = progress()
        if (current.claimedTierIds.contains(tierId) || current.routePoints < cost) return current
        preferences.writeInt(GamePreferences.KEY_ROUTE_POINTS, current.routePoints - cost)
        preferences.writeSet(GamePreferences.KEY_CLAIMED, current.claimedTierIds + tierId)
        return progress()
    }

    override fun collectMission(missionId: String, reward: Int): PlayerProgress {
        val current = progress()
        if (current.completedMissionIds.contains(missionId)) return current
        preferences.writeInt(GamePreferences.KEY_ROUTE_POINTS, current.routePoints + reward)
        preferences.writeSet(GamePreferences.KEY_MISSIONS_DONE, current.completedMissionIds + missionId)
        return progress()
    }

    override fun reset() {
        val settings = listOf(
            GamePreferences.KEY_HIGH_CONTRAST,
            GamePreferences.KEY_RHYTHM_HINTS,
            GamePreferences.KEY_CONTROL_SIDE,
            GamePreferences.KEY_TUTORIAL_SEEN
        )
        val highContrast = preferences.readBoolean(settings[0], false)
        val hints = preferences.readBoolean(settings[1], true)
        val side = preferences.readString(settings[2], "RIGHT")
        val tutorial = preferences.readBoolean(settings[3], false)
        preferences.clearAll()
        preferences.writeBoolean(settings[0], highContrast)
        preferences.writeBoolean(settings[1], hints)
        preferences.writeString(settings[2], side)
        preferences.writeBoolean(settings[3], tutorial)
    }

    private fun encodeCounts(values: Map<String, Int>): String =
        values.entries.joinToString(";") { "${it.key}:${it.value}" }

    private fun decodeCounts(raw: String): Map<String, Int> {
        if (raw.isBlank()) return emptyMap()
        val result = HashMap<String, Int>()
        for (chunk in raw.split(";")) {
            val parts = chunk.split(":")
            if (parts.size != 2) continue
            val value = parts[1].toIntOrNull() ?: continue
            result[parts[0]] = value
        }
        return result
    }
}
