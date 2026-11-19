package com.Mohcasi3.Xurgox.core.di

import android.content.Context
import com.Mohcasi3.Xurgox.data.local.GamePreferences
import com.Mohcasi3.Xurgox.data.repository.ProgressRepositoryImpl
import com.Mohcasi3.Xurgox.data.repository.SettingsRepositoryImpl
import com.Mohcasi3.Xurgox.domain.usecase.GenerateCrossingUseCase
import com.Mohcasi3.Xurgox.domain.usecase.ResolveHopUseCase
import com.Mohcasi3.Xurgox.domain.usecase.ScoreRunUseCase
import com.Mohcasi3.Xurgox.domain.usecase.StepTrafficUseCase
import com.Mohcasi3.Xurgox.domain.usecase.UpdateMissionsUseCase
import com.Mohcasi3.Xurgox.domain.usecase.VerifyCrossingSolvableUseCase

object ServiceLocator {

    private var factoryInstance: AppViewModelFactory? = null

    fun init(context: Context) {
        if (factoryInstance != null) return
        val preferences = GamePreferences(context.applicationContext)
        val traffic = StepTrafficUseCase()
        val scoreRun = ScoreRunUseCase()
        val updateMissions = UpdateMissionsUseCase()
        val progressRepository = ProgressRepositoryImpl(preferences, scoreRun, updateMissions)
        val settingsRepository = SettingsRepositoryImpl(preferences)
        factoryInstance = AppViewModelFactory(
            progressRepository = progressRepository,
            settingsRepository = settingsRepository,
            generateCrossing = GenerateCrossingUseCase(VerifyCrossingSolvableUseCase(traffic)),
            stepTraffic = traffic,
            resolveHop = ResolveHopUseCase(traffic),
            scoreRun = scoreRun
        )
    }

    val viewModelFactory: AppViewModelFactory
        get() = checkNotNull(factoryInstance)
}
