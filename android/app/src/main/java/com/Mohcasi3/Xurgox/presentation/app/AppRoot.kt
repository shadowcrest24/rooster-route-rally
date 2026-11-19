package com.Mohcasi3.Xurgox.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Mohcasi3.Xurgox.core.di.ServiceLocator
import com.Mohcasi3.Xurgox.core.navigation.Screen
import com.Mohcasi3.Xurgox.presentation.collection.CollectionScreen
import com.Mohcasi3.Xurgox.presentation.game.GameScreen
import com.Mohcasi3.Xurgox.presentation.gameover.GameOverScreen
import com.Mohcasi3.Xurgox.presentation.levels.LevelsScreen
import com.Mohcasi3.Xurgox.presentation.menu.MenuScreen
import com.Mohcasi3.Xurgox.presentation.missions.MissionsScreen
import com.Mohcasi3.Xurgox.presentation.rewards.RewardsScreen
import com.Mohcasi3.Xurgox.presentation.settings.SettingsScreen
import com.Mohcasi3.Xurgox.presentation.splash.SplashScreen
import com.Mohcasi3.Xurgox.presentation.tutorial.TutorialScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    AnimatedContent(
        targetState = screen,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            (slideInVertically(animationSpec = tween(320)) { height -> height / 6 } + fadeIn(tween(320)))
                .togetherWith(fadeOut(tween(220)))
        }
    ) { target ->
        when (target) {
            Screen.Splash -> SplashScreen(onFinished = { appViewModel.replaceRoot(Screen.Menu) })
            Screen.Menu -> MenuScreen(
                onPlay = { appViewModel.navigate(Screen.Game) },
                onLevels = { appViewModel.navigate(Screen.Levels) },
                onMissions = { appViewModel.navigate(Screen.Missions) },
                onRewards = { appViewModel.navigate(Screen.Rewards) },
                onCollection = { appViewModel.navigate(Screen.Collection) },
                onTutorial = { appViewModel.navigate(Screen.Tutorial) },
                onSettings = { appViewModel.navigate(Screen.Settings) }
            )
            Screen.Game -> GameScreen(
                onFinished = { result -> appViewModel.swap(Screen.GameOver(result)) },
                onMenu = { appViewModel.back() }
            )
            is Screen.GameOver -> GameOverScreen(
                result = target.result,
                onPlayAgain = { appViewModel.swap(Screen.Game) },
                onLevels = { appViewModel.swap(Screen.Levels) },
                onMenu = { appViewModel.back() }
            )
            Screen.Tutorial -> TutorialScreen(onMenu = { appViewModel.back() })
            Screen.Levels -> LevelsScreen(
                onMenu = { appViewModel.back() },
                onPlayStage = { appViewModel.swap(Screen.Game) }
            )
            Screen.Rewards -> RewardsScreen(onMenu = { appViewModel.back() })
            Screen.Collection -> CollectionScreen(onMenu = { appViewModel.back() })
            Screen.Missions -> MissionsScreen(onMenu = { appViewModel.back() })
            Screen.Settings -> SettingsScreen(onMenu = { appViewModel.back() })
        }
    }
}
