package com.darekbx.workouts.ui.workouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.darekbx.workouts.data.dto.Workout

@Composable
fun WorkoutsScreen(
    workouts: State<List<Workout>>
) {
    Workouts(modifier = Modifier.fillMaxWidth().fillMaxHeight())
}

@Composable
fun Workouts(modifier: Modifier) {
    Column(modifier = modifier) {
        (0 until 10).forEach {
            Text(text = "$it")
        }
    }
}
