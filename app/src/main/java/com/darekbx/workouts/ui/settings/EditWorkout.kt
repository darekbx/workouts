package com.darekbx.workouts.ui.settings

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun EditWorkout(workoutUid: String? = null) {

}

@Preview
@Composable
fun WorkoutName(modifier: Modifier = Modifier, name: String? = null) {
    val text by remember { mutableStateOf(name ?: "") }
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = { },
        label = { Text("Label") }
    )
}
