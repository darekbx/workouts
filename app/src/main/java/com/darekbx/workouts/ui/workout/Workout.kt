package com.darekbx.workouts.ui.workout

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.workouts.viewmodels.WorkoutsViewModel

@Composable
fun WorkoutScreen(
    uuid: String,
    workoutsViewModel: WorkoutsViewModel = hiltViewModel()
) {
    Text(text = "Workout uuid: $uuid, playbackSpeed: ${workoutsViewModel.playbackSpeed}")
}