package com.darekbx.workouts.ui.navigation

import com.darekbx.workouts.R

sealed class NavigationItem(
    var route: String,
    var labelResId: Int,
    val iconResId: Int,
    var arguments: Map<String, Any> = emptyMap()
) {
    object Home: NavigationItem("home", R.string.home, R.drawable.ic_home)
    object Settings: NavigationItem("settings", R.string.settings, R.drawable.ic_settings)
    object Workout: NavigationItem("workout", R.string.home, R.drawable.ic_home)
}
