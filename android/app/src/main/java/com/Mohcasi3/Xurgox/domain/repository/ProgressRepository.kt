package com.Mohcasi3.Xurgox.domain.repository

import com.Mohcasi3.Xurgox.domain.model.PlayerProgress
import com.Mohcasi3.Xurgox.domain.model.RunResult
import com.Mohcasi3.Xurgox.domain.model.RunReward

interface ProgressRepository {
    fun progress(): PlayerProgress
    fun saveRun(result: RunResult): RunReward
    fun selectStage(stage: Int)
    fun claimTier(tierId: String, cost: Int): PlayerProgress
    fun collectMission(missionId: String, reward: Int): PlayerProgress
    fun reset()
}
