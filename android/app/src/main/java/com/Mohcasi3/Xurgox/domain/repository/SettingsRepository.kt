package com.Mohcasi3.Xurgox.domain.repository

import com.Mohcasi3.Xurgox.domain.model.ControlSide
import com.Mohcasi3.Xurgox.domain.model.GameSettings

interface SettingsRepository {
    fun settings(): GameSettings
    fun setHighContrast(value: Boolean)
    fun setRhythmHints(value: Boolean)
    fun setControlSide(side: ControlSide)
    fun setTutorialSeen(value: Boolean)
}
