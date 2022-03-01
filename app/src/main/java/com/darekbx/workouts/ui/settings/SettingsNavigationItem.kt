package com.darekbx.workouts.ui.settings

sealed class SettingsNavigationItem(var route: String) {
    object Settings: SettingsNavigationItem("settings")
    object WorkoutsSettings: SettingsNavigationItem("workouts_settings")
    object AddWorkout: SettingsNavigationItem("add_workout")
    object PlaybackSettings: SettingsNavigationItem("playback_settings")
}
