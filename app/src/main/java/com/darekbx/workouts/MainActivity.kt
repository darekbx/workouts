package com.darekbx.workouts

/**
 *
 * Check if AirPods are connected
 *
 * Settings:
 *  - fast forward inverval e.g. +/-10s (10s, 20s, 30s, ...)
 *  - play speed increase e.g. +/-5% (5%, 10%, 20%, ...)
 *
 * First screen:
 *  - list of the trainings:
 *    - 'back'
 *    - 'neck'
 *    - 'overall'?
 *  - each item:
 *    - when last played (e.g. 2d ago)
 *    - how many times played
 *    - image (selected one frame of the movie)
 *
 * Training screen:
 *  - movie playback
 *  - progress bar with time
 *  - pause button
 *  - -10s, +10s buttons (configurable in settings)
 *  - play speed buttons e.g. -5%, +5% (configurable in settings)
 *  - play speed is displayed
 *  - training step skip/redo*
 */

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.darekbx.workouts.ui.navigation.BottomAppBar
import com.darekbx.workouts.ui.navigation.NavigationItem
import com.darekbx.workouts.ui.settings.SettingsScreen
import com.darekbx.workouts.ui.theme.WorkoutsTheme
import com.darekbx.workouts.ui.workout.WorkoutScreen
import com.darekbx.workouts.ui.workouts.WorkoutsScreen
import com.darekbx.workouts.viewmodels.WorkoutsViewModel
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val workoutsViewModel: WorkoutsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkoutsTheme {
                val navController = rememberNavController()
                Scaffold(
                    backgroundColor = MaterialTheme.colors.background,
                    bottomBar = { BottomAppBar(navController) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Navigation(navController)
                    }
                }
            }
        }
    }

    @Composable
    fun Navigation(navController: NavHostController) {
        NavHost(navController, startDestination = NavigationItem.Home.route) {
            composable(NavigationItem.Home.route) {
                val workouts = workoutsViewModel.workouts().observeAsState(listOf())
                WorkoutsScreen(workouts) {
                    val args = mapOf("uuid" to it.uid)
                    navController.navigate(NavigationItem.Workout.apply { arguments = args }.route)
                }
            }
            composable(NavigationItem.Workout.route) {
                WorkoutScreen(NavigationItem.Workout.arguments["uuid"] as String)
            }
            composable(NavigationItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
