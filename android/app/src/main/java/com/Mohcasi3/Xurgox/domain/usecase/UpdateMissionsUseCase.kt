package com.Mohcasi3.Xurgox.domain.usecase

import com.Mohcasi3.Xurgox.domain.model.Mission
import com.Mohcasi3.Xurgox.domain.model.MissionGain
import com.Mohcasi3.Xurgox.domain.model.MissionKind
import com.Mohcasi3.Xurgox.domain.model.PlayerProgress
import com.Mohcasi3.Xurgox.domain.model.RunResult
import kotlin.math.max
import kotlin.math.min

class UpdateMissionsUseCase {

    fun apply(
        pool: List<Mission>,
        progress: PlayerProgress,
        result: RunResult
    ): Pair<Map<String, Int>, List<MissionGain>> {
        val updated = HashMap(progress.missionProgress)
        val gains = ArrayList<MissionGain>()
        for (mission in pool) {
            if (progress.completedMissionIds.contains(mission.id)) continue
            val before = updated[mission.id] ?: 0
            val after = min(mission.target, advance(mission, before, result))
            if (after != before) {
                updated[mission.id] = after
                gains.add(MissionGain(mission.id, after, mission.target, after >= mission.target))
            }
        }
        return updated to gains
    }

    private fun advance(mission: Mission, before: Int, result: RunResult): Int = when (mission.kind) {
        MissionKind.BADGES -> before + result.badges
        MissionKind.LANES_IN_RUN -> max(before, result.lanes)
        MissionKind.TIME_LEFT -> max(before, result.secondsLeft)
        MissionKind.STREAK -> max(before, result.bestStreak)
        MissionKind.DASH -> before + result.dashesUsed
        MissionKind.CLEAR_STAGE -> if (result.stageCleared && result.stage >= mission.target) mission.target else before
    }
}
