package com.Mohcasi3.Xurgox.data.local

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun readString(key: String, fallback: String): String = prefs.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun readSet(key: String): Set<String> = prefs.getStringSet(key, emptySet())?.toSet() ?: emptySet()

    fun writeSet(key: String, value: Set<String>) {
        prefs.edit().putStringSet(key, value).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val STORE_NAME = "rooster_route_rally_store"

        const val KEY_BEST_SCORE = "best_score"
        const val KEY_BEST_STREAK = "best_streak"
        const val KEY_ROUTE_POINTS = "route_points"
        const val KEY_STAGES_UNLOCKED = "stages_unlocked"
        const val KEY_SELECTED_STAGE = "selected_stage"
        const val KEY_BADGES = "collected_badges"
        const val KEY_CLAIMED = "claimed_tiers"
        const val KEY_MISSIONS_DONE = "missions_done"
        const val KEY_MISSION_PROGRESS = "mission_progress"
        const val KEY_STAGE_SCORES = "stage_scores"
        const val KEY_STAGES_CLEARED = "stages_cleared"
        const val KEY_HIGH_CONTRAST = "high_contrast"
        const val KEY_RHYTHM_HINTS = "rhythm_hints"
        const val KEY_CONTROL_SIDE = "control_side"
        const val KEY_TUTORIAL_SEEN = "tutorial_seen"
    }
}
