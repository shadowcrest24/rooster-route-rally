package com.Mohcasi3.Xurgox.core.navigation

import com.Mohcasi3.Xurgox.domain.model.RunResult

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Game : Screen
    data class GameOver(val result: RunResult) : Screen
    data object Tutorial : Screen
    data object Levels : Screen
    data object Rewards : Screen
    data object Collection : Screen
    data object Missions : Screen
    data object Settings : Screen
}
