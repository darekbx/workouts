package com.darekbx.workouts.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darekbx.workouts.ui.theme.Typography

@ExperimentalComposeUiApi
@Composable
fun SettingsScreen() {
    val navController = rememberNavController()
    SettingsNavigation(navController)
}

@ExperimentalComposeUiApi
@Composable
fun SettingsNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = SettingsNavigationItem.Settings.route) {
        composable(SettingsNavigationItem.Settings.route) {
            SettingsMenu(navController)
        }
        composable(SettingsNavigationItem.WorkoutsSettings.route) {
            WorkoutsSettings(onAdd = { navController.navigate(SettingsNavigationItem.AddWorkout.route) })
        }
        composable(SettingsNavigationItem.PlaybackSettings.route) {
            PlaybackSettings()
        }
        composable(SettingsNavigationItem.AddWorkout.route) {
            // TODO pass workout uid to edit
            EditWorkout(onCompleted = { navController.navigate(SettingsNavigationItem.WorkoutsSettings.route)  })
        }
    }
}

@Composable
private fun SettingsMenu(navController: NavHostController) {
    Column(Modifier.padding(16.dp)) {
        CreateButton(
            Modifier.fillMaxWidth(),
            name = "Workouts",
            description = "Add or delete workouts"
        ) {
            navController.navigate(SettingsNavigationItem.WorkoutsSettings.route)
        }
        CreateButton(
            Modifier.fillMaxWidth(),
            name = "Playback options",
            description = "Define playback speed or fastforward interval"
        ) {
            navController.navigate(SettingsNavigationItem.PlaybackSettings.route)
        }
    }
}

@Composable
private fun CreateButton(
    modifier: Modifier = Modifier,
    name: String,
    description: String,
    onClick: () -> Unit
) {
    Column(
        modifier.clickable { onClick() }
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = name,
            style = Typography.body1,
            color = Color.White
        )
        Text(
            text = description,
            style = Typography.h5,
            color = Color.Gray
        )
    }
}
