package com.darekbx.workouts

/**
 * Settings:
 *  - fast forward inverval e.g. +/-10s (10s, 20s, 30s, ...)
 *  - play speed increase e.g. +/-5% (5%, 10%, 20%, ...)
 *  - movies:
 *    - add new movie
 *    - select name
 *    - choose frame for preview
 *    - add time markers for training steps*
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
 *
 *  * - optional
 */

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.livedata.observeAsState
import com.darekbx.workouts.ui.navigation.BottomAppBar
import com.darekbx.workouts.ui.navigation.NavigationItem
import com.darekbx.workouts.ui.settings.SettingsScreen
import com.darekbx.workouts.ui.theme.WorkoutsTheme
import com.darekbx.workouts.ui.workouts.WorkoutsScreen
import com.darekbx.workouts.viewmodels.WorkoutsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val workoutsViewModel: WorkoutsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkoutsTheme {
                val navController = rememberNavController()
                Scaffold(
                    backgroundColor = Color.Black,
                    bottomBar = { BottomAppBar(navController) },
                    content = { Navigation(navController) }
                )
            }
        }
    }

    @Composable
    fun Navigation(navController: NavHostController) {
        NavHost(navController, startDestination = NavigationItem.Home.route) {
            composable(NavigationItem.Home.route) {

                val workouts = workoutsViewModel.workouts().observeAsState(listOf())
                WorkoutsScreen(
                    workouts,
                    onAdd = workoutsViewModel::add
                )

            }
            composable(NavigationItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
