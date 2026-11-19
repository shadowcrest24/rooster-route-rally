package com.Mohcasi3.Xurgox.presentation.app

import androidx.lifecycle.ViewModel
import com.Mohcasi3.Xurgox.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = ArrayDeque<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun navigate(target: Screen) {
        backStack.addLast(_screen.value)
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun replaceRoot(target: Screen) {
        backStack.clear()
        _screen.value = target
        _canGoBack.value = false
    }

    fun swap(target: Screen) {
        _screen.value = target
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
        _canGoBack.value = backStack.isNotEmpty()
    }
}
