package com.darekbx.workouts.ui.settings

sealed class SettingsNavigationItem(var route: String) {
    object Settings: SettingsNavigationItem("settings")
    object MovieSettings: SettingsNavigationItem("movies_settings")
    object PlaybackSettings: SettingsNavigationItem("playback_settings")
}
