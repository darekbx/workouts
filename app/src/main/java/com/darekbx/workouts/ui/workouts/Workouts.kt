package com.darekbx.workouts.ui.workouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.darekbx.workouts.data.dto.Workout

@Composable
fun WorkoutsScreen(
    workouts: State<List<Workout>>
) {
    Workouts(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        (0..34).map { Workout("", "name $it", "", 0, 0, 0, byteArrayOf()) }
    )
}

@Composable
fun Workouts(
    modifier: Modifier,
    workouts: List<Workout>
) {
    LazyColumn(modifier = modifier) {
        items(workouts) {
            Text(text = "${it.name}")
        }
    }
}
