package com.Mohcasi3.Xurgox.data.repository

import com.Mohcasi3.Xurgox.data.local.GamePreferences
import com.Mohcasi3.Xurgox.domain.model.ControlSide
import com.Mohcasi3.Xurgox.domain.model.GameSettings
import com.Mohcasi3.Xurgox.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val preferences: GamePreferences) : SettingsRepository {

    override fun settings(): GameSettings = GameSettings(
        highContrast = preferences.readBoolean(GamePreferences.KEY_HIGH_CONTRAST, false),
        rhythmHints = preferences.readBoolean(GamePreferences.KEY_RHYTHM_HINTS, true),
        controlSide = runCatching {
            ControlSide.valueOf(preferences.readString(GamePreferences.KEY_CONTROL_SIDE, ControlSide.RIGHT.name))
        }.getOrDefault(ControlSide.RIGHT),
        tutorialSeen = preferences.readBoolean(GamePreferences.KEY_TUTORIAL_SEEN, false)
    )

    override fun setHighContrast(value: Boolean) {
        preferences.writeBoolean(GamePreferences.KEY_HIGH_CONTRAST, value)
    }

    override fun setRhythmHints(value: Boolean) {
        preferences.writeBoolean(GamePreferences.KEY_RHYTHM_HINTS, value)
    }

    override fun setControlSide(side: ControlSide) {
        preferences.writeString(GamePreferences.KEY_CONTROL_SIDE, side.name)
    }

    override fun setTutorialSeen(value: Boolean) {
        preferences.writeBoolean(GamePreferences.KEY_TUTORIAL_SEEN, value)
    }
}
