package com.darekbx.workouts.ui.workouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.darekbx.workouts.data.dto.Workout
import java.util.*

@Composable
fun WorkoutsScreen(
    workouts: State<List<Workout>>,
    onAdd: (Workout) -> Unit
) {
    Workouts(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        workouts.value,
        onAdd
    )
}

@Composable
fun Workouts(
    modifier: Modifier,
    workouts: List<Workout>,
    onAdd: (Workout) -> Unit
) {
    Column(modifier = modifier) {
        Button(onClick = { onAdd(Workout(UUID.randomUUID().toString(), "Name", "path", 1000, 1, 1000, byteArrayOf())) }) {
            Text(text = "Add")
        }
        workouts.forEach {
            Text(text = "${it.name}")
        }
    }
}
