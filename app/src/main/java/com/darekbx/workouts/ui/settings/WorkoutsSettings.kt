package com.darekbx.workouts.ui.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.workouts.data.dto.Workout
import com.darekbx.workouts.viewmodels.WorkoutsViewModel

@Composable
fun WorkoutsSettings(
    workoutsViewModel: WorkoutsViewModel = hiltViewModel(),
    onAdd: () -> Unit
) {
    val workouts = workoutsViewModel.workouts().observeAsState(listOf())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onAdd() }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) {
        ListWorkounts(workouts = workouts.value)
    }

}

@Composable
fun ListWorkounts(modifier: Modifier = Modifier, workouts: List<Workout>) {
    LazyColumn(modifier) {
        items(workouts) { workout ->
            WorkoutItem(workout = workout)
        }
    }
}

@Composable
private fun WorkoutItem(modifier: Modifier = Modifier, workout: Workout) {
    Row(modifier.fillMaxWidth()) {

        Text(text = "${workout.name}")
        Text(text = "${workout.length}")
    }
}
