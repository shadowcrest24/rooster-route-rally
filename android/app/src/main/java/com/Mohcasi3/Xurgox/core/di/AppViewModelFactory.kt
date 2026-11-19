package com.Mohcasi3.Xurgox.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.Mohcasi3.Xurgox.domain.repository.ProgressRepository
import com.Mohcasi3.Xurgox.domain.repository.SettingsRepository
import com.Mohcasi3.Xurgox.domain.usecase.GenerateCrossingUseCase
import com.Mohcasi3.Xurgox.domain.usecase.ResolveHopUseCase
import com.Mohcasi3.Xurgox.domain.usecase.ScoreRunUseCase
import com.Mohcasi3.Xurgox.domain.usecase.StepTrafficUseCase
import com.Mohcasi3.Xurgox.presentation.app.AppViewModel
import com.Mohcasi3.Xurgox.presentation.collection.CollectionViewModel
import com.Mohcasi3.Xurgox.presentation.game.GameViewModel
import com.Mohcasi3.Xurgox.presentation.gameover.GameOverViewModel
import com.Mohcasi3.Xurgox.presentation.levels.LevelsViewModel
import com.Mohcasi3.Xurgox.presentation.menu.MenuViewModel
import com.Mohcasi3.Xurgox.presentation.missions.MissionsViewModel
import com.Mohcasi3.Xurgox.presentation.rewards.RewardsViewModel
import com.Mohcasi3.Xurgox.presentation.settings.SettingsViewModel
import com.Mohcasi3.Xurgox.presentation.splash.SplashViewModel
import com.Mohcasi3.Xurgox.presentation.tutorial.TutorialViewModel

class AppViewModelFactory(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val generateCrossing: GenerateCrossingUseCase,
    private val stepTraffic: StepTrafficUseCase,
    private val resolveHop: ResolveHopUseCase,
    private val scoreRun: ScoreRunUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(progressRepository, settingsRepository)
            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(progressRepository, settingsRepository, generateCrossing, stepTraffic, resolveHop, scoreRun)
            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(progressRepository)
            modelClass.isAssignableFrom(TutorialViewModel::class.java) ->
                TutorialViewModel(settingsRepository)
            modelClass.isAssignableFrom(LevelsViewModel::class.java) ->
                LevelsViewModel(progressRepository)
            modelClass.isAssignableFrom(RewardsViewModel::class.java) ->
                RewardsViewModel(progressRepository)
            modelClass.isAssignableFrom(CollectionViewModel::class.java) ->
                CollectionViewModel(progressRepository)
            modelClass.isAssignableFrom(MissionsViewModel::class.java) ->
                MissionsViewModel(progressRepository)
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(settingsRepository, progressRepository)
            else -> throw IllegalArgumentException(modelClass.name)
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }
}
